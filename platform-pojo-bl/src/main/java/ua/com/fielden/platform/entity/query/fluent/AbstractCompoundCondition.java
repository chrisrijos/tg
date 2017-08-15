package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.ICompoundCondition;

abstract class AbstractCompoundCondition<T1, T2> extends AbstractLogicalCondition<T1>
		implements ICompoundCondition<T1, T2> {

	abstract T2 getParent2();

	@Override
	public T2 end() {
		return copy(getParent2(), getTokens().endCondition());
	}
}