package ua.com.fielden.platform.eql.stage1.elements;

import ua.com.fielden.platform.eql.meta.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage2.elements.CountOf2;
import ua.com.fielden.platform.eql.stage2.elements.ISingleOperand2;

public class CountOf1 extends SingleOperandFunction1<CountOf2> {
    private final boolean distinct;

    public CountOf1(final ISingleOperand1<? extends ISingleOperand2> operand, final boolean distinct) {
        super(operand);
        this.distinct = distinct;
    }

    @Override
    public CountOf2 transform(final PropsResolutionContext resolutionContext) {
        return new CountOf2(getOperand().transform(resolutionContext), distinct);
    }
}