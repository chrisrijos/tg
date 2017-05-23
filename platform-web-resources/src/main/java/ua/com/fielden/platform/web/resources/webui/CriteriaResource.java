package ua.com.fielden.platform.web.resources.webui;

import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;
import static ua.com.fielden.platform.streaming.ValueCollectors.toLinkedHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import ua.com.fielden.platform.criteria.generator.ICriteriaGenerator;
import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.dao.ISessionEnabled;
import ua.com.fielden.platform.dao.annotations.SessionRequired;
import ua.com.fielden.platform.dao.exceptions.EntityCompanionException;
import ua.com.fielden.platform.data.generator.IGenerator;
import ua.com.fielden.platform.domaintree.IGlobalDomainTreeManager;
import ua.com.fielden.platform.domaintree.IServerGlobalDomainTreeManager;
import ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeManager.ICentreDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.domaintree.impl.CalculatedProperty;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.factory.ICompanionObjectFinder;
import ua.com.fielden.platform.entity.functional.centre.CentreContextHolder;
import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.entity_centre.review.criteria.EnhancedCentreEntityQueryCriteria;
import ua.com.fielden.platform.error.Result;
import ua.com.fielden.platform.security.user.IUserProvider;
import ua.com.fielden.platform.ui.menu.MiWithConfigurationSupport;
import ua.com.fielden.platform.utils.EntityUtils;
import ua.com.fielden.platform.utils.Pair;
import ua.com.fielden.platform.web.app.IWebUiConfig;
import ua.com.fielden.platform.web.centre.CentreContext;
import ua.com.fielden.platform.web.centre.CentreUpdater;
import ua.com.fielden.platform.web.centre.EntityCentre;
import ua.com.fielden.platform.web.centre.IQueryEnhancer;
import ua.com.fielden.platform.web.centre.api.EntityCentreConfig.ResultSetProp;
import ua.com.fielden.platform.web.centre.api.context.CentreContextConfig;
import ua.com.fielden.platform.web.centre.api.resultset.ICustomPropsAssignmentHandler;
import ua.com.fielden.platform.web.centre.api.resultset.IRenderingCustomiser;
import ua.com.fielden.platform.web.centre.api.resultset.PropDef;
import ua.com.fielden.platform.web.factories.webui.ResourceFactoryUtils;
import ua.com.fielden.platform.web.resources.RestServerUtil;
import ua.com.fielden.platform.web.utils.EntityResourceUtils;

/**
 * The web resource for criteria serves as a back-end mechanism of criteria retrieval. It provides a base implementation for handling the following methods:
 * <ul>
 * <li>retrieve entity -- GET request.
 * </ul>
 *
 * @author TG Team
 *
 */
public class CriteriaResource extends ServerResource {
    private final static Logger logger = Logger.getLogger(CriteriaResource.class);

    private final static String staleCriteriaMessage = "Selection criteria have been changed, but not applied. "
                                                     + "Previously applied values are in effect. "
                                                     + "Please tap action <b>RUN</b> to apply the updated selection criteria.";

    private final RestServerUtil restUtil;
    private final ICompanionObjectFinder companionFinder;

    private final ICriteriaGenerator critGenerator;
    private final EntityCentre<AbstractEntity<?>> centre;

    private final IWebUiConfig webUiConfig;
    private final IServerGlobalDomainTreeManager serverGdtm;
    private final IUserProvider userProvider;
    private final EntityFactory entityFactory;

    public CriteriaResource(
            final RestServerUtil restUtil,  
            final EntityCentre<AbstractEntity<?>> centre,
            final IWebUiConfig webUiConfig,
            final ICompanionObjectFinder companionFinder,
            final IServerGlobalDomainTreeManager serverGdtm,
            final IUserProvider userProvider,
            final ICriteriaGenerator critGenerator,
            final EntityFactory entityFactory,
            final Context context,
            final Request request,
            final Response response) {
        init(context, request, response);

        this.restUtil = restUtil;
        this.companionFinder = companionFinder;

        this.centre = centre;
        this.critGenerator = critGenerator;

        this.webUiConfig = webUiConfig;
        this.serverGdtm = serverGdtm;
        this.userProvider = userProvider;
        this.entityFactory = entityFactory;
    }

