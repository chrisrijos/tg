package ua.com.fielden.platform.eql.stage2.elements.functions;

import ua.com.fielden.platform.eql.stage2.elements.TransformationContext;
import ua.com.fielden.platform.eql.stage2.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;
import ua.com.fielden.platform.eql.stage3.elements.functions.MinOf3;
import ua.com.fielden.platform.eql.stage3.elements.operands.ISingleOperand3;

public class MinOf2 extends SingleOperandFunction2<MinOf3> {

    public MinOf2(final ISingleOperand2<? extends ISingleOperand3> operand) {
        super(operand);
    }

    @Override
    public Class<?> type() {
        return operand.type();
    }

    @Override
    public Object hibType() {
        return operand.hibType();
    } 
    
    @Override
    public TransformationResult<MinOf3> transform(final TransformationContext context) {
        final TransformationResult<? extends ISingleOperand3> operandTransformationResult = operand.transform(context);
        return new TransformationResult<MinOf3>(new MinOf3(operandTransformationResult.item), operandTransformationResult.updatedContext);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = super.hashCode();
        return prime * result + MinOf2.class.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || super.equals(obj) && obj instanceof MinOf2;
    }
}