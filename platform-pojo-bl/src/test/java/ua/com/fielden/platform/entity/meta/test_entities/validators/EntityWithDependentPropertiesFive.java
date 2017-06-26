package ua.com.fielden.platform.entity.meta.test_entities.validators;

import static ua.com.fielden.platform.error.Result.failure;
import static ua.com.fielden.platform.error.Result.successful;

import java.lang.annotation.Annotation;
import java.util.Set;

import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.entity.meta.test_entities.EntityWithDependentProperties;
import ua.com.fielden.platform.entity.validation.IBeforeChangeEventHandler;
import ua.com.fielden.platform.error.Result;

public class EntityWithDependentPropertiesFive implements IBeforeChangeEventHandler<String> {

    public static final String INVALID = "invalid";
    
    @Override
    public Result handle(final MetaProperty<String> property, final String newValue, Set<Annotation> mutatorAnnotations) {
        final EntityWithDependentProperties entity = (EntityWithDependentProperties) property.getEntity();
        entity.fiveCount++;
        if (entity.fiveCount == 1 && INVALID.equals(newValue)) {
            return failure("Invalida value at firts attempt.");
        }
        return successful(newValue);
    }

}
