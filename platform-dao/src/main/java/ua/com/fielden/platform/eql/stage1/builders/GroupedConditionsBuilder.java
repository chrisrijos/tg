package ua.com.fielden.platform.eql.stage1.builders;

import static ua.com.fielden.platform.entity.query.fluent.enums.TokenCategory.END_COND;
import static ua.com.fielden.platform.entity.query.fluent.enums.TokenCategory.GROUPED_CONDITIONS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ua.com.fielden.platform.entity.query.fluent.enums.TokenCategory;
import ua.com.fielden.platform.eql.stage1.elements.conditions.CompoundCondition1;
import ua.com.fielden.platform.eql.stage1.elements.conditions.Conditions1;
import ua.com.fielden.platform.eql.stage1.elements.conditions.ICondition1;
import ua.com.fielden.platform.eql.stage2.elements.conditions.ICondition2;
import ua.com.fielden.platform.utils.Pair;

public class GroupedConditionsBuilder extends AbstractTokensBuilder {

    private final boolean negated;

    protected GroupedConditionsBuilder(final AbstractTokensBuilder parent, final EntQueryGenerator queryBuilder, final boolean negated) {
        super(parent, queryBuilder);
        this.negated = negated;
        setChild(new ConditionBuilder(this, queryBuilder));
    }

    @Override
    public boolean isClosing() {
        return getLastCat() == END_COND;
    }

    @Override
    public Pair<TokenCategory, Object> getResult() {
        if (isClosing()) {
            getTokens().remove(getSize() - 1);
        }
        final Iterator<Pair<TokenCategory, Object>> iterator = getTokens().iterator();
        final ICondition1<? extends ICondition2<?>> firstCondition = (ICondition1<? extends ICondition2<?>>) iterator.next().getValue();
        final List<CompoundCondition1> otherConditions = new ArrayList<>();
        for (; iterator.hasNext();) {
            final CompoundCondition1 subsequentCompoundCondition = (CompoundCondition1) iterator.next().getValue();
            otherConditions.add(subsequentCompoundCondition);
        }
        return new Pair<TokenCategory, Object>(GROUPED_CONDITIONS, new Conditions1(negated, firstCondition, otherConditions));
    }
}