package ua.com.fielden.platform.entity_centre.review.criteria;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.inject.Inject;

import com.google.inject.Inject;

import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.dao.IGeneratedEntityController;
import ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeManager.ICentreDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.factory.ICompanionObjectFinder;
import ua.com.fielden.platform.entity.functional.centre.CentreContextHolder;
import ua.com.fielden.platform.entity.matcher.IValueMatcherFactory;
import ua.com.fielden.platform.serialisation.api.ISerialiser;
/**
 * This class is the base class to enhance with criteria and resultant properties.
 *
 * @author TG Team
 *
 * @param <T>
 * @param <DAO>
 */
public class EnhancedCentreEntityQueryCriteria<T extends AbstractEntity<?>, DAO extends IEntityDao<T>> extends EntityQueryCriteria<ICentreDomainTreeManagerAndEnhancer, T, DAO> {
    private Supplier<ICentreDomainTreeManagerAndEnhancer> freshCentreSupplier;
    private Supplier<ICentreDomainTreeManagerAndEnhancer> defaultCentreSupplier;
    private Function<Map<String, Object>, Stream<AbstractEntity<?>>> exportQueryRunner;
    private Consumer<Consumer<ICentreDomainTreeManagerAndEnhancer>> centreAdjuster;
    /**
     * This function represents centre query runner for export action which is dependent on configuration of the passed <code>customObject</code>.
     * Running of this fully-fledged query depends on query context (see property centreContextHolder).
     */
    private CentreContextHolder centreContextHolder;
    
    /**
     * Constructs {@link EnhancedCentreEntityQueryCriteria} with specified {@link IValueMatcherFactory}. Needed mostly for instantiating through injector.
     *
     * @param entityDao
     * @param valueMatcherFactory
     */
    @SuppressWarnings("rawtypes")
    @Inject
    protected EnhancedCentreEntityQueryCriteria(final IValueMatcherFactory valueMatcherFactory, final IGeneratedEntityController generatedEntityController, final ISerialiser serialiser, final ICompanionObjectFinder controllerProvider) {
        super(valueMatcherFactory, generatedEntityController, serialiser, controllerProvider);
    }
    
    public void setCentreAdjuster(final Consumer<Consumer<ICentreDomainTreeManagerAndEnhancer>> centreAdjuster) {
        this.centreAdjuster = centreAdjuster;
    }

    public Consumer<Consumer<ICentreDomainTreeManagerAndEnhancer>> centreAdjuster() {
        return centreAdjuster;
    }

    public void setFreshCentreSupplier(final Supplier<ICentreDomainTreeManagerAndEnhancer> freshCentreSupplier) {
        this.freshCentreSupplier = freshCentreSupplier;
    }

    public Supplier<ICentreDomainTreeManagerAndEnhancer> freshCentreSupplier() {
        return freshCentreSupplier;
    }

    public void setDefaultCentreSupplier(final Supplier<ICentreDomainTreeManagerAndEnhancer> defaultCentreSupplier) {
        this.defaultCentreSupplier = defaultCentreSupplier;
    }

    public Supplier<ICentreDomainTreeManagerAndEnhancer> defaultCentreSupplier() {
        return defaultCentreSupplier;
    }

    public Function<Map<String, Object>, Stream<AbstractEntity<?>>> exportQueryRunner() {
        return exportQueryRunner;
    }

    public void setExportQueryRunner(final Function<Map<String, Object>, Stream<AbstractEntity<?>>> exportQueryRunner) {
        this.exportQueryRunner = exportQueryRunner;
    }
    
    public CentreContextHolder centreContextHolder() {
        return centreContextHolder;
    }
    
    public EnhancedCentreEntityQueryCriteria<T, DAO> setCentreContextHolder(final CentreContextHolder centreContextHolder) {
        this.centreContextHolder = centreContextHolder;
        return this;
    }
}
