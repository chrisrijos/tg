package ua.com.fielden.platform.eql.stage3.elements.operands;

import java.util.Objects;

import ua.com.fielden.platform.entity.query.DbVersion;

public class QueryBasedSet3 implements ISetOperand3 {
    private final SubQuery3 model;

    public QueryBasedSet3(final SubQuery3 model) {
        this.model = model;
    }

    @Override
    public String sql(final DbVersion dbVersion) {
        return "(" + model.sql(dbVersion) + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + model.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof QueryBasedSet3)) {
            return false;
        }
        
        final QueryBasedSet3 other = (QueryBasedSet3) obj;
        
        return Objects.equals(model, other.model);
    }
}