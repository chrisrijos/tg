package ua.com.fielden.platform.entity.query.generation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ua.com.fielden.platform.entity.query.fluent.enums.TokenCategory;
import ua.com.fielden.platform.entity.query.generation.elements.CompoundCondition;
import ua.com.fielden.platform.entity.query.generation.elements.GroupedConditions;
import ua.com.fielden.platform.entity.query.generation.elements.ICondition;
import ua.com.fielden.platform.utils.Pair;

public class GroupedConditionsBuilder extends AbstractTokensBuilder {

    private final boolean negated;

    protected GroupedConditionsBuilder(final AbstractTokensBuilder parent, final EntQueryGenerator queryBuilder, final Map<String, Object> paramValues, final boolean negated) {
        super(parent, queryBuilder, paramValues);
        this.negated = negated;
        setChild(new ConditionBuilder(this, queryBuilder, paramValues));
    }

    @Override
    public boolean isClosing() {
        return getLastCat().equals(TokenCategory.END_COND);
    }

    @Override
    public Pair<TokenCategory, Object> getResult() {
        if (TokenCategory.END_COND.equals(getLastCat())) {
            getTokens().remove(getSize() - 1);
        }
        final Iterator<Pair<TokenCategory, Object>> iterator = getTokens().iterator();
        final ICondition firstCondition = (ICondition) iterator.next().getValue();
        final List<CompoundCondition> otherConditions = new ArrayList<CompoundCondition>();
        for (; iterator.hasNext();) {
            final CompoundCondition subsequentCompoundCondition = (CompoundCondition) iterator.next().getValue();
            otherConditions.add(subsequentCompoundCondition);
        }
        return new Pair<TokenCategory, Object>(TokenCategory.GROUPED_CONDITIONS, new GroupedConditions(negated, firstCondition, otherConditions));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (negated ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof GroupedConditionsBuilder))
            return false;
        final GroupedConditionsBuilder other = (GroupedConditionsBuilder) obj;
        if (negated != other.negated)
            return false;
        return true;
    }
}