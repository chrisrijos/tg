package ua.com.fielden.platform.web.master.api.widgets;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.web.master.api.actions.IPropertyActionConfig;
import ua.com.fielden.platform.web.master.api.helpers.IAlso;

/**
 *
 * A configuration for a widget to edit string properties with a meaning of a colour.
 * At some stage there should be a separate type <code>Colour</code> that would be used in place of <code>String</code> for property types that should hold a colour.
 * This configuration should support both these property types.
 * <p>
 * In case of HTML a corresponding widget should either be an <code>input</text> with <code>type="color"</code> or a custom component.
 *
 * @author TG Team
 *
 * @param <T>
 */
public interface IColourConfig<T extends AbstractEntity<?>> extends IAlso<T>, IPropertyActionConfig<T> {

}
