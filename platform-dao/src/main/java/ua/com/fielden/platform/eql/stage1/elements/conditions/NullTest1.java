package ua.com.fielden.platform.eql.stage1.elements.conditions;

import java.util.Objects;

import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.operands.ISingleOperand1;
import ua.com.fielden.platform.eql.stage2.elements.conditions.NullTest2;
import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;

public class NullTest1 implements ICondition1<NullTest2> {
    private final ISingleOperand1<? extends ISingleOperand2<?>> operand;
    private final boolean negated;

    public NullTest1(final ISingleOperand1<? extends ISingleOperand2<?>> operand, final boolean negated) {
        this.operand = operand;
        this.negated = negated;
    }

    @Override
    public NullTest2 transform(final PropsResolutionContext context) {
        return new NullTest2(operand.transform(context), negated);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (negated ? 1231 : 1237);
        result = prime * result + operand.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NullTest1)) {
            return false;
        }
        
        final NullTest1 other = (NullTest1) obj;
        
        return Objects.equals(negated, other.negated) && Objects.equals(operand, other.operand);
    }
}