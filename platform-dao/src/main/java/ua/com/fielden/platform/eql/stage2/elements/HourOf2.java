package ua.com.fielden.platform.eql.stage2.elements;

public class HourOf2 extends SingleOperandFunction2 {

    public HourOf2(final ISingleOperand2 operand) {
        super(operand);
    }

    @Override
    public String type() {
        return Integer.class.getName();
    }
}