    /**
     * Handles GET requests resulting from tg-selection-criteria <code>retrieve()</code> method (new entity).
     */
    @Get
    @Override
    public Representation get() throws ResourceException {
        return EntityResourceUtils.handleUndesiredExceptions(getResponse(), () -> {
            final Class<? extends MiWithConfigurationSupport<?>> miType = centre.getMenuItemType();
            final IGlobalDomainTreeManager gdtm = ResourceFactoryUtils.getUserSpecificGlobalManager(serverGdtm, userProvider);
            final ICentreDomainTreeManagerAndEnhancer updatedFreshCentre = CentreUpdater.updateCentre(gdtm, miType, CentreUpdater.FRESH_CENTRE_NAME);
            // NOTE: the following line can be the example how 'criteria retrieval' server errors manifest to the client application
            // throw new IllegalStateException("Illegal state during criteria retrieval.");
            return createCriteriaRetrievalEnvelope(updatedFreshCentre, miType, gdtm, restUtil, companionFinder, critGenerator);
        }, restUtil);
    }

    /**
     * Handles POST request resulting from tg-selection-criteria <code>validate()</code> method.
     */
    @Post
    @Override
    public Representation post(final Representation envelope) {
        return EntityResourceUtils.handleUndesiredExceptions(getResponse(), () -> {
            return createCriteriaValidationEnvelope(
                    EntityResourceUtils.restoreModifiedPropertiesHolderFrom(envelope, restUtil), 
                    centre.getMenuItemType(), 
                    ResourceFactoryUtils.getUserSpecificGlobalManager(serverGdtm, userProvider), 
                    restUtil, 
                    companionFinder, 
                    critGenerator);
        }, restUtil);
    }

    public static Representation createCriteriaRetrievalEnvelope(
            final ICentreDomainTreeManagerAndEnhancer updatedFreshCentre,
            final Class<? extends MiWithConfigurationSupport<?>> miType,
            final IGlobalDomainTreeManager gdtm,
            final RestServerUtil restUtil,
            final ICompanionObjectFinder companionFinder,
            final ICriteriaGenerator critGenerator
                    ) {
        return restUtil.rawListJSONRepresentation(
                CentreResourceUtils.createCriteriaValidationPrototype(miType, updatedFreshCentre, critGenerator, -1L, gdtm),
                CentreResourceUtils.createCriteriaMetaValuesCustomObject(
                        CentreResourceUtils.createCriteriaMetaValues(updatedFreshCentre, CentreResourceUtils.getEntityType(miType)),
                        CentreResourceUtils.isFreshCentreChanged(updatedFreshCentre, CentreUpdater.updateCentre(gdtm, miType, CentreUpdater.SAVED_CENTRE_NAME))
                )//
        );
    }

    public static Representation createCriteriaDiscardEnvelope(
            final ICentreDomainTreeManagerAndEnhancer updatedFreshCentre,
            final Class<? extends MiWithConfigurationSupport<?>> miType,
            final IGlobalDomainTreeManager gdtm,
            final RestServerUtil restUtil,
            final ICompanionObjectFinder companionFinder,
            final ICriteriaGenerator critGenerator,
            final String staleCriteriaMessage
                    ) {
        return restUtil.rawListJSONRepresentation(
                CentreResourceUtils.createCriteriaValidationPrototype(miType, updatedFreshCentre, critGenerator, -1L, gdtm),
                CentreResourceUtils.createCriteriaMetaValuesCustomObject(
                        CentreResourceUtils.createCriteriaMetaValues(updatedFreshCentre, CentreResourceUtils.getEntityType(miType)),
                        CentreResourceUtils.isFreshCentreChanged(updatedFreshCentre, CentreUpdater.updateCentre(gdtm, miType, CentreUpdater.SAVED_CENTRE_NAME)),
                        staleCriteriaMessage
                )//
        );
    }

