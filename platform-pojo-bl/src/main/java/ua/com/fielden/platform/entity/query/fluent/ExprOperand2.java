package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IExprOperand2;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IExprOperand3;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IExprOperationOrEnd2;

abstract class ExprOperand2<T, ET extends AbstractEntity<?>> //
		extends ExprOperand<IExprOperationOrEnd2<T, ET>, IExprOperand3<T, ET>, ET> //
		implements IExprOperand2<T, ET> {

	protected abstract T nextForExprOperand2();

	@Override
	protected IExprOperand3<T, ET> nextForExprOperand() {
		return new ExprOperand3<T, ET>() {

			@Override
			protected T nextForExprOperand3() {
				return ExprOperand2.this.nextForExprOperand2();
			}

		};
	}

	@Override
	protected IExprOperationOrEnd2<T, ET> nextForSingleOperand() {
		return new ExprOperationOrEnd2<T, ET>() {

			@Override
			protected T nextForExprOperationOrEnd2() {
				return ExprOperand2.this.nextForExprOperand2();
			}

		};
	}
}