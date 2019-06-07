package ua.com.fielden.platform.eql.stage1.builders;

import ua.com.fielden.platform.entity.query.fluent.enums.TokenCategory;
import ua.com.fielden.platform.utils.Pair;

abstract class ThreeArgumentsFunctionBuilder extends AbstractTokensBuilder {

    protected ThreeArgumentsFunctionBuilder(final AbstractTokensBuilder parent, final EntQueryGenerator queryBuilder) {
        super(parent, queryBuilder);
    }

    @Override
    public boolean isClosing() {
        return getSize() == 3;
    }

    abstract Object getModel();

    @Override
    public Pair<TokenCategory, Object> getResult() {
        return new Pair<TokenCategory, Object>(TokenCategory.FUNCTION_MODEL, getModel());
    }

}
