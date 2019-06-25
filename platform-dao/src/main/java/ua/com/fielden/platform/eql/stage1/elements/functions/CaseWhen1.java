package ua.com.fielden.platform.eql.stage1.elements.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage1.elements.conditions.ICondition1;
import ua.com.fielden.platform.eql.stage1.elements.operands.ISingleOperand1;
import ua.com.fielden.platform.eql.stage2.elements.conditions.ICondition2;
import ua.com.fielden.platform.eql.stage2.elements.functions.CaseWhen2;
import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;
import ua.com.fielden.platform.utils.Pair;

public class CaseWhen1 extends AbstractFunction1<CaseWhen2> {

    private List<Pair<ICondition1<? extends ICondition2>, ISingleOperand1<? extends ISingleOperand2>>> whenThenPairs = new ArrayList<>();
    public final ISingleOperand1<? extends ISingleOperand2> elseOperand;

    public CaseWhen1(final List<Pair<ICondition1<? extends ICondition2>, ISingleOperand1<? extends ISingleOperand2>>> whenThenPairs, final ISingleOperand1<? extends ISingleOperand2> elseOperand) {
        this.whenThenPairs.addAll(whenThenPairs);
        this.elseOperand = elseOperand;
    }

    @Override
    public TransformationResult<CaseWhen2> transform(final PropsResolutionContext resolutionContext) {
        final List<Pair<ICondition2, ISingleOperand2>> transformedWhenThenPairs = new ArrayList<>();
        PropsResolutionContext currentResolutionContext = resolutionContext;
        for (final Pair<ICondition1<? extends ICondition2>, ISingleOperand1<? extends ISingleOperand2>> pair : whenThenPairs) {
            final TransformationResult<? extends ICondition2> conditionTransformationResult = pair.getKey().transform(currentResolutionContext);
            currentResolutionContext = conditionTransformationResult.getUpdatedContext();
            final TransformationResult<? extends ISingleOperand2> operandTransformationResult = pair.getValue().transform(currentResolutionContext);
            currentResolutionContext = operandTransformationResult.getUpdatedContext();
            transformedWhenThenPairs.add(new Pair<ICondition2, ISingleOperand2>(conditionTransformationResult.getItem(), operandTransformationResult.getItem()));
        }
        final TransformationResult<? extends ISingleOperand2> elseOperandTransformationResult = elseOperand.transform(currentResolutionContext);
        
        return new TransformationResult<CaseWhen2>(new CaseWhen2(transformedWhenThenPairs, elseOperandTransformationResult.getItem()), elseOperandTransformationResult.getUpdatedContext());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elseOperand == null) ? 0 : elseOperand.hashCode());
        result = prime * result + ((whenThenPairs == null) ? 0 : whenThenPairs.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof CaseWhen1)) {
            return false;
        }
        
        final CaseWhen1 other = (CaseWhen1) obj;
        
        return Objects.equals(whenThenPairs, other.whenThenPairs) && Objects.equals(elseOperand, other.elseOperand);
    }
}