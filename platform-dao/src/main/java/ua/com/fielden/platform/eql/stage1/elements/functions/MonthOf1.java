package ua.com.fielden.platform.eql.stage1.elements.functions;

import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage1.elements.operands.ISingleOperand1;
import ua.com.fielden.platform.eql.stage2.elements.functions.MonthOf2;
import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;

public class MonthOf1 extends SingleOperandFunction1<MonthOf2> {

    public MonthOf1(final ISingleOperand1<? extends ISingleOperand2<?>> operand) {
        super(operand);
    }

    @Override
    public TransformationResult<MonthOf2> transform(final PropsResolutionContext context, final String sourceId) {
        final TransformationResult<? extends ISingleOperand2<?>> operandTransformationResult = operand.transform(context, sourceId);
        return new TransformationResult<MonthOf2>(new MonthOf2(operandTransformationResult.item), operandTransformationResult.updatedContext);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = super.hashCode();
        return prime * result + MonthOf1.class.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || super.equals(obj) && obj instanceof MonthOf1;
    }   
}