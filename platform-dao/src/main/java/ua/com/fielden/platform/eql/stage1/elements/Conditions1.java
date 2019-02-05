package ua.com.fielden.platform.eql.stage1.elements;

import static java.util.stream.Collectors.toList;
import static ua.com.fielden.platform.entity.query.fluent.enums.LogicalOperator.AND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ua.com.fielden.platform.eql.meta.PropsResolutionContext;
import ua.com.fielden.platform.eql.stage2.elements.Conditions2;
import ua.com.fielden.platform.eql.stage2.elements.ICondition2;

public class Conditions1 extends AbstractCondition1<Conditions2> {
    private final boolean negated;
    private final ICondition1<? extends ICondition2> firstCondition;
    private final List<CompoundCondition1> otherConditions = new ArrayList<>();

    public Conditions1(final boolean negated, final ICondition1<? extends ICondition2> firstCondition, final List<CompoundCondition1> otherConditions) {
        this.firstCondition = firstCondition;
        this.otherConditions.addAll(otherConditions);
        this.negated = negated;
    }

    public Conditions1(final boolean negated, final ICondition1<? extends ICondition2> firstCondition) {
        this(negated, firstCondition, Collections.<CompoundCondition1> emptyList());
    }

    public Conditions1() {
        negated = false;
        firstCondition = null;
    }

    public boolean isEmpty() {
        return firstCondition == null;
    }

    private List<List<ICondition1<? extends ICondition2>>> formDnf() {
        final List<List<ICondition1<? extends ICondition2>>> dnf = new ArrayList<>();
        List<ICondition1<? extends ICondition2>> andGroup = new ArrayList<>();

        if (firstCondition != null) {
            andGroup.add(firstCondition);
        }

        for (final CompoundCondition1 compoundCondition : otherConditions) {
            if (compoundCondition.getLogicalOperator() == AND) {
                andGroup.add(compoundCondition.getCondition());
            } else {
                if (!andGroup.isEmpty()) {
                    dnf.add(andGroup);
                }

                andGroup = new ArrayList<>();
                andGroup.add(compoundCondition.getCondition());
            }
        }

        if (!andGroup.isEmpty()) {
            dnf.add(andGroup);
        }

        return dnf;
    }

    @Override
    public Conditions2 transform(final PropsResolutionContext resolutionContext) {
        final List<List<? extends ICondition2>> transformed = formDnf().stream()
                .map(andGroup -> andGroup.stream().map(cond -> cond.transform(resolutionContext)).filter(cond -> !cond.ignore()).collect(toList()))
                .filter(andGroup -> !andGroup.isEmpty())
                .collect(toList());
        
        return new Conditions2(negated, transformed);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((firstCondition == null) ? 0 : firstCondition.hashCode());
        result = prime * result + (negated ? 1231 : 1237);
        result = prime * result + ((otherConditions == null) ? 0 : otherConditions.hashCode());
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
        if (!(obj instanceof Conditions1)) {
            return false;
        }
        final Conditions1 other = (Conditions1) obj;
        if (firstCondition == null) {
            if (other.firstCondition != null) {
                return false;
            }
        } else if (!firstCondition.equals(other.firstCondition)) {
            return false;
        }
        if (negated != other.negated) {
            return false;
        }
        if (otherConditions == null) {
            if (other.otherConditions != null) {
                return false;
            }
        } else if (!otherConditions.equals(other.otherConditions)) {
            return false;
        }
        return true;
    }
}