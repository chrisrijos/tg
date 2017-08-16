package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IJoinCompoundCondition2;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IJoinCompoundCondition3;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IJoinWhere3;

final class JoinCompoundCondition3<ET extends AbstractEntity<?>> //
		extends AbstractCompoundCondition<IJoinWhere3<ET>, IJoinCompoundCondition2<ET>> //
		implements IJoinCompoundCondition3<ET> {

	@Override
	protected IJoinWhere3<ET> nextForAbstractLogicalCondition() {
		return new JoinWhere3<ET>();
	}

	@Override
	protected IJoinCompoundCondition2<ET> nextForAbstractCompoundCondition() {
		return new JoinCompoundCondition2<ET>();
	}
}