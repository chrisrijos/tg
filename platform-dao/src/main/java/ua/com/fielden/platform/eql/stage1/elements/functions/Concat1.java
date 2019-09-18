package ua.com.fielden.platform.eql.stage1.elements.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage1.elements.operands.ISingleOperand1;
import ua.com.fielden.platform.eql.stage2.elements.functions.Concat2;
import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;
import ua.com.fielden.platform.eql.stage3.elements.operands.ISingleOperand3;

public class Concat1 extends AbstractFunction1<Concat2> {

    private final List<ISingleOperand1<? extends ISingleOperand2<?>>> operands;

    public Concat1(final List<ISingleOperand1<? extends ISingleOperand2<?>>> operands) {
        this.operands = operands;
    }

    @Override
    public TransformationResult<Concat2> transform(final PropsResolutionContext context) {
        final List<ISingleOperand2<? extends ISingleOperand3>> transformed = new ArrayList<>();
        PropsResolutionContext currentResolutionContext = context;
        for (final ISingleOperand1<? extends ISingleOperand2<? extends ISingleOperand3>> operand : operands) {
            final TransformationResult<? extends ISingleOperand2<? extends ISingleOperand3>> operandTr = operand.transform(context);
            transformed.add(operandTr.item);
            currentResolutionContext = operandTr.updatedContext;
        }
        return new TransformationResult<Concat2>(new Concat2(transformed), currentResolutionContext);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + operands.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Concat1)) {
            return false;
        }
        
        final Concat1 other = (Concat1) obj;
        
        return Objects.equals(operands, other.operands);
    }
}