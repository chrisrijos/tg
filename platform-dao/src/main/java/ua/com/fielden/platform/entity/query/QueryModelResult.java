package ua.com.fielden.platform.entity.query;

import java.util.Map;
import java.util.SortedSet;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.generation.elements.ResultQueryYieldDetails;
import ua.com.fielden.platform.utils.EntityUtils;

public final class QueryModelResult<T extends AbstractEntity<?>> {
    private Class<T> resultType;
    private String sql;
    private Map<String, Object> paramValues;
    private final SortedSet<ResultQueryYieldDetails> yieldedPropsInfo;
    private final IRetrievalModel<T> fetchModel;


    public QueryModelResult(final Class<T> resultType, final String sql, final SortedSet<ResultQueryYieldDetails> yieldedPropsInfo, final Map<String, Object> paramValues, final IRetrievalModel<T> fetchModel) {
        this.resultType = resultType;
        this.sql = sql;
        this.paramValues = paramValues;
        this.yieldedPropsInfo = yieldedPropsInfo;
        this.fetchModel = fetchModel;
    }

    public Class<T> getResultType() {
        return resultType;
    }

    public String getSql() {
        return sql;
    }

    public Map<String, Object> getParamValues() {
        return paramValues;
    }

    public SortedSet<ResultQueryYieldDetails> getYieldedPropsInfo() {
        return yieldedPropsInfo;
    }
    
    public IRetrievalModel<T> getFetchModel() {
        return fetchModel;
    }

    public boolean idOnlyQuery() {
        return EntityUtils.isPersistedEntityType(resultType) && yieldedPropsInfo.size() == 1 && "id".equals(yieldedPropsInfo.iterator().next().getName());
    }
}