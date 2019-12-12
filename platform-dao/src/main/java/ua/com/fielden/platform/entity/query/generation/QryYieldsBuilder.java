package ua.com.fielden.platform.entity.query.generation;

import java.util.Map;

import ua.com.fielden.platform.entity.query.fluent.enums.TokenCategory;
import ua.com.fielden.platform.entity.query.generation.elements.Yield;
import ua.com.fielden.platform.entity.query.generation.elements.Yields;
import ua.com.fielden.platform.utils.IDates;
import ua.com.fielden.platform.utils.Pair;

public class QryYieldsBuilder extends AbstractTokensBuilder {

    protected QryYieldsBuilder(final EntQueryGenerator queryBuilder, final Map<String, Object> paramValues, final IDates dates) {
        super(null, queryBuilder, paramValues, dates);
    }

    @Override
    public boolean isClosing() {
        return false;
    }

    // TODO handle yield().entity(String joinAlias) properly

    public Yields getModel() {
        if (getChild() != null && getSize() == 0) {
            finaliseChild();
            //throw new RuntimeException("Unable to produce result - unfinished model state!");
        }

        final Yields result = new Yields();
        for (final Pair<TokenCategory, Object> pair : getTokens()) {
            final Yield yield = (Yield) pair.getValue();

            result.addYield(yield);
        }

        return result;
    }

    @Override
    public Pair<TokenCategory, Object> getResult() {
        throw new RuntimeException("Not applicable!");
    }
}
