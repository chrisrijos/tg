package ua.com.fielden.platform.sample.domain;

import ua.com.fielden.platform.dao.CommonEntityDao;
import ua.com.fielden.platform.entity.annotation.EntityType;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.sample.domain.ITgWorkshop;
import ua.com.fielden.platform.sample.domain.TgWorkshop;

import com.google.inject.Inject;

@EntityType(TgWorkshop.class)
public class TgWorkshopDao extends CommonEntityDao<TgWorkshop> implements ITgWorkshop {

    @Inject
    protected TgWorkshopDao(final IFilter filter) {
        super(filter);
    }
}
