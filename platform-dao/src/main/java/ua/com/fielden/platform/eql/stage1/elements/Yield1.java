package ua.com.fielden.platform.eql.stage1.elements;

import ua.com.fielden.platform.eql.meta.PropsResolutionContext;
import ua.com.fielden.platform.eql.meta.TransformationResult;
import ua.com.fielden.platform.eql.stage1.elements.operands.ISingleOperand1;
import ua.com.fielden.platform.eql.stage2.elements.Yield2;
import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;

public class Yield1 {
    public final ISingleOperand1<? extends ISingleOperand2> operand;
    public final String alias;
    public final boolean hasRequiredHint;

    public Yield1(final ISingleOperand1<? extends ISingleOperand2> operand, final String alias, final boolean hasRequiredHint) {
        this.operand = operand;
        this.alias = alias;
        this.hasRequiredHint = hasRequiredHint;
    }
    
    public Yield1(final ISingleOperand1<? extends ISingleOperand2> operand, final String alias) {
        this(operand, alias, false);
    }

    public TransformationResult<Yield2> transform(final PropsResolutionContext resolutionContext) {
        final TransformationResult<? extends ISingleOperand2> operandTransformationResult = operand.transform(resolutionContext);
        return new TransformationResult<Yield2>(new Yield2(operandTransformationResult.getItem(), alias, hasRequiredHint), operandTransformationResult.getUpdatedContext());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alias == null) ? 0 : alias.hashCode());
        result = prime * result + ((operand == null) ? 0 : operand.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Yield1)) {
            return false;
        }
        final Yield1 other = (Yield1) obj;
        if (alias == null) {
            if (other.alias != null) {
                return false;
            }
        } else if (!alias.equals(other.alias)) {
            return false;
        }
        if (operand == null) {
            if (other.operand != null) {
                return false;
            }
        } else if (!operand.equals(other.operand)) {
            return false;
        }
        return true;
    }
}