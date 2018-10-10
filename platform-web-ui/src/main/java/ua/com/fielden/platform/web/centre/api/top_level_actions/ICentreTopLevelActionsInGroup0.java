package ua.com.fielden.platform.web.centre.api.top_level_actions;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.web.centre.api.actions.EntityActionConfig;

/**
 * A contract for adding top action to a group.
 *
 * @author TG Team
 *
 * @param <T>
 */
public interface ICentreTopLevelActionsInGroup0<T extends AbstractEntity<?>> {
    ICentreTopLevelActionsInGroup0<T> addGroupAction(final EntityActionConfig actionConfig);
    IAlsoCentreTopLevelActions<T> endTopActionsGroup();
}
