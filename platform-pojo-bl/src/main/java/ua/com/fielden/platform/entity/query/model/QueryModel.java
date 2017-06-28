package ua.com.fielden.platform.entity.query.model;

import static java.lang.String.format;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.TokenCategory;
import ua.com.fielden.platform.utils.Pair;

public abstract class QueryModel<T extends AbstractEntity<?>> extends AbstractModel {
    private Class<T> resultType;
    private boolean filterable = false;

    protected QueryModel() {
    }

    public QueryModel(final List<Pair<TokenCategory, Object>> tokens, final Class<T> resultType) {
        super(tokens);
        this.resultType = resultType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resultType == null) ? 0 : resultType.hashCode());
        result = prime * result + ((getTokens() == null) ? 0 : getTokens().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof QueryModel)) {
            return false;
        }
        final QueryModel<?> that = (QueryModel<?>) obj;

        if (resultType == null) {
            if (that.resultType != null) {
                return false;
            }
        } else if (!resultType.equals(that.resultType)) {
            return false;
        }
        if (getTokens() == null) {
            if (that.getTokens() != null) {
                return false;
            }
        } else if (!getTokens().equals(that.getTokens())) {
            return false;
        }
        return true;
    }

    public Class<T> getResultType() {
        return resultType;
    }

    public boolean isFilterable() {
        return filterable;
    }

    public void setFilterable(final boolean filterable) {
        this.filterable = filterable;
    }

    @Override
    public String toString() {
        return super.toString() +
                format("\n\t%s%s", StringUtils.rightPad("is filterable", 32, '.'), filterable);
    }
}