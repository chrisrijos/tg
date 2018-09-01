package ua.com.fielden.platform.web.centre;

import static java.lang.String.format;
import static ua.com.fielden.platform.domaintree.impl.AbstractDomainTree.validateRootType;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.from;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;
import static ua.com.fielden.platform.reflection.AnnotationReflector.getKeyType;
import static ua.com.fielden.platform.utils.CollectionUtil.setOf;
import static ua.com.fielden.platform.utils.EntityUtils.deepCopy;
import static ua.com.fielden.platform.utils.EntityUtils.fetchWithKeyAndDesc;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Period;

import ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeManager.ICentreDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.domaintree.centre.impl.CentreDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.domaintree.exceptions.DomainTreeException;
import ua.com.fielden.platform.domaintree.impl.CalculatedPropertyInfo;
import ua.com.fielden.platform.domaintree.impl.CustomProperty;
import ua.com.fielden.platform.entity.AbstractBatchAction;
import ua.com.fielden.platform.entity.annotation.EntityType;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.ICompoundCondition0;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.security.user.User;
import ua.com.fielden.platform.serialisation.api.ISerialiser;
import ua.com.fielden.platform.types.tuples.T2;
import ua.com.fielden.platform.ui.config.EntityCentreConfig;
import ua.com.fielden.platform.ui.config.MainMenuItem;
import ua.com.fielden.platform.ui.config.api.IEntityCentreConfig;
import ua.com.fielden.platform.ui.config.api.IMainMenuItem;
import ua.com.fielden.platform.ui.menu.MiWithConfigurationSupport;
import ua.com.fielden.platform.web.app.IWebUiConfig;

/**
 * This utility class contains additional methods applicable to {@link CentreUpdater}.
 *
 * @author TG Team
 *
 */
public class CentreUpdaterUtils extends CentreUpdater {
    private final static Logger logger = Logger.getLogger(CentreUpdaterUtils.class);
    
    /** Protected default constructor to prevent instantiation. */
    protected CentreUpdaterUtils() {
    }
    
    ///////////////////////////// CENTRE CREATION /////////////////////////////
    protected static ICentreDomainTreeManagerAndEnhancer createDefaultCentre(
            final Class<?> menuItemType,
            final IWebUiConfig webUiConfig) {
        final EntityCentre entityCentre = webUiConfig.getCentres().get(menuItemType);
        if (entityCentre != null) {
            return entityCentre.createDefaultCentre();
        } else {
            throw errorf("EntityCentre instance could not be found for [%s] menu item type.", menuItemType.getSimpleName());
        }
    }
    
    public static ICentreDomainTreeManagerAndEnhancer createEmptyCentre(final Class<?> root, final ISerialiser serialiser, final T2<Map<Class<?>, Set<CalculatedPropertyInfo>>, Map<Class<?>, List<CustomProperty>>> calculatedAndCustomProperties, final Class<? extends MiWithConfigurationSupport<?>> miType) {
        // TODO next line of code must take in to account that the menu item is for association centre.
        final CentreDomainTreeManagerAndEnhancer centre = new CentreDomainTreeManagerAndEnhancer(serialiser, setOf(root), calculatedAndCustomProperties, miType);
        // initialise checkedProperties tree to make it more predictable in getting meta-info from "checkedProperties"
        centre.getFirstTick().checkedProperties(root);
        centre.getSecondTick().checkedProperties(root);
        
        return centre;
    }
    
    ///////////////////////////// ERROR REPORTING /////////////////////////////
    /**
     * Logs and returns an {@link DomainTreeException} error with specified message.
     *
     * @param message
     */
    private static DomainTreeException error(final String message) {
        logger.error(message);
        return new DomainTreeException(message); // TODO migrate to CentreUpdaterException? don't forget to adjust the usage of DomainTreeException.
    }
    
    private static DomainTreeException errorf(final String message, final Object... args) {
        return error(format(message, args));
    }
    
