package ua.com.fielden.platform.web.centre;

import static ua.com.fielden.platform.domaintree.impl.GlobalDomainTreeManager.DEFAULT_CONFIG_TITLE;

import java.util.LinkedHashSet;
import com.google.inject.Inject;

import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.AbstractFunctionalEntityForCollectionModificationProducer;
import ua.com.fielden.platform.entity.ICollectionModificationController;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.factory.ICompanionObjectFinder;
import ua.com.fielden.platform.entity_centre.review.criteria.EnhancedCentreEntityQueryCriteria;

/**
 * A producer for new instances of entity {@link CentreConfigLoadAction}.
 *
 * @author TG Team
 *
 */
public class CentreConfigLoadActionProducer extends AbstractFunctionalEntityForCollectionModificationProducer<EnhancedCentreEntityQueryCriteria<AbstractEntity<?>, IEntityDao<AbstractEntity<?>>>, CentreConfigLoadAction, String, LoadableCentreConfig> {
    private final ICollectionModificationController<EnhancedCentreEntityQueryCriteria<AbstractEntity<?>, IEntityDao<AbstractEntity<?>>>, CentreConfigLoadAction, String, LoadableCentreConfig> controller;
    
    @Inject
    public CentreConfigLoadActionProducer(final EntityFactory factory, final ICompanionObjectFinder companionFinder) {
        super(factory, CentreConfigLoadAction.class, companionFinder);
        this.controller = new CentreConfigLoadActionController();
    }
    
    @Override
    protected ICollectionModificationController<EnhancedCentreEntityQueryCriteria<AbstractEntity<?>, IEntityDao<AbstractEntity<?>>>, CentreConfigLoadAction, String, LoadableCentreConfig> controller() {
        return controller;
    }
    
    @Override
    protected CentreConfigLoadAction provideCurrentlyAssociatedValues(final CentreConfigLoadAction entity, final EnhancedCentreEntityQueryCriteria<AbstractEntity<?>, IEntityDao<AbstractEntity<?>>> masterEntity) {
        if (contextNotEmpty()) {
            // provide loadable configurations into the action
            entity.setCentreConfigurations(new LinkedHashSet<>(selectionCrit().loadableCentresSupplier().get()));
            
            final LinkedHashSet<String> chosenIds = new LinkedHashSet<>();
            chosenIds.add(selectionCrit().saveAsNameSupplier().get().orElse(DEFAULT_CONFIG_TITLE));
            
            // provide chosenIds into the action
            entity.setChosenIds(chosenIds);
        }
        
        return entity;
    }
    
}