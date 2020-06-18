package ua.com.fielden.platform.eql.stage1.builders;

import static java.util.Collections.unmodifiableMap;
import static ua.com.fielden.platform.entity.query.fluent.enums.LogicalOperator.AND;
import static ua.com.fielden.platform.entity.query.fluent.enums.TokenCategory.ORDER_TOKENS;
import static ua.com.fielden.platform.entity.query.fluent.enums.TokenCategory.QUERY_TOKEN;
import static ua.com.fielden.platform.entity.query.fluent.enums.TokenCategory.SORT_ORDER;
import static ua.com.fielden.platform.utils.CollectionUtil.listOf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.DbVersion;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.entity.query.IRetrievalModel;
import ua.com.fielden.platform.entity.query.fluent.enums.QueryTokens;
import ua.com.fielden.platform.entity.query.fluent.enums.TokenCategory;
import ua.com.fielden.platform.entity.query.model.ConditionModel;
import ua.com.fielden.platform.entity.query.model.OrderingModel;
import ua.com.fielden.platform.entity.query.model.QueryModel;
import ua.com.fielden.platform.eql.stage1.elements.EntQueryBlocks1;
import ua.com.fielden.platform.eql.stage1.elements.OrderBys1;
import ua.com.fielden.platform.eql.stage1.elements.conditions.CompoundCondition1;
import ua.com.fielden.platform.eql.stage1.elements.conditions.Conditions1;
import ua.com.fielden.platform.eql.stage1.elements.operands.ResultQuery1;
import ua.com.fielden.platform.eql.stage1.elements.operands.SourceQuery1;
import ua.com.fielden.platform.eql.stage1.elements.operands.SubQuery1;
import ua.com.fielden.platform.eql.stage1.elements.sources.IQrySource1;
import ua.com.fielden.platform.eql.stage1.elements.sources.Sources1;
import ua.com.fielden.platform.utils.IDates;
import ua.com.fielden.platform.utils.Pair;

public class EntQueryGenerator {
    public static final String NOW = "UC#NOW";

    public final DbVersion dbVersion;
    public final IDates dates;
    public final IFilter filter;
    public final String username;
    private final Map<String, Object> paramValues = new HashMap<>();

    private final int increment = 1;
    
    public EntQueryGenerator(final DbVersion dbVersion, final IFilter filter, final String username, final IDates dates, final Map<String, Object> paramValues) {
        this.dbVersion = dbVersion;
        this.filter = filter;
        this.username = username;
        this.dates = dates;
        this.paramValues.putAll(paramValues);
        if (dates != null && dates.now() != null) {
            this.paramValues.put(NOW, dates.now().toDate());
        }
    }
    
    private int contextId = 0;
    
    public int nextCondtextId() {
        contextId = contextId + increment;
        return contextId;
    }
    
    public <T extends AbstractEntity<?>, Q extends QueryModel<T>> ResultQuery1 generateEntQueryAsResultQuery(final QueryModel<T> qm, final OrderingModel orderModel, final IRetrievalModel<T> fetchModel) {
        return new ResultQuery1(parseTokensIntoComponents(qm, orderModel), qm.getResultType(), fetchModel);
    }

    public <T extends AbstractEntity<?>> SourceQuery1 generateEntQueryAsSourceQuery(final QueryModel<T> qryModel) {
        return generateEntQueryAsSourceQuery(qryModel, qryModel.getResultType(), true);
    }
        
    public <T extends AbstractEntity<?>> SourceQuery1 generateEntQueryAsSyntheticEntityQuery(final QueryModel<T> qryModel, final Class<T> resultType) {
        return generateEntQueryAsSourceQuery(qryModel, resultType, false);
    }
    
    private <T extends AbstractEntity<?>> SourceQuery1 generateEntQueryAsSourceQuery(final QueryModel<T> qryModel, final Class<T> resultType, final boolean isCorrelated) {
        return new SourceQuery1(parseTokensIntoComponents(qryModel, null), resultType, true);
    }

