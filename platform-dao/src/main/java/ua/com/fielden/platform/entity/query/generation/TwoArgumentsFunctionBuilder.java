package ua.com.fielden.platform.entity.query.generation;

import java.util.Map;

import ua.com.fielden.platform.entity.query.fluent.enums.TokenCategory;
import ua.com.fielden.platform.utils.IDates;
import ua.com.fielden.platform.utils.Pair;

abstract class TwoArgumentsFunctionBuilder extends AbstractTokensBuilder {

    protected TwoArgumentsFunctionBuilder(final AbstractTokensBuilder parent, final EntQueryGenerator queryBuilder, final Map<String, Object> paramValues, final IDates dates) {
        super(parent, queryBuilder, paramValues, dates);
    }

    @Override
    public boolean isClosing() {
        return getSize() == 2;
    }

    abstract Object getModel();

    @Override
    public Pair<TokenCategory, Object> getResult() {
        return new Pair<>(TokenCategory.FUNCTION_MODEL, getModel());
    }

}