    private static Representation createCriteriaValidationEnvelope(
            final Map<String, Object> modifiedPropertiesHolder,
            final Class<? extends MiWithConfigurationSupport<?>> miType,
            final IGlobalDomainTreeManager gdtm,
            final RestServerUtil restUtil,
            final ICompanionObjectFinder companionFinder,
            final ICriteriaGenerator critGenerator
                    ) {
        final EnhancedCentreEntityQueryCriteria<AbstractEntity<?>, ? extends IEntityDao<AbstractEntity<?>>> appliedCriteriaEntity = CentreResourceUtils.createCriteriaEntity(modifiedPropertiesHolder, companionFinder, critGenerator, miType, gdtm);
        final ICentreDomainTreeManagerAndEnhancer updatedFreshCentre = appliedCriteriaEntity.getCentreDomainTreeMangerAndEnhancer();
        return restUtil.rawListJSONRepresentation(
                appliedCriteriaEntity,
                CentreResourceUtils.createCriteriaMetaValuesCustomObject(
                        CentreResourceUtils.createCriteriaMetaValues(updatedFreshCentre, CentreResourceUtils.getEntityType(miType)),
                        CentreResourceUtils.isFreshCentreChanged(updatedFreshCentre, CentreUpdater.updateCentre(gdtm, miType, CentreUpdater.SAVED_CENTRE_NAME)),
                        createStaleCriteriaMessage((String) modifiedPropertiesHolder.get("@@wasRun"), updatedFreshCentre, miType, gdtm, companionFinder, critGenerator)
                )//
        );
    }

    public static <T extends AbstractEntity<?>, M extends EnhancedCentreEntityQueryCriteria<T, ? extends IEntityDao<T>>> String createStaleCriteriaMessage(final String wasRun, final ICentreDomainTreeManagerAndEnhancer freshCentre, final Class<? extends MiWithConfigurationSupport<?>> miType, final IGlobalDomainTreeManager gdtm, final ICompanionObjectFinder companionFinder, final ICriteriaGenerator critGenerator) {
        if (wasRun != null) {
            final boolean isCriteriaStale = !EntityUtils.equalsEx(CentreUpdater.updateCentre(gdtm, miType, CentreUpdater.PREVIOUSLY_RUN_CENTRE_NAME), freshCentre);
            if (isCriteriaStale) {
                logger.info(staleCriteriaMessage);
                return staleCriteriaMessage;
            }
        }
        return null;
    }
    