    public SubQuery1 generateEntQueryAsSubquery(final QueryModel<?> qryModel) {
        return new SubQuery1(parseTokensIntoComponents(qryModel, null), qryModel.getResultType());
    }

    private EntQueryBlocks1 parseTokensIntoComponents(final QueryModel<?> qryModel, final OrderingModel orderModel) {
        final QrySourcesBuilder from = new QrySourcesBuilder(this);
        final ConditionsBuilder where = new ConditionsBuilder(null, this);
        final QryYieldsBuilder select = new QryYieldsBuilder(this);
        final QryGroupsBuilder groupBy = new QryGroupsBuilder(this);

        ITokensBuilder active = null;

        for (final Pair<TokenCategory, Object> pair : qryModel.getTokens()) {
            if (QUERY_TOKEN != pair.getKey()) {
                if (active != null) {
                    active.add(pair.getKey(), pair.getValue());
                }
            } else {
                switch ((QueryTokens) pair.getValue()) {
                case WHERE:
                    active = where;
                    where.setChild(new ConditionBuilder(where, this));
                    break;
                case FROM:
                    active = from;
                    break;
                case YIELD:
                    active = select;
                    select.setChild(new YieldBuilder(select, this));
                    break;
                case GROUP_BY:
                    active = groupBy;
                    groupBy.setChild(new GroupBuilder(groupBy, this));
                    break;
                default:
                    break;
                }
            }
        }

        final Sources1 fromModel = from.getModel();
        final Conditions1 whereModel = addFilteringCondition(where.getModel(), qryModel.isFilterable(), filter, username, fromModel.main);

        return new EntQueryBlocks1(fromModel, whereModel, select.getModel(), groupBy.getModel(), produceOrderBys(orderModel), qryModel.isYieldAll());
    }
    
    private Conditions1 addFilteringCondition(final Conditions1 originalConditions, final boolean filterable, final IFilter filter, final String username, final IQrySource1 mainSource) {
        if (filterable && filter != null) {
            final ConditionModel filteringCondition = filter.enhance(mainSource.sourceType(), mainSource.getAlias(), username);
            if (filteringCondition != null) {
                // LOGGER.debug("\nApplied user-driven-filter to query main source type [" + mainSource.sourceType().getSimpleName() + "]");
                final Conditions1 userDateFilteringCondition = new StandAloneConditionBuilder(this, filteringCondition, false).getModel();
                return new Conditions1(false, userDateFilteringCondition, listOf(new CompoundCondition1(AND, originalConditions)));
            }
        }

        return originalConditions;
    }

    private List<Pair<TokenCategory, Object>> linearizeTokens(final List<Pair<TokenCategory, Object>> tokens) {
        final List<Pair<TokenCategory, Object>> result = new ArrayList<>();
        for (final Pair<TokenCategory, Object> pair : tokens) {
            if (ORDER_TOKENS == pair.getKey()) {
                result.addAll(linearizeTokens(((OrderingModel) pair.getValue()).getTokens()));
            } else {
                result.add(pair);
            }
        }

        return result;
    }

    private OrderBys1 produceOrderBys(final OrderingModel orderModel) {
        final QryOrderingsBuilder orderBy = new QryOrderingsBuilder(null, this);

        if (orderModel != null) {
            final List<Pair<TokenCategory, Object>> linearizedTokens = linearizeTokens(orderModel.getTokens());
            for (final Iterator<Pair<TokenCategory, Object>> iterator = linearizedTokens.iterator(); iterator.hasNext();) {
                final Pair<TokenCategory, Object> pair = iterator.next();
                if (SORT_ORDER == pair.getKey()) {
                    orderBy.add(pair.getKey(), pair.getValue());
                    if (iterator.hasNext()) {
                        orderBy.setChild(new OrderByBuilder(orderBy, this));
                    }
                } else {
                    if (orderBy.getChild() == null) {
                        orderBy.setChild(new OrderByBuilder(orderBy, this));
                    }
                    orderBy.add(pair.getKey(), pair.getValue());
                }
            }
        }
        return orderBy.getModel();
    }
    
    public Map<String, Object> getParamValues() {
        return unmodifiableMap(paramValues);
    }
}