package ua.com.fielden.platform.eql.stage1.elements;

import ua.com.fielden.platform.eql.meta.TransformatorToS2;
import ua.com.fielden.platform.eql.stage2.elements.MinOf2;
import ua.com.fielden.platform.eql.stage2.elements.ISingleOperand2;

public class MinOf1 extends SingleOperandFunction1<MinOf2> {

    public MinOf1(final ISingleOperand1<? extends ISingleOperand2> operand) {
        super(operand);
    }

    @Override
    public MinOf2 transform(final TransformatorToS2 resolver) {
        return new MinOf2(getOperand().transform(resolver));
    }
}