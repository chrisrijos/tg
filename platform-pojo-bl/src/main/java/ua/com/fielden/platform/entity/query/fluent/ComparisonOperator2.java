package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IComparisonOperator2;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.ICompoundCondition2;

final class ComparisonOperator2<ET extends AbstractEntity<?>> //
		extends ComparisonOperator<ICompoundCondition2<ET>, ET> //
		implements IComparisonOperator2<ET> {

	@Override
	protected ICompoundCondition2<ET> nextForComparisonOperator() {
		return new CompoundCondition2<ET>();
	}
}