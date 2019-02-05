package ua.com.fielden.platform.eql.stage1.elements;

import ua.com.fielden.platform.eql.meta.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage2.elements.MinuteOf2;
import ua.com.fielden.platform.eql.stage2.elements.ISingleOperand2;

public class MinuteOf1 extends SingleOperandFunction1<MinuteOf2> {

    public MinuteOf1(final ISingleOperand1<? extends ISingleOperand2> operand) {
        super(operand);
    }

    @Override
    public MinuteOf2 transform(final PropsResolutionContext resolutionContext) {
        return new MinuteOf2(getOperand().transform(resolutionContext));
    }
}