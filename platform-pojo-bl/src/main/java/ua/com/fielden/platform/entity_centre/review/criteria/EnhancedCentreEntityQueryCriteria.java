package ua.com.fielden.platform.entity_centre.review.criteria;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.inject.Inject;

import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.dao.IGeneratedEntityController;
import ua.com.fielden.platform.domaintree.centre.ICentreDomainTreeManager.ICentreDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.factory.ICompanionObjectFinder;
import ua.com.fielden.platform.entity.functional.centre.CentreContextHolder;
import ua.com.fielden.platform.entity.matcher.IValueMatcherFactory;
import ua.com.fielden.platform.serialisation.api.ISerialiser;

//TODO must finish implementation in order to provide correct ordering, fetch model etc. Consider to provide reference on to the ICriteriaDomainTreeManager.
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
    private Function<Map<String, Object>, List<AbstractEntity<?>>> exportQueryRunner;
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

    public void setFreshCentreSupplier(final Supplier<ICentreDomainTreeManagerAndEnhancer> freshCentreSupplier) {
        this.freshCentreSupplier = freshCentreSupplier;
    }

    public Supplier<ICentreDomainTreeManagerAndEnhancer> freshCentreSupplier() {
        return freshCentreSupplier;
    }

    public Function<Map<String, Object>, List<AbstractEntity<?>>> exportQueryRunner() {
        return exportQueryRunner;
    }

    public void setExportQueryRunner(final Function<Map<String, Object>, List<AbstractEntity<?>>> exportQueryRunner) {
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
