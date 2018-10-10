package ua.com.fielden.platform.sample.domain;

import ua.com.fielden.platform.dao.CommonEntityDao;
import ua.com.fielden.platform.dao.annotations.SessionRequired;
import ua.com.fielden.platform.entity.annotation.EntityType;
import ua.com.fielden.platform.entity.query.IFilter;
import ua.com.fielden.platform.sample.domain.security_tokens.DeleteFuelTypeToken;
import ua.com.fielden.platform.security.Authorise;

import com.google.inject.Inject;

@EntityType(TgFuelType.class)
public class TgFuelTypeDao extends CommonEntityDao<TgFuelType> implements ITgFuelType {

    @Inject
    protected TgFuelTypeDao(final IFilter filter) {
        super(filter);
    }

    @Override
    @SessionRequired
    @Authorise(DeleteFuelTypeToken.class)
    public void delete(final TgFuelType entity) {
        defaultDelete(entity);
    }

}
