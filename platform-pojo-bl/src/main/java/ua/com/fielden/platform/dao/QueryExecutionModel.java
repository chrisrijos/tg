package ua.com.fielden.platform.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.EntityAggregates;
import ua.com.fielden.platform.entity.query.fluent.ValuePreprocessor;
import ua.com.fielden.platform.entity.query.fluent.fetch;
import ua.com.fielden.platform.entity.query.model.AggregatedResultQueryModel;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.entity.query.model.OrderingModel;
import ua.com.fielden.platform.entity.query.model.QueryModel;

public final class QueryExecutionModel<T extends AbstractEntity<?>, Q extends QueryModel<T>> {
    private final Q queryModel;
    private final OrderingModel orderModel;
    private final fetch<T> fetchModel;
    private final Map<String, Object> paramValues;
    private final boolean lightweight;
    transient private final ValuePreprocessor valuePreprocessor = new ValuePreprocessor();
    transient private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * A convenient copy method.
     * 
     * @return
     */
    public QueryExecutionModel<T, Q> copy() {
        return new QueryExecutionModel<T, Q>(this.queryModel, this.orderModel, this.fetchModel, this.paramValues, this.lightweight);
    }
    
    protected QueryExecutionModel() {
        queryModel = null;
        orderModel = null;
        fetchModel = null;
        paramValues = null;
        lightweight = false;
    }
    
    protected QueryExecutionModel(final Q queryModel, final OrderingModel orderModel, final fetch<T> fetchModel, final Map<String, Object> paramValues, final boolean lightweight) {
        this.queryModel = queryModel;
        this.orderModel = orderModel;
        this.fetchModel = fetchModel;
        this.paramValues = new HashMap<String, Object>();
        this.paramValues.putAll(paramValues);
        this.lightweight = lightweight;
    }

    private QueryExecutionModel(final Builder<T, Q> builder) {
        queryModel = builder.queryModel;
        orderModel = builder.orderModel;
        fetchModel = builder.fetchModel;
        paramValues = preprocessParamValues(builder.paramValues);
        lightweight = builder.lightweight;
        logger.debug(this);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("\nQEM");
        sb.append("\n  fetch:" + (fetchModel != null ? fetchModel : ""));
        sb.append("\n  query:" + queryModel);
        sb.append("\n  order:" + (orderModel != null ? orderModel : ""));
        sb.append("\n  param:" + (paramValues.size() > 0 ? paramValues : ""));
        sb.append("\n  light: " + lightweight);
        sb.append("\n");
        return sb.toString();
    }

    private Map<String, Object> preprocessParamValues(final Map<String, Object> paramValues) {
        final Map<String, Object> result = new HashMap<String, Object>();
        for (final Map.Entry<String, Object> entry : paramValues.entrySet()) {
            result.put(entry.getKey(), valuePreprocessor.apply(entry.getValue()));
        }
        return result;
    }

    public Q getQueryModel() {
        return queryModel;
    }

    public OrderingModel getOrderModel() {
        return orderModel;
    }

    public fetch<T> getFetchModel() {
        return fetchModel;
    }

    public Map<String, Object> getParamValues() {
        return Collections.unmodifiableMap(paramValues);
    }

    public boolean isLightweight() {
        return lightweight;
    }

    public static <E extends AbstractEntity<?>> Builder<E, EntityResultQueryModel<E>> from(final EntityResultQueryModel<E> queryModel) {
        return new Builder<E, EntityResultQueryModel<E>>(queryModel);
    }

    public static Builder<EntityAggregates, AggregatedResultQueryModel> from(final AggregatedResultQueryModel queryModel) {
        return new Builder<EntityAggregates, AggregatedResultQueryModel>(queryModel);
    }

    public QueryExecutionModel<T, Q> lightweight() {
        return new QueryExecutionModel<T, Q>(this.queryModel, this.orderModel, this.fetchModel, this.paramValues, true);
    }
    
    public static class Builder<T extends AbstractEntity<?>, Q extends QueryModel<T>> {
        private Q queryModel;
        private OrderingModel orderModel;
        private fetch<T> fetchModel;
        private Map<String, Object> paramValues = new HashMap<String, Object>();
        private boolean lightweight = false;

        public QueryExecutionModel<T, Q> model() {
            return new QueryExecutionModel<T, Q>(this);
        }

        private Builder(final EntityResultQueryModel<T> queryModel) {
            this.queryModel = (Q) queryModel;
        }

        private Builder(final AggregatedResultQueryModel queryModel) {
            this.queryModel = (Q) queryModel;
        }

        public Builder<T, Q> with(final OrderingModel val) {
            orderModel = val;
            return this;
        }

        public Builder<T, Q> with(final fetch<T> val) {
            fetchModel = val;
            return this;
        }

        public Builder<T, Q> with(final Map<String, Object> val) {
            paramValues.putAll(val);
            return this;
        }

        public Builder<T, Q> with(final String name, final Object value) {
            paramValues.put(name, value);
            return this;
        }

        public Builder<T, Q> lightweight() {
            lightweight = true;
            return this;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fetchModel == null) ? 0 : fetchModel.hashCode());
        result = prime * result + (lightweight ? 1231 : 1237);
        result = prime * result + ((orderModel == null) ? 0 : orderModel.hashCode());
        result = prime * result + ((paramValues == null) ? 0 : paramValues.hashCode());
        result = prime * result + ((queryModel == null) ? 0 : queryModel.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof QueryExecutionModel)) {
            return false;
        }

        final QueryExecutionModel<?, ?> that = (QueryExecutionModel<?, ?>) obj;
        if (fetchModel == null) {
            if (that.fetchModel != null) {
                return false;
            }
        } else if (!fetchModel.equals(that.fetchModel)) {
            return false;
        }
        if (lightweight != that.lightweight) {
            return false;
        }
        if (orderModel == null) {
            if (that.orderModel != null) {
                return false;
            }
        } else if (!orderModel.equals(that.orderModel)) {
            return false;
        }
        if (paramValues == null) {
            if (that.paramValues != null) {
                return false;
            }
        } else if (!paramValues.equals(that.paramValues)) {
            return false;
        }
        if (queryModel == null) {
            if (that.queryModel != null) {
                return false;
            }
        } else if (!queryModel.equals(that.queryModel)) {
            return false;
        }
        return true;
    }
}