package ua.com.fielden.platform.entity.query.generation;

import java.util.Map;

import ua.com.fielden.platform.entity.query.generation.elements.MonthOf;

public class MonthOfBuilder extends OneArgumentFunctionBuilder {

    protected MonthOfBuilder(final AbstractTokensBuilder parent, final EntQueryGenerator queryBuilder, final Map<String, Object> paramValues) {
        super(parent, queryBuilder, paramValues);
    }

    @Override
    Object getModel() {
        return new MonthOf(getModelForSingleOperand(firstCat(), firstValue()), getDbVersion());
    }
}
