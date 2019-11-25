package ua.com.fielden.platform.entity.query.generation;

import java.util.Map;

import ua.com.fielden.platform.entity.query.generation.elements.MinuteOf;
import ua.com.fielden.platform.utils.IUniversalConstants;

public class MinuteOfBuilder extends OneArgumentFunctionBuilder {

    protected MinuteOfBuilder(final AbstractTokensBuilder parent, final EntQueryGenerator queryBuilder, final Map<String, Object> paramValues, final IUniversalConstants universalConstants) {
        super(parent, queryBuilder, paramValues, universalConstants);
    }

    @Override
    Object getModel() {
        return new MinuteOf(getModelForSingleOperand(firstCat(), firstValue()), getDbVersion());
    }
}
