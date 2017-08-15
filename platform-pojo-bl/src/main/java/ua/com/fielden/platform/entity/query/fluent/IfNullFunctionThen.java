package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IFunctionLastArgument;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IIfNullFunctionThen;

abstract class IfNullFunctionThen<T, ET extends AbstractEntity<?>> extends AbstractQueryLink implements IIfNullFunctionThen<T, ET> {
	
	abstract T getParent();

    @Override
    public IFunctionLastArgument<T, ET> then() {
    	return copy(new FunctionLastArgument<T, ET>(){

			@Override
			T getParent3() {
				return IfNullFunctionThen.this.getParent();
			}
        	
        }, getTokens());
    }
}