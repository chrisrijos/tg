package ua.com.fielden.platform.eql.stage1.elements.functions;

import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage1.elements.operands.ISingleOperand1;
import ua.com.fielden.platform.eql.stage2.elements.functions.AverageOf2;
import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;

public class AverageOf1 extends SingleOperandFunction1<AverageOf2> {
    private final boolean distinct;

    public AverageOf1(final ISingleOperand1<? extends ISingleOperand2<?>> operand, final boolean distinct) {
        super(operand);
        this.distinct = distinct;
    }

    @Override
    public TransformationResult<AverageOf2> transform(final PropsResolutionContext context, final String sourceId) {
        final TransformationResult<? extends ISingleOperand2<?>> operandTr = operand.transform(context, sourceId);
        return new TransformationResult<AverageOf2>(new AverageOf2(operandTr.item, distinct), operandTr.updatedContext);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = super.hashCode();
        return prime * result + (distinct ? 1231 : 1237);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (!super.equals(obj)) {
            return false;
        }
        
        if (!(obj instanceof AverageOf1)) {
            return false;
        }
        
        final AverageOf1 other = (AverageOf1) obj;
        
        return distinct == other.distinct;
    }
}