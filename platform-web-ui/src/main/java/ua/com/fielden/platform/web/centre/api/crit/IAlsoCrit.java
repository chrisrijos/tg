package ua.com.fielden.platform.web.centre.api.crit;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.web.centre.api.crit.layout.ILayoutConfig;

/**
 * A contract to provide fluent joining of selection criteria definitions.
 *
 * @author TG Team
 *
 * @param <T>
 */
public interface IAlsoCrit<T extends AbstractEntity<?>> extends ILayoutConfig<T> {
    ISelectionCriteriaBuilder<T> also();
}