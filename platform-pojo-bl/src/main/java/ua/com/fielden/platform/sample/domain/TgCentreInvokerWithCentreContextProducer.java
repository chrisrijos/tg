package ua.com.fielden.platform.sample.domain;

import ua.com.fielden.platform.dao.DefaultEntityProducerWithContext;
import ua.com.fielden.platform.dao.IEntityProducer;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.factory.ICompanionObjectFinder;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * A producer for new instances of entity {@link TgCentreInvokerWithCentreContext}.
 *
 * @author TG Team
 *
 */
public class TgCentreInvokerWithCentreContextProducer extends DefaultEntityProducerWithContext<TgCentreInvokerWithCentreContext> implements IEntityProducer<TgCentreInvokerWithCentreContext> {
    private final Logger logger = Logger.getLogger(getClass());

    @Inject
    public TgCentreInvokerWithCentreContextProducer(final EntityFactory factory, final ICompanionObjectFinder companionFinder) {
        super(factory, TgCentreInvokerWithCentreContext.class, companionFinder);
    }

    @Override
    protected TgCentreInvokerWithCentreContext provideDefaultValues(final TgCentreInvokerWithCentreContext entity) {
        logger.error("restored masterEntity (centre context): " + getContext());
        logger.error("restored masterEntity (centre context's selection criteria): " + getContext().getSelectionCrit().get("tgPersistentEntityWithProperties_critOnlyBigDecimalProp"));
        logger.error("restored masterEntity (centre context's selection criteria): " + getContext().getSelectionCrit().get("tgPersistentEntityWithProperties_bigDecimalProp_from"));
        
        entity.setCritOnlyBigDecimalPropCriterion(getContext().getSelectionCrit().get("tgPersistentEntityWithProperties_critOnlyBigDecimalProp"));
        entity.setBigDecimalPropFromCriterion(getContext().getSelectionCrit().get("tgPersistentEntityWithProperties_bigDecimalProp_from"));
        return entity;
    }
}