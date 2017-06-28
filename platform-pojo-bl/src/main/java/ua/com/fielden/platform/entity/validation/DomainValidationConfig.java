package ua.com.fielden.platform.entity.validation;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This is class representing domain validation configuration specifying the relationships between entity properties and the corresponding domain validators.
 * 
 * @author 01es
 * 
 */
@Deprecated
public class DomainValidationConfig {
    private final Logger logger = Logger.getLogger(this.getClass());
    /**
     * A storage for validators where the key represents entity class and the corresponding value is a map between property names and a corresponding validator.
     */
    private final Map<Class<?>, Map<String, IBeforeChangeEventHandler>> domainValidators = new HashMap<Class<?>, Map<String, IBeforeChangeEventHandler>>();

    /**
     * Return domain validator associated with property of the specified type (or one of its super types, if it is not registered with property of the specified one). The returned
     * value is null if no association was found.
     * 
     * @param entityType
     * @param propertyName
     * @return
     */
    public IBeforeChangeEventHandler getValidator(final Class<?> entityType, final String propertyName) {
        final Map<Class<?>, Map<String, IBeforeChangeEventHandler>> allValidators = getAllValidatorsFor(entityType);
        IBeforeChangeEventHandler validator = null;
        Class<?> runningEntityType = entityType;
        while (validator == null && runningEntityType != null) {
            final Map<String, IBeforeChangeEventHandler> validators = allValidators.get(runningEntityType);
            validator = validators != null ? validators.get(propertyName) : null;
            runningEntityType = runningEntityType.getSuperclass();
        }
        logger.debug("Request to get validator for " + propertyName + "@" + entityType.getName() + ": " + validator);
        return validator;
    }

    /**
     * Associates an instance of domain validator with property of certain entity type.
     * 
     * @param entityType
     * @param propertyName
     * @param domainValidator
     * @return
     */
    public DomainValidationConfig setValidator(final Class<?> entityType, final String propertyName, final IBeforeChangeEventHandler domainValidator) {
        logger.debug("Validator " + domainValidator + " is being set for " + propertyName + "@" + entityType.getName());
        final Map<String, IBeforeChangeEventHandler> map = domainValidators.get(entityType) == null ? new HashMap<String, IBeforeChangeEventHandler>()
                : domainValidators.get(entityType);
        map.put(propertyName, domainValidator); // this put replaces a validator if there was already one associated with the specified property
        domainValidators.put(entityType, map);
        return this;
    }

    /**
     * Returns all domain validators for specified class and all of its superclasses
     * 
     * @param entityType
     * @return
     */
    private Map<Class<?>, Map<String, IBeforeChangeEventHandler>> getAllValidatorsFor(Class<?> entityType) {
        final Map<Class<?>, Map<String, IBeforeChangeEventHandler>> allValidators = new HashMap<Class<?>, Map<String, IBeforeChangeEventHandler>>();
        while (entityType != null) {
            final Map<String, IBeforeChangeEventHandler> validators = domainValidators.get(entityType);
            if (validators != null) {
                allValidators.put(entityType, validators);
            }
            entityType = entityType.getSuperclass();
        }
        return allValidators;
    }

}