    /**
     * Handles PUT request resulting from tg-selection-criteria <code>run()</code> method.
     */
    @SuppressWarnings("unchecked")
    @Put
    @Override
    public Representation put(final Representation envelope) {
        return EntityResourceUtils.handleUndesiredExceptions(getResponse(), () -> {
            logger.debug("CRITERIA_RESOURCE: run started.");
            //            // NOTE: the following line can be the example how 'centre running' server errors manifest to the client application
            //            throw new IllegalStateException("Illegal state during centre running.");
            final Class<? extends MiWithConfigurationSupport<?>> miType = centre.getMenuItemType();
            final CentreContextHolder centreContextHolder = EntityResourceUtils.restoreCentreContextHolder(envelope, restUtil);

            final Map<String, Object> customObject = new LinkedHashMap<String, Object>(centreContextHolder.getCustomObject());

            final IGlobalDomainTreeManager gdtm = ResourceFactoryUtils.getUserSpecificGlobalManager(serverGdtm, userProvider);

            final boolean isRunning = CentreResourceUtils.isRunning(customObject);
            final boolean isSorting = CentreResourceUtils.isSorting(customObject);
            
            final ICentreDomainTreeManagerAndEnhancer updatedFreshCentre;
            final EnhancedCentreEntityQueryCriteria<?, ?> freshCentreAppliedCriteriaEntity;
            
            if (isRunning) {
                freshCentreAppliedCriteriaEntity = CentreResourceUtils.createCriteriaEntity(centreContextHolder.getModifHolder(), companionFinder, critGenerator, miType, gdtm);
                updatedFreshCentre = freshCentreAppliedCriteriaEntity.getCentreDomainTreeMangerAndEnhancer();
                
                // There is a need to validate criteria entity with the check for 'required' properties. If it is not successful -- immediately return result without query running, fresh centre persistence, data generation etc.
                final Result validationResult = freshCentreAppliedCriteriaEntity.isValid();
                if (!validationResult.isSuccessful()) {
                    logger.debug("CRITERIA_RESOURCE: run finished (validation failed).");
                    final String staleCriteriaMessage = CriteriaResource.createStaleCriteriaMessage((String) centreContextHolder.getModifHolder().get("@@wasRun"), updatedFreshCentre, miType, gdtm, companionFinder, critGenerator);
                    return restUtil.rawListJSONRepresentation(freshCentreAppliedCriteriaEntity, updateResultantCustomObject(miType, gdtm, updatedFreshCentre, new LinkedHashMap<>(), staleCriteriaMessage));
                }
            } else {
                updatedFreshCentre = null;
                freshCentreAppliedCriteriaEntity = null;
            }
            
            // if the run() invocation warrants data generation (e.g. it has nothing to do with sorting)
            // then for an entity centre configuration check if a generator was provided
            final boolean createdByConstraintShouldOccur = centre.getGeneratorTypes().isPresent();
            final boolean generationShouldOccur = isRunning && !isSorting && createdByConstraintShouldOccur;
            if (generationShouldOccur) {
                // obtain the type for entities to be generated
                final Class<? extends AbstractEntity<?>> generatorEntityType = (Class<? extends AbstractEntity<?>>) centre.getGeneratorTypes().get().getKey();
                
                // create and execute a generator instance
                final IGenerator generator = centre.createGeneratorInstance(centre.getGeneratorTypes().get().getValue());
                final Result generationResult = generator.gen(generatorEntityType,
                        freshCentreAppliedCriteriaEntity.nonProxiedProperties().collect(toLinkedHashMap(
                                (final MetaProperty<?> mp) -> mp.getName(), 
                                (final MetaProperty<?> mp) -> Optional.ofNullable(mp.getValue()))));
                // if the data generation was unsuccessful based on the returned Result value then stop any further logic and return the obtained result
                // otherwise, proceed with the request handling further to actually query the data
                // in most cases, the generated and queried data would be represented by the same entity and, thus, the final query needs to be enhanced with user related filtering by property 'createdBy'
                if (!generationResult.isSuccessful()) {
                    logger.debug("CRITERIA_RESOURCE: run finished (generation failed).");
                    final String staleCriteriaMessage = CriteriaResource.createStaleCriteriaMessage((String) centreContextHolder.getModifHolder().get("@@wasRun"), updatedFreshCentre, miType, gdtm, companionFinder, critGenerator);
                    final Result result = generationResult.copyWith(new ArrayList<>(Arrays.asList(freshCentreAppliedCriteriaEntity, updateResultantCustomObject(miType, gdtm, updatedFreshCentre, new LinkedHashMap<>(), staleCriteriaMessage))));
                    return restUtil.resultJSONRepresentation(result);
                }
            }
            
            if (isRunning) {
                CentreUpdater.initAndCommit(gdtm, miType, CentreUpdater.PREVIOUSLY_RUN_CENTRE_NAME, updatedFreshCentre);
            }
            
            final ICentreDomainTreeManagerAndEnhancer previouslyRunCentre = CentreUpdater.updateCentre(gdtm, miType, CentreUpdater.PREVIOUSLY_RUN_CENTRE_NAME);
            final  EnhancedCentreEntityQueryCriteria<AbstractEntity<?>, ?> previouslyRunCriteriaEntity = CentreResourceUtils.createCriteriaValidationPrototype(miType, previouslyRunCentre, critGenerator, 0L, gdtm);
            
            final Optional<Pair<IQueryEnhancer<AbstractEntity<?>>, Optional<CentreContext<AbstractEntity<?>, ?>>>> queryEnhancerAndContext = createQueryEnhancerAndContext(
                    webUiConfig,
                    companionFinder,
                    serverGdtm,
                    userProvider,
                    critGenerator,
                    entityFactory,
                    centreContextHolder,
                    centre.getQueryEnhancerConfig(),
                    previouslyRunCriteriaEntity);
            
            final Pair<Map<String, Object>, List<?>> pair =
                    CentreResourceUtils.createCriteriaMetaValuesCustomObjectWithResult(
                            customObject,
                            previouslyRunCriteriaEntity,
                            centre.getAdditionalFetchProvider(),
                            queryEnhancerAndContext,
                            // There could be cases where the generated data and the queried data would have different types.
                            // For example, the queried data could be modelled by a synthesized entity that includes a subquery based on some generated data.
                            // In such cases, it is unpossible to enhance the final query with a user related condition automatically.
                            // This should be the responsibility of the application developer to properly construct a subquery that is based on the generated data.
                            // The query will be enhanced with condition createdBy=currentUser if createdByConstraintShouldOccur and generatorEntityType equal to the type of queried data (otherwise end-developer should do that itself by using queryEnhancer or synthesized model).
                            createdByConstraintShouldOccur && centre.getGeneratorTypes().get().getKey().equals(CentreResourceUtils.getEntityType(miType)) ? Optional.of(userProvider.getUser()) : Optional.empty());
            if (isRunning) {
                updateResultantCustomObject(miType, gdtm, previouslyRunCentre, pair.getKey(), null);
            }

            // Running the rendering customiser for result set of entities.
            final Optional<IRenderingCustomiser<?>> renderingCustomiser = centre.getRenderingCustomiser();
            if (renderingCustomiser.isPresent()) {
                final IRenderingCustomiser<?> renderer = renderingCustomiser.get();
                final List<Object> renderingHints = new ArrayList<>();
                for (final Object entity : pair.getValue()) {
                    renderingHints.add(renderer.getCustomRenderingFor((AbstractEntity<?>)entity).get());
                }
                pair.getKey().put("renderingHints", renderingHints);
            } else {
                pair.getKey().put("renderingHints", new ArrayList<Object>());
            }

            enhanceResultEntitiesWithCustomPropertyValues(centre, centre.getCustomPropertiesDefinitions(), centre.getCustomPropertiesAsignmentHandler(), (List<AbstractEntity<?>>) pair.getValue());

            final ArrayList<Object> list = new ArrayList<Object>();
            list.add(isRunning ? previouslyRunCriteriaEntity : null);
            list.add(pair.getKey());

            // TODO It looks like adding values directly to the list outside the map object leads to proper type/serialiser correspondence
            // FIXME Need to investigate why this is the case.
            list.addAll(pair.getValue());

            // NOTE: the following line can be the example how 'criteria running' server errors manifest to the client application
            // throw new IllegalStateException("Illegal state during criteria running.");
            logger.debug("CRITERIA_RESOURCE: run finished.");
            return restUtil.rawListJSONRepresentation(list.toArray());
        }, restUtil);
    }

