package ua.com.fielden.platform.eql.stage1.elements;

import ua.com.fielden.platform.eql.meta.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage2.elements.HourOf2;
import ua.com.fielden.platform.eql.stage2.elements.ISingleOperand2;

public class HourOf1 extends SingleOperandFunction1<HourOf2> {

    public HourOf1(final ISingleOperand1<? extends ISingleOperand2> operand) {
        super(operand);
    }

    @Override
    public HourOf2 transform(final PropsResolutionContext resolutionContext) {
        return new HourOf2(getOperand().transform(resolutionContext));
    }
}