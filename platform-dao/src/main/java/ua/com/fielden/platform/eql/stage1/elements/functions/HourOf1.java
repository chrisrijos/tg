package ua.com.fielden.platform.eql.stage1.elements.functions;

import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage1.elements.operands.ISingleOperand1;
import ua.com.fielden.platform.eql.stage2.elements.functions.HourOf2;
import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;

public class HourOf1 extends SingleOperandFunction1<HourOf2> {

    public HourOf1(final ISingleOperand1<? extends ISingleOperand2<?>> operand) {
        super(operand);
    }

    @Override
    public TransformationResult<HourOf2> transform(final PropsResolutionContext context) {
        final TransformationResult<? extends ISingleOperand2<?>> operandTransformationResult = operand.transform(context);
        return new TransformationResult<HourOf2>(new HourOf2(operandTransformationResult.item), operandTransformationResult.updatedContext);
    }
}