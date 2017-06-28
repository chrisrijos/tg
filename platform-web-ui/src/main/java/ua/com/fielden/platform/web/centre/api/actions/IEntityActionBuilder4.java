package ua.com.fielden.platform.web.centre.api.actions;

import ua.com.fielden.platform.entity.AbstractEntity;

public interface IEntityActionBuilder4<T extends AbstractEntity<?>> extends IEntityActionBuilder5<T> {
    IEntityActionBuilder4IconStyle<T> icon(final String iconName);
}