package ua.com.fielden.platform.entity.query.generation.elements;

import static ua.com.fielden.platform.utils.CollectionUtil.listOf;

import java.util.ArrayList;
import java.util.List;

import ua.com.fielden.platform.entity.query.DbVersion;

public class LikeTest extends AbstractCondition {
    private final ISingleOperand leftOperand;
    private final ISingleOperand rightOperand;
    private final boolean negated;
    private final boolean caseInsensitive;

    @Override
    public String sql() {
        return leftOperand.sql() + (negated ? " NOT LIKE " : " LIKE ") + rightOperand.sql();
    }

    public LikeTest(final ISingleOperand leftOperand, final ISingleOperand rightOperand, final boolean negated, final boolean caseInsensitive) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.negated = negated;
        this.caseInsensitive = caseInsensitive;
    }

    @Override
    public boolean ignore() {
        return leftOperand.ignore() || rightOperand.ignore();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (caseInsensitive ? 1231 : 1237);
        result = prime * result + ((leftOperand == null) ? 0 : leftOperand.hashCode());
        result = prime * result + (negated ? 1231 : 1237);
        result = prime * result + ((rightOperand == null) ? 0 : rightOperand.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof LikeTest)) {
            return false;
        }
        final LikeTest other = (LikeTest) obj;
        if (caseInsensitive != other.caseInsensitive) {
            return false;
        }
        if (leftOperand == null) {
            if (other.leftOperand != null) {
                return false;
            }
        } else if (!leftOperand.equals(other.leftOperand)) {
            return false;
        }
        if (negated != other.negated) {
            return false;
        }
        if (rightOperand == null) {
            if (other.rightOperand != null) {
                return false;
            }
        } else if (!rightOperand.equals(other.rightOperand)) {
            return false;
        }
        return true;
    }

    @Override
    protected List<IPropertyCollector> getCollection() {
        return  listOf(leftOperand, rightOperand);
    }
}