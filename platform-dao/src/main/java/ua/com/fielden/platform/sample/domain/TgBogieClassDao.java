package ua.com.fielden.platform.sample.domain;

import ua.com.fielden.platform.dao.CommonEntityDao;
import ua.com.fielden.platform.entity.annotation.EntityType;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.sample.domain.ITgBogieClass;
import ua.com.fielden.platform.sample.domain.TgBogieClass;

import com.google.inject.Inject;

@EntityType(TgBogieClass.class)
public class TgBogieClassDao extends CommonEntityDao<TgBogieClass> implements ITgBogieClass {

    @Inject
    protected TgBogieClassDao(final IFilter filter) {
        super(filter);
    }
}