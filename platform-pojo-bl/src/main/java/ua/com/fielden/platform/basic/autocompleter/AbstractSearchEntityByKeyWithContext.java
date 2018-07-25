/**
 *
 */
package ua.com.fielden.platform.basic.autocompleter;

import static java.util.Collections.emptyMap;
import static ua.com.fielden.platform.basic.ValueMatcherUtils.createRelaxedSearchByKeyCriteriaModel;
import static ua.com.fielden.platform.entity.AbstractEntity.DESC;
import static ua.com.fielden.platform.entity.AbstractEntity.KEY;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.cond;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchKeyAndDescOnly;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.from;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.orderBy;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;
import static ua.com.fielden.platform.utils.EntityUtils.hasDescProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.com.fielden.platform.basic.IValueMatcherWithContext;
import ua.com.fielden.platform.basic.IValueMatcherWithFetch;
import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.dao.QueryExecutionModel.Builder;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.fetch;
import ua.com.fielden.platform.entity.query.model.ConditionModel;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.entity.query.model.OrderingModel;

/**
 * Key based value matcher, which supports context assignment.
 *
 * @author TG Team
 */
public abstract class AbstractSearchEntityByKeyWithContext<CONTEXT extends AbstractEntity<?>, T extends AbstractEntity<?>> implements IValueMatcherWithContext<CONTEXT, T>, IValueMatcherWithFetch<T> {

    protected final IEntityDao<T> companion;
    private final fetch<T> defaultFetchModel;
    private fetch<T> fetchModel;
    private CONTEXT context;

    public AbstractSearchEntityByKeyWithContext(final IEntityDao<T> companion) {
        this.companion = companion;
        this.defaultFetchModel = companion == null ? null : fetchKeyAndDescOnly(companion.getEntityType());
    }

    /**
     * This method may be overridden to create a different EQL condition model for search criteria.
     *
     * @param context
     * @param searchString
     * @return
     */
    protected ConditionModel makeSearchCriteriaModel(final CONTEXT context, final String searchString) {
        if ("%".equals(searchString)) {
            return cond().val(1).eq().val(1).model();
        }

        final ConditionModel keyCriteria = createRelaxedSearchByKeyCriteriaModel(searchString);

        return hasDescProperty(companion.getEntityType()) ? cond().condition(keyCriteria).or().prop(DESC).iLike().val("%" + searchString).model() : keyCriteria;
    }

    /**
     * This method may be overridden to provide the param values for the resulting query based on the provided context.
     *
     * @param context
     * @param params
     *            - params to fill
     */
    protected void fillParamsBasedOnContext(final CONTEXT context, final Map<String, Object> params) {
        // Do nothing here
    }

    /**
     * This method may be overridden to provide an alternative ordering if the default ordering by the key is not suitable.
     *
     * @return alternative ordering model
     */
    protected OrderingModel makeOrderingModel(final String searchString) {
        return orderBy().prop(KEY).asc().model();
    }

    private Builder<T, EntityResultQueryModel<T>> createCommonQueryBuilderForFindMatches(final String searchString, final Class<T> entityType) {
        final ConditionModel searchCriteria = makeSearchCriteriaModel(getContext(), searchString);
        final EntityResultQueryModel<T> queryModel = (searchCriteria != null ? select(entityType).where().condition(searchCriteria).model() : select(entityType).model()).setFilterable(true);
        final OrderingModel ordering = composeOrderingModelForQuery(searchString);

        fillParamsBasedOnContext(getContext(), emptyMap());
        return from(queryModel).with(ordering).with(emptyMap()).lightweight();
    }

    private OrderingModel composeOrderingModelForQuery(final String searchString) {
        return "%".equals(searchString) ? makeOrderingModel(searchString)
                : orderBy().expr(makeSearchResultOrderingPriority(companion.getEntityType(), searchString)).asc().order(makeOrderingModel(searchString)).model();
    }

    @Override
    public List<T> findMatches(final String searchString) {
        return companion.getFirstEntities(createCommonQueryBuilderForFindMatches(searchString, companion.getEntityType()).with(defaultFetchModel).model(), getPageSize());
    }

    @Override
    public List<T> findMatchesWithModel(final String searchString) {
        return companion.getFirstEntities(createCommonQueryBuilderForFindMatches(searchString, companion.getEntityType()).with(getFetch()).model(), getPageSize());
    }

    @Override
    public fetch<T> getFetch() {
        return fetchModel;
    }

    @Override
    public void setFetch(final fetch<T> fetchModel) {
        this.fetchModel = fetchModel;
    }

    @Override
    public CONTEXT getContext() {
        return context;
    }

    @Override
    public void setContext(final CONTEXT context) {
        this.context = context;
    }

}