    /**
     * Resultant custom object contains important result information such as 'isCentreChanged' (guards enablement of SAVE / DISCARD buttons) or 'metaValues' 
     * (they bind to metaValues criteria editors) or information whether selection criteria is stale (config button colour).
     * <p>
     * This method updates such information just before returning resultant custom object to the client.
     * 
     * @param miType
     * @param gdtm
     * @param updatedFreshCentre
     * @param resultantCustomObject
     * @param staleCriteriaMessage
     * 
     * @return
     */
    private static Map<String, Object> updateResultantCustomObject(final Class<? extends MiWithConfigurationSupport<?>> miType, final IGlobalDomainTreeManager gdtm, final ICentreDomainTreeManagerAndEnhancer updatedFreshCentre, final Map<String, Object> resultantCustomObject, final String staleCriteriaMessage) {
        resultantCustomObject.put("isCentreChanged", CentreResourceUtils.isFreshCentreChanged(updatedFreshCentre, CentreUpdater.updateCentre(gdtm, miType, CentreUpdater.SAVED_CENTRE_NAME)));
        resultantCustomObject.put("metaValues", CentreResourceUtils.createCriteriaMetaValues(updatedFreshCentre, CentreResourceUtils.getEntityType(miType)));
        
        // Resultant custom object contains information whether selection criteria is stale (config button colour).
        // Such information should be updated just before returning resultant custom object to the client.
        resultantCustomObject.put("staleCriteriaMessage", staleCriteriaMessage);
        
        return resultantCustomObject;
    }

