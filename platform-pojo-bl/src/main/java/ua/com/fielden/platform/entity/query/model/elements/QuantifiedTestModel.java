package ua.com.fielden.platform.entity.query.model.elements;

import java.util.ArrayList;
import java.util.List;

import ua.com.fielden.platform.entity.query.model.structure.ICondition;
import ua.com.fielden.platform.entity.query.model.structure.ISingleOperand;
import ua.com.fielden.platform.entity.query.tokens.QueryTokens;

public class QuantifiedTestModel implements ICondition {
    private final ISingleOperand leftOperand;
    private final EntQuery rightOperand;
    private final QueryTokens quantifier;
    private final QueryTokens operator;

    public QuantifiedTestModel(final ISingleOperand leftOperand, final QueryTokens operator, final QueryTokens quantifier, final EntQuery rightOperand) {
	this.leftOperand = leftOperand;
	this.rightOperand = rightOperand;
	this.operator = operator;
	this.quantifier = quantifier;
    }

    @Override
    public List<String> getPropNames() {
	final List<String> result = new ArrayList<String>();

	result.addAll(leftOperand.getPropNames());
	result.addAll(rightOperand.getPropNames());

	return result;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((leftOperand == null) ? 0 : leftOperand.hashCode());
	result = prime * result + ((operator == null) ? 0 : operator.hashCode());
	result = prime * result + ((quantifier == null) ? 0 : quantifier.hashCode());
	result = prime * result + ((rightOperand == null) ? 0 : rightOperand.hashCode());
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof QuantifiedTestModel))
	    return false;
	final QuantifiedTestModel other = (QuantifiedTestModel) obj;
	if (leftOperand == null) {
	    if (other.leftOperand != null)
		return false;
	} else if (!leftOperand.equals(other.leftOperand))
	    return false;
	if (operator != other.operator)
	    return false;
	if (quantifier != other.quantifier)
	    return false;
	if (rightOperand == null) {
	    if (other.rightOperand != null)
		return false;
	} else if (!rightOperand.equals(other.rightOperand))
	    return false;
	return true;
    }
}
