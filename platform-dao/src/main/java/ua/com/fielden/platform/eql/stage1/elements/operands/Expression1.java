package ua.com.fielden.platform.eql.stage1.elements.operands;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage2.elements.operands.CompoundSingleOperand2;
import ua.com.fielden.platform.eql.stage2.elements.operands.Expression2;
import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;

public class Expression1 implements ISingleOperand1<Expression2> {

    public final ISingleOperand1<? extends ISingleOperand2<?>> first;
    private final List<CompoundSingleOperand1> items;

    public Expression1(final ISingleOperand1<? extends ISingleOperand2<?>> first, final List<CompoundSingleOperand1> items) {
        this.first = first;
        this.items = items;
    }

    @Override
    public TransformationResult<Expression2> transform(final PropsResolutionContext context) {
        final List<CompoundSingleOperand2> transformed = new ArrayList<>();
        final TransformationResult<? extends ISingleOperand2<?>> firstTr = first.transform(context);
        PropsResolutionContext currentResolutionContext = firstTr.updatedContext;
        for (final CompoundSingleOperand1 item : items) {
            final TransformationResult<? extends ISingleOperand2<?>> itemTr = item.operand.transform(currentResolutionContext);
            transformed.add(new CompoundSingleOperand2(itemTr.item, item.operator));
            currentResolutionContext = itemTr.updatedContext;
        }
        return new TransformationResult<Expression2>(new Expression2(firstTr.item, transformed), currentResolutionContext);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + first.hashCode();
        result = prime * result + items.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Expression1)) {
            return false;
        }
        final Expression1 other = (Expression1) obj;
        
        return Objects.equals(first, other.first) && Objects.equals(items, other.items);
    }
}