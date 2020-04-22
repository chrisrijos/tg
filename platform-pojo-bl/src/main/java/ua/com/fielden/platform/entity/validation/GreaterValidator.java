package ua.com.fielden.platform.entity.validation;

import static java.lang.String.format;
import static ua.com.fielden.platform.error.Result.failure;
import static ua.com.fielden.platform.error.Result.successful;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.Set;

import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.error.Result;
import ua.com.fielden.platform.types.Money;

/**
 * This validator implements a check for limit to be greater to the specified limit.
 *
 * @author TG Team
 *
 */
public class GreaterValidator implements IBeforeChangeEventHandler<Object> {
    public static final String ERR_VALUE_SHOULD_BE_GREATER_THAN = "Value should be greater than %s.";

    protected String limit;

    @Override
    public Result handle(final MetaProperty<Object> property, final Object newValue, final Set<Annotation> mutatorAnnotations) {
        if (newValue == null) {
            return successful("Value is null and thus not applicable for validation.");
        }
        // Money new value should be correctly converted.
        final String strValue = (newValue instanceof Money) ? ((Money) newValue).getAmount().toString() : newValue.toString();
        final BigDecimal numValue = new BigDecimal(strValue);

        return numValue.compareTo(new BigDecimal(limit)) > 0 
                ? successful(property.getEntity())
                : failure(property.getEntity(), format(ERR_VALUE_SHOULD_BE_GREATER_THAN, limit));
    }

}
