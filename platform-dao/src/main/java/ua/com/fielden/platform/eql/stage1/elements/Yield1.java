package ua.com.fielden.platform.eql.stage1.elements;

import java.util.Objects;

import ua.com.fielden.platform.eql.stage1.elements.operands.ISingleOperand1;
import ua.com.fielden.platform.eql.stage2.elements.Yield2;
import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;

public class Yield1 {
    public final ISingleOperand1<? extends ISingleOperand2<?>> operand;
    public final String alias;
    public final boolean hasRequiredHint;

    public Yield1(final ISingleOperand1<? extends ISingleOperand2<?>> operand, final String alias, final boolean hasRequiredHint) {
        this.operand = operand;
        this.alias = alias;
        this.hasRequiredHint = hasRequiredHint;
    }
    
    public TransformationResult<Yield2> transform(final PropsResolutionContext context, final String sourceId) {
        final TransformationResult<? extends ISingleOperand2<?>> operandTr = operand.transform(context, sourceId);
        return new TransformationResult<Yield2>(new Yield2(operandTr.item, alias, hasRequiredHint), operandTr.updatedContext);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alias == null) ? 0 : alias.hashCode());
        result = prime * result + (hasRequiredHint ? 1231 : 1237);
        result = prime * result + operand.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
    
        if (!(obj instanceof Yield1)) {
            return false;
        }
        
        final Yield1 other = (Yield1) obj;

        return Objects.equals(operand, other.operand) && Objects.equals(alias, other.alias) && (hasRequiredHint == other.hasRequiredHint);
    }
}