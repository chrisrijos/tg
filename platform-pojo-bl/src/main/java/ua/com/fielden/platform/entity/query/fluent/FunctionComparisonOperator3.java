package ua.com.fielden.platform.entity.query.fluent;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IFunctionComparisonOperator3;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IFunctionCompoundCondition3;

abstract class FunctionComparisonOperator3<T, ET extends AbstractEntity<?>> extends AbstractComparisonOperator<IFunctionCompoundCondition3<T, ET>, ET> implements IFunctionComparisonOperator3<T, ET> {

	abstract T getParent5();

    @Override
    IFunctionCompoundCondition3<T, ET> getParent1() {
    	return new FunctionCompoundCondition3<T, ET>(){

			@Override
			T getParent3() {
				return FunctionComparisonOperator3.this.getParent5();
			}
        	
        };
    }
}