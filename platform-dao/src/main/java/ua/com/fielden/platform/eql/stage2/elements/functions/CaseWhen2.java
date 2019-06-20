package ua.com.fielden.platform.eql.stage2.elements.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ua.com.fielden.platform.eql.stage2.elements.conditions.ICondition2;
import ua.com.fielden.platform.eql.stage2.elements.operands.ISingleOperand2;
import ua.com.fielden.platform.utils.Pair;

public class CaseWhen2 extends AbstractFunction2 {

    private List<Pair<ICondition2, ISingleOperand2>> whenThenPairs = new ArrayList<Pair<ICondition2, ISingleOperand2>>();
    private final ISingleOperand2 elseOperand;

    public CaseWhen2(final List<Pair<ICondition2, ISingleOperand2>> whenThenPairs, final ISingleOperand2 elseOperand) {
        this.whenThenPairs.addAll(whenThenPairs);
        this.elseOperand = elseOperand;
    }

    @Override
    public Class type() {
        // TODO EQL
        return whenThenPairs.get(0).getValue().type();
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
        
        if (!(obj instanceof CaseWhen2)) {
            return false;
        }

        final CaseWhen2 other = (CaseWhen2) obj;

        return Objects.equals(whenThenPairs, other.whenThenPairs) && Objects.equals(elseOperand, other.elseOperand);
    }
}