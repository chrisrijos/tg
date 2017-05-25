package ua.com.fielden.platform.entity.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation used for specifying mapping of properties to corresponding table columns.
 * 
 * @author TG Team
 */

@Retention(RUNTIME)
@Target({ FIELD })
public @interface PersistentType {

    /**
     * Represents result of getName() method on instances of hibernate Type interface implementing classes (e.g. LongType - "long", YesNoType - "yes_no", BigDecimalType -
     * "big_decimal"
     * 
     * @return
     */
    String value() default ""; //

    /**
     * Returns implementers of IUserTypeInstantiate or ICompositeUserTypeInstantiate (e.g. ISimpleMoneyType.class, ISimplyMoneyWithTaxAmountType.class). Void.class means that
     * nothing has been specified (i.e. Null).
     * 
     * @return
     */
    Class userType() default Void.class; // represents hibernate type class
}