    ///////////////////////////// VALIDATION /////////////////////////////
    /**
     * Validates the type of menu item (a part of centre key) to be actually "menu item type".
     *
     * @param menuItemType
     */
    private static void validateMenuItemType(final Class<?> menuItemType) {
        try {
            final Class<?> parentClassForMenuItems = ClassLoader.getSystemClassLoader().loadClass("ua.com.fielden.platform.ui.menu.MiWithConfigurationSupport");
            if (!parentClassForMenuItems.isAssignableFrom(menuItemType)) {
                throw errorf("The menu item type %s is not 'MiWithConfigurationSupport' descendant, which should be a parent type for menu items for all entity centres.", menuItemType.getSimpleName());
            }
        } catch (final ClassNotFoundException e) {
            throw errorf("There are no loaded class 'MiWithConfigurationSupport', which should be a parent type for menu items for entity centres. [%s]", e.getMessage());
        }
    }
    
    private static Class<?> validateMenuItemTypeRootType(final Class<?> menuItemType) {
        final EntityType etAnnotation = menuItemType.getAnnotation(EntityType.class);
        if (etAnnotation == null || etAnnotation.value() == null) {
            throw errorf("The menu item type %s has no 'EntityType' annotation, which is necessary to specify the root type of the centre.", menuItemType.getSimpleName());
        }
        final Class<?> root = etAnnotation.value();
        validateRootType(root);
        return getValidatedRootIfAssociation(root);
    }
    
    /**
     * Returns the key type of the entity if it is a association batch action entity.
     *
     * @param value
     * @return
     */
    private static Class<?> getValidatedRootIfAssociation(final Class<?> value) {
        if (AbstractBatchAction.class.isAssignableFrom(value)) {
            final Class<?> root = getKeyType(value);
            validateRootType(root);
            return root;
        }
        return value;
    }
    
    
    
    ///////////////////////////// CENTRE MAINTENANCE /////////////////////////////
    public static Optional<ICentreDomainTreeManagerAndEnhancer> initEntityCentreManager(
            final Class<?> menuItemType,
            final User user,
            final String name,
            final ISerialiser serialiser,
            final IWebUiConfig webUiConfig,
            final IEntityCentreConfig eccCompanion) {
        final String loggingSuffix = format("for type [%s] with name [%s] for user [%s]", menuItemType.getSimpleName(), name, user);
        logger.info(format("\t\tInitialising entity-centre instance %s...", loggingSuffix));
        final DateTime start = new DateTime();
        
        validateMenuItemType(menuItemType);
        validateMenuItemTypeRootType(menuItemType);
        
        final EntityResultQueryModel<EntityCentreConfig> model = modelFor(user, menuItemType.getName(), name);
        
        final List<EntityCentreConfig> firstTwoConfigs = eccCompanion.getFirstEntities(from(model).model(), 2);
        final Optional<ICentreDomainTreeManagerAndEnhancer> centre;
        if (firstTwoConfigs.size() > 1) { 
            throw errorf("There are more than one entity-centre instance %s.", loggingSuffix); // TODO return warning? collect unfortunate garbage?
        } else {
            centre = firstTwoConfigs.stream().findAny().map(ecc -> restoreCentreManagerFrom(ecc, serialiser, menuItemType, webUiConfig, eccCompanion, loggingSuffix));
        }
        final DateTime end = new DateTime();
        final Period pd = new Period(start, end);
        logger.info(format("\t\tInitialising entity-centre instance %s...done in [%s]", loggingSuffix, pd.getSeconds() + " s " + pd.getMillis() + " ms"));
        return centre;
    }
    
