package ua.com.fielden.platform.eql.stage1.elements.functions;

import static ua.com.fielden.platform.types.tuples.T2.t2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ua.com.fielden.platform.entity.query.fluent.ITypeCast;
import ua.com.fielden.platform.eql.stage1.elements.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage1.elements.TransformationResult;
import ua.com.fielden.platform.eql.stage1.elements.conditions.ICondition1;
import ua.com.fielden.platform.eql.stage1.elements.operands.ISingleOperand1;
import ua.com.fielden.platform.eql.stage2.elements.conditions.ICondition2;
import ua.com.fielden.platform.eql.stage2.elements.functions.CaseWhen2;
import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;
import ua.com.fielden.platform.eql.stage3.elements.conditions.ICondition3;
import ua.com.fielden.platform.eql.stage3.elements.operands.ISingleOperand3;
import ua.com.fielden.platform.types.tuples.T2;

public class CaseWhen1 extends AbstractFunction1<CaseWhen2> {

    private List<T2<ICondition1<? extends ICondition2<?>>, ISingleOperand1<? extends ISingleOperand2<?>>>> whenThenPairs = new ArrayList<>();
    public final ISingleOperand1<? extends ISingleOperand2<?>> elseOperand;
    private final ITypeCast typeCast;

    public CaseWhen1(final List<T2<ICondition1<? extends ICondition2<?>>, ISingleOperand1<? extends ISingleOperand2<?>>>> whenThenPairs, final ISingleOperand1<? extends ISingleOperand2<?>> elseOperand, final ITypeCast typeCast) {
        this.whenThenPairs.addAll(whenThenPairs);
        this.elseOperand = elseOperand;
        this.typeCast = typeCast;
    }

    @Override
    public TransformationResult<CaseWhen2> transform(final PropsResolutionContext context) {
        final List<T2<ICondition2<? extends ICondition3>, ISingleOperand2<? extends ISingleOperand3>>> transformedWhenThenPairs = new ArrayList<>();
        PropsResolutionContext currentResolutionContext = context;
        for (final T2<ICondition1<? extends ICondition2<?>>, ISingleOperand1<? extends ISingleOperand2<?>>> pair : whenThenPairs) {
            final TransformationResult<? extends ICondition2<? extends ICondition3>> conditionTr = pair._1.transform(currentResolutionContext);
            currentResolutionContext = conditionTr.updatedContext;
            final TransformationResult<? extends ISingleOperand2<? extends ISingleOperand3>> operandTr = pair._2.transform(currentResolutionContext);
            currentResolutionContext = operandTr.updatedContext;
            transformedWhenThenPairs.add(t2(conditionTr.item, operandTr.item));
        }
        final TransformationResult<? extends ISingleOperand2<? extends ISingleOperand3>> elseOperandTr = elseOperand.transform(currentResolutionContext);
        
        return new TransformationResult<CaseWhen2>(new CaseWhen2(transformedWhenThenPairs, elseOperandTr.item, typeCast), elseOperandTr.updatedContext);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elseOperand == null) ? 0 : elseOperand.hashCode());
        result = prime * result + ((typeCast == null) ? 0 : typeCast.hashCode());
        result = prime * result + whenThenPairs.hashCode();
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
        
        return Objects.equals(whenThenPairs, other.whenThenPairs) && Objects.equals(elseOperand, other.elseOperand) && Objects.equals(typeCast, other.typeCast);
    }
}