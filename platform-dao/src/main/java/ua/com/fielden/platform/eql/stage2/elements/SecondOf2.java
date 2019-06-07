package ua.com.fielden.platform.eql.stage2.elements;

import ua.com.fielden.platform.eql.stage2.elements.ISingleOperand2;

public class SecondOf2 extends SingleOperandFunction2 {

    public SecondOf2(final ISingleOperand2 operand) {
        super(operand);
    }

    @Override
    public Class type() {
        return Integer.class;
    }
}