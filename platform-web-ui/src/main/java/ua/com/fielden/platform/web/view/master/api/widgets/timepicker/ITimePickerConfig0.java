package ua.com.fielden.platform.web.view.master.api.widgets.timepicker;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.web.view.master.api.actions.IPropertyActionConfig;
import ua.com.fielden.platform.web.view.master.api.helpers.IAlso;

/**
* A configuration for an time picker component that gets associated with a property of type <code>Date</code> that resembles time only.
*
* As a note, the actual <code>input</code> field could have date and step attributes assigned.
* Such as <code>type="datetime-local"</code> and <code>step=60</code> for a one minute increment on the associated spinner (tested in Chrome).
*
* @author TG Team
*
* @param <T>
*/
public interface ITimePickerConfig0<T extends AbstractEntity<?>> extends IAlso<T>, IPropertyActionConfig<T> {

}
