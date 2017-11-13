package ua.com.fielden.platform.eql.stage1.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ua.com.fielden.platform.entity.query.fluent.enums.ArithmeticalOperator;
import ua.com.fielden.platform.entity.query.fluent.enums.TokenCategory;
import ua.com.fielden.platform.eql.stage1.elements.CompoundSingleOperand1;
import ua.com.fielden.platform.eql.stage1.elements.Expression1;
import ua.com.fielden.platform.eql.stage1.elements.ISingleOperand1;
import ua.com.fielden.platform.utils.Pair;

public class ExpressionBuilder extends AbstractTokensBuilder {

    protected ExpressionBuilder(final AbstractTokensBuilder parent, final EntQueryGenerator queryBuilder) {
        super(parent, queryBuilder);
    }

    @Override
    public boolean isClosing() {
        return TokenCategory.END_EXPR.equals(getLastCat());
    }

    @Override
    public Pair<TokenCategory, Object> getResult() {
        if (TokenCategory.END_EXPR.equals(getLastCat())) {
            getTokens().remove(getSize() - 1);
        }
        final Iterator<Pair<TokenCategory, Object>> iterator = getTokens().iterator();
        final Pair<TokenCategory, Object> firstOperandPair = iterator.next();
        final ISingleOperand1 firstOperand = getModelForSingleOperand(firstOperandPair.getKey(), firstOperandPair.getValue());
        final List<CompoundSingleOperand1> items = new ArrayList<CompoundSingleOperand1>();
        for (; iterator.hasNext();) {
            final ArithmeticalOperator operator = (ArithmeticalOperator) iterator.next().getValue();
            final Pair<TokenCategory, Object> subsequentOperandPair = iterator.next();
            final ISingleOperand1 subsequentOperand = getModelForSingleOperand(subsequentOperandPair.getKey(), subsequentOperandPair.getValue());

            items.add(new CompoundSingleOperand1(subsequentOperand, operator));
        }

        return new Pair<TokenCategory, Object>(TokenCategory.EXPR, new Expression1(firstOperand, items));
    }
}
