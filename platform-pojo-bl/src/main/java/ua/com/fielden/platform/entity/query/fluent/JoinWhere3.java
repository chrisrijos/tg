package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IJoinComparisonOperator3;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IJoinCompoundCondition3;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IJoinWhere3;

final class JoinWhere3<ET extends AbstractEntity<?>> //
		extends ConditionalOperand<IJoinComparisonOperator3<ET>, IJoinCompoundCondition3<ET>, ET> //
		implements IJoinWhere3<ET> {

	@Override
	protected IJoinCompoundCondition3<ET> nextForConditionalOperand() {
		return new JoinCompoundCondition3<ET>();
	}

	@Override
	protected IJoinComparisonOperator3<ET> nextForSingleOperand() {
		return new JoinComparisonOperator3<ET>();
	}
}