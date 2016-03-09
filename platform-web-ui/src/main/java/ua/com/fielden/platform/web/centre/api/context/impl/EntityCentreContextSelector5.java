package ua.com.fielden.platform.web.centre.api.context.impl;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.web.centre.api.context.CentreContextConfig;
import ua.com.fielden.platform.web.centre.api.context.IEntityCentreContextSelector1;
import ua.com.fielden.platform.web.centre.api.context.IEntityCentreContextSelector4;
import ua.com.fielden.platform.web.centre.api.context.IEntityCentreContextSelector5;

/**
 * Default implementation for the entity centre context selector API.
 *
 * @author TG Team
 *
 * @param <T>
 */
class EntityCentreContextSelector5<T extends AbstractEntity<?>> implements IEntityCentreContextSelector5<T> {

    private final boolean withSelectionCrit;
    private final boolean withMasterEntity;


    public EntityCentreContextSelector5(final boolean withSelectionCrit, final boolean withMasterEntity) {
        this.withSelectionCrit = withSelectionCrit;
        this.withMasterEntity = withMasterEntity;
    }

    @Override
    public CentreContextConfig build() {
        return new CentreContextConfig(
                false,
                false,
                withSelectionCrit,
                withMasterEntity
               );
    }

    @Override
    public IEntityCentreContextSelector1<T> withCurrentEntity() {
        return new EntityCentreContextSelector1_2_4_done<T>(true, false, withSelectionCrit, withMasterEntity);
    }

    @Override
    public IEntityCentreContextSelector1<T> withSelectedEntities() {
        return new EntityCentreContextSelector1_2_4_done<T>(false, true, withSelectionCrit, withMasterEntity);
    }

    @Override
    public IEntityCentreContextSelector4<T> withSelectionCrit() {
        return new EntityCentreContextSelector1_2_4_done<T>(false, false, true, withMasterEntity);
    }

}