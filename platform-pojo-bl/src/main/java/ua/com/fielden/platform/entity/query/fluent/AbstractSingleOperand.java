package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IConcatFunctionArgument;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IDateDiffIntervalFunction;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IFunctionLastArgument;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IFunctionWhere0;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IIfNullFunctionArgument;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IRoundFunctionArgument;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.ISingleOperand;
import ua.com.fielden.platform.entity.query.model.ExpressionModel;
import ua.com.fielden.platform.entity.query.model.SingleResultQueryModel;

abstract class AbstractSingleOperand<T, ET extends AbstractEntity<?>> extends AbstractQueryLink
		implements ISingleOperand<T, ET> {
	abstract T nextForAbstractSingleOperand();

	@Override
	public T val(final Object value) {
		return copy(nextForAbstractSingleOperand(), getTokens().val(value));
	}

	@Override
	public T iVal(final Object value) {
		return copy(nextForAbstractSingleOperand(), getTokens().iVal(value));
	}

	@Override
	public T model(final SingleResultQueryModel<?> model) {
		return copy(nextForAbstractSingleOperand(), getTokens().model(model));
	}

	@Override
	public T param(final String paramName) {
		return copy(nextForAbstractSingleOperand(), getTokens().param(paramName));
	}

	@Override
	public T param(final Enum paramName) {
		return param(paramName.toString());
	}

	@Override
	public T iParam(final String paramName) {
		return copy(nextForAbstractSingleOperand(), getTokens().iParam(paramName));
	}

	@Override
	public T iParam(final Enum paramName) {
		return iParam(paramName.toString());
	}

	@Override
	public T prop(final String propertyName) {
		return copy(nextForAbstractSingleOperand(), getTokens().prop(propertyName));
	}

	@Override
	public T prop(final Enum propertyName) {
		return prop(propertyName.toString());
	}

	@Override
	public T extProp(final String propertyName) {
		return copy(nextForAbstractSingleOperand(), getTokens().extProp(propertyName));
	}

	@Override
	public T extProp(final Enum propertyName) {
		return extProp(propertyName.toString());
	}

	@Override
	public T expr(final ExpressionModel expr) {
		return copy(nextForAbstractSingleOperand(), getTokens().expr(expr));
	}

	@Override
	public IDateDiffIntervalFunction<T, ET> count() {
		return copy(new DateDiffIntervalFunction<T, ET>() {

			@Override
			T nextForDateDiffIntervalFunction() {
				return AbstractSingleOperand.this.nextForAbstractSingleOperand();
			}

		}, getTokens().countDateIntervalFunction());
	}

	@Override
	public IFunctionWhere0<T, ET> caseWhen() {
		return copy(new FunctionWhere0<T, ET>() {

			@Override
			T nextForFunctionWhere0() {
				return AbstractSingleOperand.this.nextForAbstractSingleOperand();
			}

		}, getTokens().caseWhenFunction());
	}

	@Override
	public IIfNullFunctionArgument<T, ET> ifNull() {
		return copy(new IfNullFunctionArgument<T, ET>() {

			@Override
			T nextForIfNullFunctionArgument() {
				return AbstractSingleOperand.this.nextForAbstractSingleOperand();
			}

		}, getTokens().ifNull());
	}

	@Override
	public IConcatFunctionArgument<T, ET> concat() {
		return copy(new ConcatFunctionArgument<T, ET>() {

			@Override
			T nextForConcatFunctionArgument() {
				return AbstractSingleOperand.this.nextForAbstractSingleOperand();
			}

		}, getTokens().concat());
	}

	@Override
	public IRoundFunctionArgument<T, ET> round() {
		return copy(new RoundFunctionArgument<T, ET>() {

			@Override
			T nextForRoundFunctionArgument() {
				return AbstractSingleOperand.this.nextForAbstractSingleOperand();
			}

		}, getTokens().round());
	}

	@Override
	public T now() {
		return copy(nextForAbstractSingleOperand(), getTokens().now());
	}

	protected FunctionLastArgument<T, ET> createFunctionLastArgument() {
		return new FunctionLastArgument<T, ET>() {

			@Override
			T nextForFunctionLastArgument() {
				return AbstractSingleOperand.this.nextForAbstractSingleOperand();
			}
		};
	}

	@Override
	public IFunctionLastArgument<T, ET> upperCase() {
		return copy(createFunctionLastArgument(), getTokens().uppercase());
	}

	@Override
	public IFunctionLastArgument<T, ET> lowerCase() {
		return copy(createFunctionLastArgument(), getTokens().lowercase());
	}

	@Override
	public IFunctionLastArgument<T, ET> secondOf() {
		return copy(createFunctionLastArgument(), getTokens().secondOf());
	}

	@Override
	public IFunctionLastArgument<T, ET> minuteOf() {
		return copy(createFunctionLastArgument(), getTokens().minuteOf());
	}

	@Override
	public IFunctionLastArgument<T, ET> hourOf() {
		return copy(createFunctionLastArgument(), getTokens().hourOf());
	}

	@Override
	public IFunctionLastArgument<T, ET> dayOf() {
		return copy(createFunctionLastArgument(), getTokens().dayOf());
	}

	@Override
	public IFunctionLastArgument<T, ET> monthOf() {
		return copy(createFunctionLastArgument(), getTokens().monthOf());
	}

	@Override
	public IFunctionLastArgument<T, ET> yearOf() {
		return copy(createFunctionLastArgument(), getTokens().yearOf());
	}

	@Override
	public IFunctionLastArgument<T, ET> dateOf() {
		return copy(createFunctionLastArgument(), getTokens().dateOf());
	}

	@Override
	public IFunctionLastArgument<T, ET> absOf() {
		return copy(createFunctionLastArgument(), getTokens().absOf());
	}
}