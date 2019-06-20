package ua.com.fielden.platform.eql.stage2.elements.conditions;

import java.util.Objects;

import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;

public class NullTest2 extends AbstractCondition2 {
    public final ISingleOperand2 operand;
    private final boolean negated;

    public NullTest2(final ISingleOperand2 operand, final boolean negated) {
        this.operand = operand;
        this.negated = negated;
    }

    @Override
    public boolean ignore() {
        return operand.ignore();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (negated ? 1231 : 1237);
        result = prime * result + ((operand == null) ? 0 : operand.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NullTest2)) {
            return false;
        }
        
        final NullTest2 other = (NullTest2) obj;
        
        return Objects.equals(negated, other.negated) && Objects.equals(operand, other.operand);
    }
}