    private static ICentreDomainTreeManagerAndEnhancer restoreCentreManagerFrom(
            // params for actual deserialisation
            final EntityCentreConfig ecc,
            final ISerialiser serialiser,
            // params for: deserialisation failed -- create empty and save
            final Class<?> menuItemType,
            final IWebUiConfig webUiConfig,
            final IEntityCentreConfig eccCompanion,
            // params for: deserialisation failed -- logging
            final String loggingSuffix) {
        try {
            logger.info(format("\t\t\trestoreCentreManagerFrom entity-centre instance  %s...", loggingSuffix));
            final DateTime start = new DateTime();
            final CentreDomainTreeManagerAndEnhancer result = serialiser.deserialise(ecc.getConfigBody(), CentreDomainTreeManagerAndEnhancer.class);
            final DateTime end = new DateTime();
            final Period pd = new Period(start, end);
            logger.info(format("\t\t\trestoreCentreManagerFrom entity-centre instance %s...done in [%s]", loggingSuffix, pd.getSeconds() + " s " + pd.getMillis() + " ms"));
            return result;
        } catch (final Exception deserialisationException) {
            logger.error("============================================ CENTRE DESERIALISATION HAS FAILED ============================================");
            logger.error(format("Unable to deserialise a entity-centre instance %s. The exception is the following: ", loggingSuffix), deserialisationException);
            logger.error(format("Started creation of default entity-centre configuration %s.", loggingSuffix));
            final ICentreDomainTreeManagerAndEnhancer newCentreManager = createDefaultCentre(menuItemType, webUiConfig);
            logger.error(format("Started saving of default entity-centre configuration %s.", loggingSuffix));
            ecc.setConfigBody(serialiser.serialise(newCentreManager));
            eccCompanion.save(ecc);
            logger.error(format("Ended creation and saving of default entity-centre configuration %s. For now it can be used.", loggingSuffix));
            logger.error("============================================ CENTRE DESERIALISATION HAS FAILED [END] ============================================");
            return newCentreManager;
        }
    }
    
    public static ICentreDomainTreeManagerAndEnhancer saveAsEntityCentreManager(
            final ICentreDomainTreeManagerAndEnhancer centre,
            final Class<?> menuItemType,
            final User user,
            final String newName,
            final String newDesc,
            final ISerialiser serialiser,
            final IEntityCentreConfig eccCompanion,
            final IMainMenuItem mmiCompanion) {
        final String loggingSuffix = format("for type [%s] with name [%s] for user [%s]", menuItemType.getSimpleName(), newName, user);
        logger.info(format("\t\tsaveAsEntityCentreManager entity-centre instance %s...", loggingSuffix));
        final DateTime start = new DateTime();
        
        validateMenuItemType(menuItemType);
        validateMenuItemTypeRootType(menuItemType);
        
        // save an instance of EntityCentreConfig with overridden body, which should exist in DB
        final String menuItemTypeName = menuItemType.getName();
        
        final MainMenuItem menuItem = mmiCompanion.findByKeyOptional(menuItemTypeName).orElseGet(() -> {
            final MainMenuItem newMainMenuItem = mmiCompanion.new_();
            newMainMenuItem.setKey(menuItemTypeName);
            return mmiCompanion.save(newMainMenuItem);
        });
        final EntityCentreConfig ecc = eccCompanion.new_();
        ecc.setOwner(user);
        ecc.setTitle(newName);
        ecc.setMenuItem(menuItem);
        ecc.setDesc(newDesc);
        ecc.setConfigBody(serialiser.serialise(centre));
        eccCompanion.quickSave(ecc); // please note that CommonEntityDao exception will be thrown in case where such ecc instance already exists // TODO check quickSave usage!
        
        final DateTime end = new DateTime();
        final Period pd = new Period(start, end);
        logger.info(format("\t\tsaveAsEntityCentreManager entity-centre instance %s...done in [%s]", loggingSuffix, pd.getSeconds() + " s " + pd.getMillis() + " ms"));
        return centre;
    }
    
    public static ICentreDomainTreeManagerAndEnhancer saveEntityCentreManager(
            final ICentreDomainTreeManagerAndEnhancer centre,
            final Class<?> menuItemType,
            final User user,
            final String name,
            final String newDesc,
            final ISerialiser serialiser,
            final IEntityCentreConfig eccCompanion) {
        final String loggingSuffix = format("for type [%s] with name [%s] for user [%s]", menuItemType.getSimpleName(), name, user);
        logger.info(format("\t\tsaveEntityCentreManager entity-centre instance %s...", loggingSuffix));
        final DateTime start = new DateTime();
        
        validateMenuItemType(menuItemType);
        validateMenuItemTypeRootType(menuItemType);
        
        // save an instance of EntityCentreConfig with overridden body, which should exist in DB
        final EntityResultQueryModel<EntityCentreConfig> model = modelFor(user, menuItemType.getName(), name);
        
        final List<EntityCentreConfig> firstTwoConfigs = eccCompanion.getFirstEntities(from(model).model(), 2);
        if (firstTwoConfigs.size() > 1) { 
            throw errorf("There are more than one entity-centre instance %s.", loggingSuffix); // TODO return warning? collect unfortunate garbage?
        } else if (firstTwoConfigs.size() < 1) { 
            throw errorf("Unable to save non-existent entity-centre instance %s.", loggingSuffix); // TODO return warning? provide some self-healing logic?
        } else {
            final EntityCentreConfig ecc = firstTwoConfigs.get(0);
            ecc.setConfigBody(serialiser.serialise(centre));
            if (newDesc != null) {
                ecc.setDesc(newDesc);
            }
            eccCompanion.quickSave(ecc); // TODO check quickSave usage!
            final DateTime end = new DateTime();
            final Period pd = new Period(start, end);
            logger.info(format("\t\tsaveEntityCentreManager entity-centre instance %s...done in [%s]", loggingSuffix, pd.getSeconds() + " s " + pd.getMillis() + " ms"));
            return centre;
        }
    }
    
