package ua.com.fielden.platform.web.centre.api.actions;

import ua.com.fielden.platform.entity.AbstractEntity;

public interface IEntityActionBuilder5<T extends AbstractEntity<?>> extends IEntityActionBuilder6<T> {
    IEntityActionBuilder6<T> shortDesc(final String shortDesc);
}
