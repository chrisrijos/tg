package ua.com.fielden.platform.basic.autocompleter;

import static java.lang.String.format;
import static ua.com.fielden.platform.entity.AbstractEntity.DESC;
import static ua.com.fielden.platform.entity.AbstractEntity.KEY;
import static ua.com.fielden.platform.entity.ActivatableAbstractEntity.ACTIVE;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.orderBy;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;
import static ua.com.fielden.platform.utils.EntityUtils.hasDescProperty;

import ua.com.fielden.platform.basic.IValueMatcherWithContext;
import ua.com.fielden.platform.dao.IEntityDao;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.ActivatableAbstractEntity;
import ua.com.fielden.platform.entity.exceptions.EntityException;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.ICompoundCondition0;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.ICompoundCondition1;
import ua.com.fielden.platform.entity.query.model.OrderingModel;
import ua.com.fielden.platform.reflection.TitlesDescsGetter;
import ua.com.fielden.platform.utils.EntityUtils;

/**
 * This is a fall back implementation for {@link IValueMatcherWithContext}, which does not use a context. It simply performs the search by key and description, if applicable.
 * <p>
 * Also, in case of matching activatable entity values, only <code>active</code> ones are matched.
 *
 * @author TG Team
 *
 * @param <CONTEXT>
 * @param <T>
 */
public class FallbackValueMatcherWithContext<CONTEXT extends AbstractEntity<?>, T extends AbstractEntity<?>> extends AbstractSearchEntityByKeyWithContext<CONTEXT, T> {

    private final Class<T> entityType;
    private final boolean activeOnly;

    public FallbackValueMatcherWithContext(final IEntityDao<T> co, final boolean activeOnly) {
        super(co);

        entityType = co.getEntityType();
        this.activeOnly = activeOnly;
        if (activeOnly && !ActivatableAbstractEntity.class.isAssignableFrom(entityType)) {
            final String entityTitle = TitlesDescsGetter.getEntityTitleAndDesc(entityType).getKey();
            throw new EntityException(format("Activatable type is expected. Entity [%s] is not activatable.", entityTitle));
        }

    }

    @Override
    public Integer getPageSize() {
        return IEntityDao.DEFAULT_PAGE_CAPACITY;
    }

    @Override
    protected ICompoundCondition0<T> startEqlBasedOnContext(
            final CONTEXT context,
            final String searchString) {

        final ICompoundCondition1<T> incompleteEql1 = select(companion.getEntityType()).where()
                .begin()
                    .prop(KEY).iLike().val(searchString);

        final ICompoundCondition0<T> condition;
        if (hasDescProperty(entityType)) {
            condition = incompleteEql1.or().upperCase().prop(DESC).iLike().val("%" + searchString).end();
        } else {
            condition = incompleteEql1.end();
        }

        if (activeOnly) {
            return condition.and().prop(ACTIVE).eq().val(true);
        } else {
            return condition;
        }

    }
    
    @Override
    protected OrderingModel makeOrderingModel(final String searchString) {
    	if (hasDescProperty(entityType)) {
    		return orderBy().order(createKeyBeforeDescOrderingModel(searchString)).order(super.makeOrderingModel(searchString)).model();
    	} 
        return super.makeOrderingModel(searchString);
    }
}