package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IYieldExprOperand;

abstract class AbstractYieldExprOperand<T1, T2, ET extends AbstractEntity<?>> //
		extends AbstractYieldedItem<T1, ET> //
		implements IYieldExprOperand<T1, T2, ET> {

	protected abstract T2 nextForAbstractYieldExprOperand();

	@Override
	public T2 beginExpr() {
		return copy(nextForAbstractYieldExprOperand(), getTokens().beginExpression());
	}
}