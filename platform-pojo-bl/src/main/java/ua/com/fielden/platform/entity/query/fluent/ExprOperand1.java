package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IExprOperand1;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IExprOperand2;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IExprOperationOrEnd1;

abstract class ExprOperand1<T, ET extends AbstractEntity<?>> //
		extends ExprOperand<IExprOperationOrEnd1<T, ET>, IExprOperand2<T, ET>, ET> //
		implements IExprOperand1<T, ET> {

	protected abstract T nextForExprOperand1();

	@Override
	protected IExprOperand2<T, ET> nextForExprOperand() {
		return new ExprOperand2<T, ET>() {

			@Override
			protected T nextForExprOperand2() {
				return ExprOperand1.this.nextForExprOperand1();
			}

		};
	}

	@Override
	protected IExprOperationOrEnd1<T, ET> nextForSingleOperand() {
		return new ExprOperationOrEnd1<T, ET>() {

			@Override
			protected T nextForExprOperationOrEnd1() {
				return ExprOperand1.this.nextForExprOperand1();
			}

		};
	}
}