package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IJoinComparisonOperator1;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IJoinCompoundCondition1;

class JoinComparisonOperator1<ET extends AbstractEntity<?>> extends AbstractComparisonOperator<IJoinCompoundCondition1<ET>, ET> implements IJoinComparisonOperator1<ET> {

    @Override
    IJoinCompoundCondition1<ET> nextForAbstractComparisonOperator() {
        return new JoinCompoundCondition1<ET>();
    }
}