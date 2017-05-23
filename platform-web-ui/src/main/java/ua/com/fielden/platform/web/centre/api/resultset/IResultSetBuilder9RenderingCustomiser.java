package ua.com.fielden.platform.web.centre.api.resultset;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.web.centre.api.query_enhancer.IQueryEnhancerSetter;

/**
 * This contract serves for specifying custom rendering logic. Custom rendering is optional.
 *
 * @author TG Team
 *
 * @param <T>
 */
public interface IResultSetBuilder9RenderingCustomiser<T extends AbstractEntity<?>> extends IQueryEnhancerSetter<T> {

    IQueryEnhancerSetter<T> setRenderingCustomiser(final Class<? extends IRenderingCustomiser<?>> type);
}
