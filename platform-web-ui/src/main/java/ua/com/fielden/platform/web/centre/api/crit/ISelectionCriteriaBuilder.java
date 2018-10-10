package ua.com.fielden.platform.web.centre.api.crit;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.web.centre.api.resultset.IResultSetBuilder2Properties;

/**
 *
 * Provides a convenient abstraction for specifying selection criteria for an entity centre.
 * <p>
 * It extends {@link IResultSetBuilder2Properties} to cater for skipping addition of selection criteria and continuing directly with a set up of the result set.
 *
 * @author TG Team
 *
 * @param <T>
 */
public interface ISelectionCriteriaBuilder<T extends AbstractEntity<?>> {
    ISelectionCritKindSelector<T> addCrit(final String propName);
}
