package ua.com.fielden.platform.eql.s1.processing;

import ua.com.fielden.platform.entity.query.fluent.enums.DateIntervalUnit;
import ua.com.fielden.platform.eql.s1.elements.CountDateInterval1;

public class CountDateIntervalBuilder1 extends ThreeArgumentsFunctionBuilder1 {

    protected CountDateIntervalBuilder1(final AbstractTokensBuilder1 parent, final EntQueryGenerator1 queryBuilder) {
        super(parent, queryBuilder);
    }

    @Override
    Object getModel() {
        return new CountDateInterval1((DateIntervalUnit) firstValue(), getModelForSingleOperand(secondCat(), secondValue()), getModelForSingleOperand(thirdCat(), thirdValue()));
    }
}