    public static Optional<Pair<IQueryEnhancer<AbstractEntity<?>>, Optional<CentreContext<AbstractEntity<?>, ?>>>> createQueryEnhancerAndContext(
            final IWebUiConfig webUiConfig,
            final ICompanionObjectFinder companionFinder,
            final IServerGlobalDomainTreeManager serverGdtm,
            final IUserProvider userProvider,
            final ICriteriaGenerator critGenerator,
            final EntityFactory entityFactory,
            final CentreContextHolder centreContextHolder,
            final Optional<Pair<IQueryEnhancer<AbstractEntity<?>>, Optional<CentreContextConfig>>> queryEnhancerConfig,
            final  EnhancedCentreEntityQueryCriteria<AbstractEntity<?>, ?> criteriaEntity) {
        if (queryEnhancerConfig.isPresent()) {
            return Optional.of(new Pair<>(
                    queryEnhancerConfig.get().getKey(),
                    CentreResourceUtils.createCentreContext(
                            webUiConfig,
                            companionFinder,
                            serverGdtm,
                            userProvider,
                            critGenerator,
                            entityFactory,
                            centreContextHolder,
                            criteriaEntity,
                            queryEnhancerConfig.get().getValue())//
            ));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Assigns the values for custom properties.
     *
     * @param propertiesDefinitions
     * @param customPropertiesAsignmentHandler
     * @param entities
     */
    public static void enhanceResultEntitiesWithCustomPropertyValues(
            final EntityCentre<AbstractEntity<?>> centre, 
            final Optional<List<ResultSetProp>> propertiesDefinitions, 
            final Optional<Class<? extends ICustomPropsAssignmentHandler>> customPropertiesAsignmentHandler, 
            final List<AbstractEntity<?>> entities) {
        if (customPropertiesAsignmentHandler.isPresent()) {
            setCustomValues(entities, centre.createAssignmentHandlerInstance(customPropertiesAsignmentHandler.get()));
        }

        if (propertiesDefinitions.isPresent()) {
            for (final ResultSetProp resultSetProp : propertiesDefinitions.get()) {
                if (resultSetProp.propDef.isPresent()) {
                    final PropDef<?> propDef = resultSetProp.propDef.get();
                    final String propertyName = CalculatedProperty.generateNameFrom(propDef.title);
                    if (propDef.value.isPresent()) {
                        setCustomValue(entities, propertyName, propDef.value.get());
                    }
                }
            }
        }
    }

    private static void setCustomValue(final List<AbstractEntity<?>> entities, final String propertyName, final Object value) {
        for (final AbstractEntity<?> entity : entities) {
            entity.set(propertyName, value);
        }
    }

    private static <T extends AbstractEntity<?>> void setCustomValues(final List<AbstractEntity<?>> entities, final ICustomPropsAssignmentHandler assignmentHandler) {
        for (final AbstractEntity<?> entity : entities) {
            assignmentHandler.assignValues(entity);
        }
    }
}