package ua.com.fielden.platform.web.centre.api.resultset;

import ua.com.fielden.platform.entity.AbstractEntity;

/**
 * This is just a gluing contract to add fluency to the result set property adding expressions.
 *
 * @author TG Team
 *
 * @param <T>
 */
public interface IAlsoProp<T extends AbstractEntity<?>> extends IResultSetBuilder1PrimaryAction<T> {
    IResultSetBuilder<T> also();
}