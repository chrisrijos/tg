package ua.com.fielden.platform.basic.autocompleter;

import static ua.com.fielden.platform.entity.AbstractEntity.DESC;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.cond;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.orderBy;
import static ua.com.fielden.platform.utils.EntityUtils.hasDescProperty;

import ua.com.fielden.platform.basic.IValueMatcherWithCentreContext;
import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.query.model.ConditionModel;
import ua.com.fielden.platform.entity.query.model.OrderingModel;
import ua.com.fielden.platform.web.centre.CentreContext;

/**
 * This is a fall back implementation for {@link IValueMatcherWithCentreContext}, which does not do anything with the provided context. It simply performs the search by key
 * operation.
 *
 * @author TG Team
 *
 * @param <T>
 */
public class FallbackValueMatcherWithCentreContext<T extends AbstractEntity<?>> extends AbstractSearchEntityByKeyWithCentreContext<T> {

    private final Class<T> entityType;
    private final boolean hasDescProp;

    public FallbackValueMatcherWithCentreContext(final IEntityDao<T> co) {
        super(co);
        entityType = co.getEntityType();
        this.hasDescProp = hasDescProperty(entityType);
    }

    @Override
    public Integer getPageSize() {
        return IEntityDao.DEFAULT_PAGE_CAPACITY;
    }

    @Override
    protected ConditionModel makeSearchCriteriaModel(final CentreContext<T, ?> context, final String searchString) {
        if ("%".equals(searchString)) {
            return cond().val(1).eq().val(1).model();
        }

        final ConditionModel originalcondition = super.makeSearchCriteriaModel(context, searchString);

        return hasDescProp ? cond().condition(originalcondition).or().prop(DESC).iLike().val("%" + searchString).model() : originalcondition;
    }

    @Override
    protected OrderingModel makeOrderingModel(final String searchString) {
        if ("%".equals(searchString)) {
            return super.makeOrderingModel(searchString);
        } else {
            return orderBy().order(createRelaxedKeyDescOrderingModel(searchString)).order(super.makeOrderingModel(searchString)).model();
        }
    }
}