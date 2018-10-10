package ua.com.fielden.platform.test.entities.meta;

import ua.com.fielden.platform.entity.meta.IAfterChangeEventHandler;
import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.test.domain.entities.Advice;

/**
 * Implements the rule that define properties for meta-property associated with property <code>dispatchedToWorkshop</code> of entity {@link Advice}.
 * 
 * @author 01es
 * 
 */
public class AdviceDispatchedToWorkshopMetaDefiner implements IAfterChangeEventHandler {

    /**
     * The ability to modify properties <code>carrier</code> and <code>road</code> is defined by the fact that advice is dispatched or not. If dispatched then these properties
     * should not be modified.
     */
    @Override
    public void handle(final MetaProperty property, final Object entityPropertyValue) {
        final Advice advice = (Advice) property.getEntity();
        final Boolean isDispatched = entityPropertyValue != null;
        System.out.println("DEFINE RULE IS BEING EXECUTED ON PROPETY " + property.getName() + " FOR " + advice);
        define(advice, isDispatched, advice.isRoad());
    }

    private void define(final Advice advice, final boolean isDispatched, final boolean isRoad) {
        advice.getProperty("carrier").setEditable(!isDispatched && !isRoad);
        advice.getProperty("road").setEditable(!isDispatched);
    }

}
