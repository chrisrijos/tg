package ua.com.fielden.platform.web.centre.api.crit.impl;

import ua.com.fielden.platform.serialisation.jackson.DefaultValueContract;
import ua.com.fielden.platform.web.view.master.api.widgets.datetimepicker.impl.DateTimePickerWidget;

/**
 * An implementation for date single-editor criterion.
 *
 * @author TG Team
 *
 */
public class DateSingleCriterionWidget extends AbstractSingleCriterionWidget {

    /**
     * Creates an instance of {@link DateSingleCriterionWidget} for specified entity type and property name.
     *
     * @param criteriaType
     * @param propertyName
     */
    public DateSingleCriterionWidget(final Class<?> root, final Class<?> managedType, final String propertyName) {
        super(root, propertyName,
                new DateTimePickerWidget(
                        AbstractCriterionWidget.generateSingleTitleDesc(root, managedType, propertyName),
                        AbstractCriterionWidget.generateSingleName(root, managedType, propertyName),
                        false,
                        DefaultValueContract.getTimeZone(managedType, propertyName),
                        DefaultValueContract.getTimePortionToDisplay(managedType, propertyName)
                ));
    }
}
