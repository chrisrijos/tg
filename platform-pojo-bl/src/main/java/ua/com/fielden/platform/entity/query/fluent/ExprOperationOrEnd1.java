package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IExprOperand1;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IExprOperationOrEnd0;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IExprOperationOrEnd1;

abstract class ExprOperationOrEnd1<T, ET extends AbstractEntity<?>> //
		extends ExprOperationOrEnd<IExprOperand1<T, ET>, IExprOperationOrEnd0<T, ET>, ET> //
		implements IExprOperationOrEnd1<T, ET> {

    protected ExprOperationOrEnd1(final Tokens tokens) {
        super(tokens);
    }
    
	protected abstract T nextForExprOperationOrEnd1(final Tokens tokens);

	@Override
	protected IExprOperationOrEnd0<T, ET> nextForExprOperationOrEnd(final Tokens tokens) {
		return new ExprOperationOrEnd0<T, ET>(tokens) {

			@Override
			protected T nextForExprOperationOrEnd0(final Tokens tokens) {
				return ExprOperationOrEnd1.this.nextForExprOperationOrEnd1(tokens);
			}

		};
	}

	@Override
	protected IExprOperand1<T, ET> nextForArithmeticalOperator(final Tokens tokens) {
		return new ExprOperand1<T, ET>(tokens) {

			@Override
			protected T nextForExprOperand1(final Tokens tokens) {
				return ExprOperationOrEnd1.this.nextForExprOperationOrEnd1(tokens);
			}
		};
	}
}