    /**
     * A copy method for entity centre that copies also "transient" stuff like currentAnalyses and freezedAnalyses. It has been done to take care about copying entity centre with
     * some changed / freezed analyses (all that changes will be promoted to copies).
     *
     * @param centre
     * @return
     */
    protected static ICentreDomainTreeManagerAndEnhancer copyCentre(final ICentreDomainTreeManagerAndEnhancer centre, final ISerialiser serialiser) {
        logger.debug(format("\t\tCopying centre..."));
        logger.debug(format("\t\t\tDeep copy..."));
        final DateTime start = new DateTime();
        final ICentreDomainTreeManagerAndEnhancer copy = deepCopy(centre, serialiser);
        logger.debug(format("\t\t\tDeep copy...done in [%s]", new Period(start, new DateTime()).getMillis() + " ms"));
        
        final DateTime end = new DateTime();
        final Period pd = new Period(start, end);
        logger.debug(format("\t\tCopying centre... done in [%s].", pd.getSeconds() + " s " + pd.getMillis() + " ms"));
        return copy;
    }
    
    protected static EntityCentreConfig findConfig(final Class<?> menuItemType, final User user, final String name, final IEntityCentreConfig eccCompanion) {
        return eccCompanion.getEntity(
            from(modelFor(user, menuItemType.getName(), name)).with(fetchWithKeyAndDesc(EntityCentreConfig.class, true).with("preferred").fetchModel()).model()
        );
    }
    
    /**
     * Removes centre configurations from persistent storage.
     * 
     * @param menuItemType
     * @param names
     */
    public static void removeCentres(final User user, final Class<?> menuItemType, final IEntityCentreConfig eccCompanion, final String ... names) {
        eccCompanion.delete(multiModelFor(user, menuItemType.getName(), names));
    }
    
    ///////////////////////////// EQL MODELS /////////////////////////////
    /**
     * Creates partial model to retrieve {@link EntityCentreConfig} instances for specified <code>user</code> and <code>menuItemTypeName</code>.
     *
     * @param user
     * @param menuItemTypeName
     * 
     * @return
     */
    private static ICompoundCondition0<EntityCentreConfig> modelFor(final User user, final String menuItemTypeName) {
        return select(EntityCentreConfig.class).where()
            .prop("owner").eq().val(user).and() // look for entity-centres for only current user
            .prop("menuItem.key").eq().val(menuItemTypeName);
    }
    
    /**
     * Creates a model to retrieve {@link EntityCentreConfig} instances for specified <code>user</code>, <code>title</code> and <code>menuItemTypeName</code>.
     *
     * @param user
     * @param menuItemTypeName
     * @param title
     * 
     * @return
     */
    private static EntityResultQueryModel<EntityCentreConfig> modelFor(final User user, final String menuItemTypeName, final String title) {
        return modelFor(user, menuItemTypeName).and()
            .prop("title").eq().val(title).model();
    }
    
    /**
     * Creates a model to retrieve {@link EntityCentreConfig} instances for specified <code>user</code>, <code>titles</code> and <code>menuItemTypeName</code>.
     *
     * @param user
     * @param menuItemTypeName
     * @param titles
     * 
     * @return
     */
    private static EntityResultQueryModel<EntityCentreConfig> multiModelFor(final User user, final String menuItemTypeName, final String... titles) {
        return modelFor(user, menuItemTypeName).and()
            .prop("title").in().values(titles).model();
    }
    
}