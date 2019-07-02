package ua.com.fielden.platform.eql.stage3.elements.functions;

import ua.com.fielden.platform.eql.stage3.elements.operands.ISingleOperand3;

public class RoundTo3 extends TwoOperandsFunction3 {

    public RoundTo3(final ISingleOperand3 operand1, final ISingleOperand3 operand2) {
        super(operand1, operand2);
    }

    @Override
    public String sql() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = super.hashCode();
        return prime * result + RoundTo3.class.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || super.equals(obj) && obj instanceof RoundTo3;
    }
}