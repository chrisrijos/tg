package ua.com.fielden.platform.eql.s2.elements;

import ua.com.fielden.platform.entity.query.fluent.enums.LogicalOperator;

public class CompoundCondition2 {
    private final LogicalOperator logicalOperator;
    private final ICondition2 condition;

    //    public String sql() {
    //	return " " + logicalOperator + " " + condition.sql();
    //    }

    public CompoundCondition2(final LogicalOperator logicalOperator, final ICondition2 condition) {
        this.logicalOperator = logicalOperator;
        this.condition = condition;
    }

    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }

    public ICondition2 getCondition() {
        return condition;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((condition == null) ? 0 : condition.hashCode());
        result = prime * result + ((logicalOperator == null) ? 0 : logicalOperator.hashCode());
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
        if (!(obj instanceof CompoundCondition2)) {
            return false;
        }
        final CompoundCondition2 other = (CompoundCondition2) obj;
        if (condition == null) {
            if (other.condition != null) {
                return false;
            }
        } else if (!condition.equals(other.condition)) {
            return false;
        }
        if (logicalOperator != other.logicalOperator) {
            return false;
        }
        return true;
    }
}
