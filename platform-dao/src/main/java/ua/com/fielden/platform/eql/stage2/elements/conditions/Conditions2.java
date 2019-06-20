package ua.com.fielden.platform.eql.stage2.elements.conditions;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Conditions2 extends AbstractCondition2 {
    private final List<List<? extends ICondition2>> allConditionsAsDnf = new ArrayList<>();
    private final boolean negated;

    public Conditions2(final boolean negated, final List<List<? extends ICondition2>> allConditions) {
        this.allConditionsAsDnf.addAll(allConditions);
        this.negated = negated;
    }
    
    public Conditions2() {
        this(false, emptyList());
    }

    @Override
    public boolean ignore() {
        return allConditionsAsDnf.isEmpty();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((allConditionsAsDnf == null) ? 0 : allConditionsAsDnf.hashCode());
        result = prime * result + (negated ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Conditions2)) {
            return false;
        }
        
        final Conditions2 other = (Conditions2) obj;
        
        return Objects.equals(allConditionsAsDnf, other.allConditionsAsDnf) && Objects.equals(negated, other.negated);
    }
}