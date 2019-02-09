package ua.com.fielden.platform.eql.stage1.elements;

import ua.com.fielden.platform.eql.meta.PropsResolutionContext;
import ua.com.fielden.platform.eql.meta.TransformationResult;
import ua.com.fielden.platform.eql.stage2.elements.AbsOf2;
import ua.com.fielden.platform.eql.stage2.elements.ISingleOperand2;

public class AbsOf1 extends SingleOperandFunction1<AbsOf2> {

    public AbsOf1(final ISingleOperand1<? extends ISingleOperand2> operand) {
        super(operand);
    }

    @Override
    public TransformationResult<AbsOf2> transform(final PropsResolutionContext resolutionContext) {
        final TransformationResult<? extends ISingleOperand2> operandTransformationResult = getOperand().transform(resolutionContext);
        return new TransformationResult<AbsOf2>(new AbsOf2(operandTransformationResult.getItem()), operandTransformationResult.getUpdatedContext());
    }
}