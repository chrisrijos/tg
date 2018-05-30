/**
 *
 */
package ua.com.fielden.platform.basic.autocompleter;

import static ua.com.fielden.platform.entity.AbstractEntity.KEY;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchKeyAndDescOnly;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.from;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.orderBy;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ua.com.fielden.platform.basic.IValueMatcherWithCentreContext;
import ua.com.fielden.platform.basic.IValueMatcherWithFetch;
import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.dao.QueryExecutionModel.Builder;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.fetch;
import ua.com.fielden.platform.entity.query.model.ConditionModel;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.entity.query.model.OrderingModel;
import ua.com.fielden.platform.web.centre.CentreContext;

/**
 * Key based value matcher, which supports entity centre context assignment.
 *
 * @author TG Team
 */
public abstract class AbstractSearchEntityByKeyWithCentreContext<T extends AbstractEntity<?>>
                      implements IValueMatcherWithCentreContext<T>, IValueMatcherWithFetch<T> {

    private final IEntityDao<T> companion;
    private final fetch<T> defaultFetchModel;
    private fetch<T> fetchModel;
    private CentreContext<T, ?> context;

    private final int pageSize = 10;
    

    public AbstractSearchEntityByKeyWithCentreContext(final IEntityDao<T> companion) {
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
    protected ConditionModel makeSearchCriteriaModel(final CentreContext<T, ?> context, final String searchString) {
        return createSearchByKeyCriteriaModel(searchString);
    }

    /**
     * This method may be overridden to provide the param values for the
     * resulting query based on the provided context.
     *
     * @param context
     * @param params
     *            - params to fill
     */
    protected void fillParamsBasedOnContext(final CentreContext<T, ?> context, final Map<String, Object> params) {
        // Do nothing here
    }

    /**
     * This method may be overridden to provide an alternative ordering if the
     * default ordering by the key is not suitable.
     *
     * @return alternative ordering model
     */
    protected OrderingModel makeOrderingModel(final String searchString) {
        return orderBy().prop(KEY).asc().model();
    }

    private Builder<T, EntityResultQueryModel<T>> createCommonQueryBuilderForFindMatches(final String searchString) {
        final ConditionModel searchCriteria = makeSearchCriteriaModel(getContext(), searchString);
        final EntityResultQueryModel<T> queryModel = searchCriteria != null ? select(companion.getEntityType()).where().condition(searchCriteria).model() : select(companion.getEntityType()).model();
        queryModel.setFilterable(true);
        final OrderingModel ordering = makeOrderingModel(searchString);
        final Map<String, Object> params = new HashMap<>();
        fillParamsBasedOnContext(getContext(), params);
        return from(queryModel).with(ordering).with(params).lightweight();
    }
    
    @Override
    public List<T> findMatches(final String searchString) {
        return companion.firstPage(createCommonQueryBuilderForFindMatches(searchString).with(defaultFetchModel).model(), getPageSize()).data();
    }

    @Override
    public List<T> findMatchesWithModel(final String searchString) {
    	return companion.firstPage(createCommonQueryBuilderForFindMatches(searchString).with(getFetch()).model(), getPageSize()).data();
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
    public CentreContext<T, ?> getContext() {
        return context;
    }

    @Override
    public void setContext(final CentreContext<T, ?> context) {
        this.context = context;
    }

    @Override
    public Integer getPageSize() {
        return pageSize;
    }
}