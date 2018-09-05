package ua.com.fielden.platform.ui.menu;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation on generated entity type (and on criteria entity type) contains information about the entity centre on which the entity 
 * type appears: 'saveAs' centre configuration name in case of 'named' configurations. This annotation should not exist on types for 'unnamed' centres.
 * 
 * @author TG Team
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface SaveAsName {
    
    /** Contains non-empty 'saveAs' name for 'named' centre configuration. */
    String value();
    
}
