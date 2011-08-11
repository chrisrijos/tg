package ua.com.fielden.platform.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.AbstractUnionEntity;
import ua.com.fielden.platform.entity.DynamicEntityKey;
import ua.com.fielden.platform.entity.annotation.KeyType;
import ua.com.fielden.platform.entity.validation.annotation.GreaterOrEqual;
import ua.com.fielden.platform.entity.validation.annotation.Max;
import ua.com.fielden.platform.equery.IQueryModelProvider;
import ua.com.fielden.platform.utils.Pair;

/**
 * This is a helper class to provide some commonly used method for retrieval of RTTI not provided directly by the Java reflection package.
 *
 * @author TG Team
 *
 */
public final class Reflector {
    public static final String DOT_SPLITTER = "\\.";

    /**
     * Let's hide default constructor, which is not needed for a static class.
     */
    private Reflector() {
    }

    // ========================================================================================================
    /////////////////////////////// Getting methods ///////////////////////////////////////////////////////////


    /**
     * This is a helper method used to walk along class hierarchy in search of the specified method.
     *
     * @param startWithClass
     * @param method
     * @param arguments
     * @return
     * @throws NoSuchMethodException
     * @throws Exception
     */
    public static Method getMethod(final Class<?> startWithClass, final String methodName, final Class<?>... arguments) throws NoSuchMethodException {
	try {
	    // setKey is a special case, because property "key" has a parametrised type that extends comparable.
	    // this means that there are two cases:
	    // 1. setter is overridden and a final key type is specified there -- need to use the assigned type as input parameter to setter in order to find it;
	    // 2. setter is not overridden -- need to use the lowest common denominator (Comparable) as input parameter to setter in order to find it.
	    if ("setKey".equals(methodName)) {
		try {
		    return getMethodForClass(startWithClass, methodName, arguments);
		} catch (final NoSuchMethodException e) {
		    return getMethodForClass(startWithClass, methodName, Comparable.class);
		}
	    }
	    return getMethodForClass(startWithClass, methodName, arguments);
	} catch (final NoSuchMethodException e) {
	    if (AbstractUnionEntity.class.isAssignableFrom(startWithClass)
		    && AbstractUnionEntity.commonMethodNames((Class<AbstractUnionEntity>) startWithClass).contains(methodName)) {
		return getMethodForClass(AbstractUnionEntity.unionProperties((Class<AbstractUnionEntity>) startWithClass).get(0).getType(), methodName, arguments);
	    }
	}
	throw new NoSuchMethodException("There is no method [" + methodName + "] in class [" + startWithClass.getSimpleName() + "] with arguments [" + Arrays.asList(arguments) + "].");
    }

    /**
     * Returns method specified with methodName from {@code startWithClass} class.
     *
     * @param startWithClass
     * @param methodName
     * @param arguments
     * @return
     * @throws NoSuchMethodException
     */
    protected static Method getMethodForClass(final Class<?> startWithClass, final String methodName, final Class<?>... arguments) throws NoSuchMethodException {
	Class<?> klass = startWithClass;
	while (klass != Object.class) { // need to iterated thought hierarchy in
	    // order to retrieve fields from above
	    // the current instance
	    try {
		return klass.getDeclaredMethod(methodName, arguments);
	    } catch (final NoSuchMethodException e) {
		klass = klass.getSuperclass();
	    }
	}
	throw new NoSuchMethodException(methodName);
    }

    /**
     * Returns constructor specified from {@code startWithClass} class.
     *
     * @param startWithClass
     * @param methodName
     * @param arguments
     * @return
     * @throws NoSuchMethodException
     */
    public static <L> Constructor<? super L> getConstructorForClass(final Class<L> startWithClass, final Class<?>... arguments) throws NoSuchMethodException {
	Class<? super L> klass = startWithClass;
	while (klass != Object.class) { // need to iterated thought hierarchy in
	    // order to retrieve fields from above
	    // the current instance
	    try {
		return klass.getConstructor(arguments);
	    } catch (final NoSuchMethodException e) {
		klass = klass.getSuperclass();
	    }
	}
	throw new NoSuchMethodException("constructor with " + Arrays.asList(arguments));
    }

    /**
     * Returns the method specified with methodName and the array of it's arguments. In order to determine correct method it uses instances instead of classes.
     *
     * @param instance
     * @param methodName
     * @param arguments
     * @return
     * @throws NoSuchMethodException
     */
    public static Method getMethod(final Object instance, final String methodName, final Class<?>... arguments) throws NoSuchMethodException {
	try {
	    return getMethodForClass(instance instanceof AbstractEntity ? ((AbstractEntity) instance).getType() : instance.getClass(), methodName, arguments);
	} catch (final NoSuchMethodException e) {
	    if (instance instanceof AbstractUnionEntity
		    && AbstractUnionEntity.commonMethodNames((Class<AbstractUnionEntity>) ((AbstractUnionEntity) instance).getType()).contains(methodName)) {
		final AbstractEntity activeEntity = ((AbstractUnionEntity) instance).activeEntity();
		if (activeEntity != null) {
		    return getMethodForClass(activeEntity.getType(), methodName, arguments);
		}
	    }
	}
	throw new NoSuchMethodException(methodName);
    }

    /**
     * Depending on the type of the field, the getter may start not with ''get'' but with ''is''. This method tries to determine a correct getter.
     *
     * @param propertyName
     * @param entity
     * @return
     * @throws Exception
     */
    public static Method obtainPropertyAccessor(final Class<?> entityClass, final String propertyName) throws NoSuchMethodException {
        final String propertyNameInGetter = propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
        try {
            return Reflector.getMethod(entityClass, "get" + propertyNameInGetter);
        } catch (final Exception e) {
            return Reflector.getMethod(entityClass, "is" + propertyNameInGetter);
        }
    }

    /**
     * Tries to obtain property setter for property, specified using dot-notation. Heavily uses
     * {@link PropertyTypeDeterminator#determinePropertyTypeWithoutKeyTypeDetermination(Class, String)} to obtain penult property in dot-notation
     *
     * @param entityClass
     * @param dotNotationExp
     * @return
     * @throws NoSuchMethodException
     * @throws Exception
     */
    public static Method obtainPropertySetter(final Class<?> entityClass, final String dotNotationExp) throws NoSuchMethodException {
        if (StringUtils.isEmpty(dotNotationExp) || dotNotationExp.contains("()")) {
            throw new IllegalArgumentException("DotNotationExp could not be empty or could not define construction with methods.");
        }
        final Pair<Class<?>, String> transformed = PropertyTypeDeterminator.transform(entityClass, dotNotationExp);
        return Reflector.getMethod(transformed.getKey(), "set" + transformed.getValue().substring(0, 1).toUpperCase() + transformed.getValue().substring(1), PropertyTypeDeterminator.determineClass(transformed.getKey(), transformed.getValue(), AbstractEntity.KEY.equalsIgnoreCase(transformed.getValue()), false));
    }

    // ========================================================================================================
    /////////////////////////////// Miscellaneous utilities ///////////////////////////////////////////////////

    /**
     * A contract for determining if the property of specified type <code>propertyType</code> could be sortable or not. For now - only property of AE type with composite key could
     * not be sortable.
     *
     * @param propertyType
     * @return
     */
    public static boolean notSortable(final Class<?> propertyType) {
        final KeyType keyType = propertyType.getAnnotation(KeyType.class);
        return AbstractEntity.class.isAssignableFrom(propertyType) && keyType != null && DynamicEntityKey.class.isAssignableFrom(keyType.value());
    }

    /**
     * Returns min and max possible values for property.
     *
     * @param entity
     * @param propertyName
     * @return
     */
    public static Pair<Comparable, Comparable> extractValidationLimits(final AbstractEntity<?> entity, final String propertyName) {
        final List<Field> fields = Finder.findProperties(entity.getType());
        Comparable min = null, max = null;
        for (final Field field : fields) { // for each property field
            if (field.getName().equals(propertyName)) { //
        	final Set<Annotation> propertyValidationAnotations = entity.extractValidationAnnotationForProperty(field, PropertyTypeDeterminator.determinePropertyType(entity.getType(), propertyName), false);
        	for (final Annotation annotation : propertyValidationAnotations) {
        	    if (annotation instanceof GreaterOrEqual) {
        		min = ((GreaterOrEqual) annotation).value();
        	    } else if (annotation instanceof Max) {
        		max = ((Max) annotation).value();
        	    }
        	}
            }
        }
        if (min == null) {
            min = Integer.MIN_VALUE;
        }
        if (max == null) {
            max = Integer.MAX_VALUE;
        }
        return new Pair<Comparable, Comparable>(min, max);
    }

    /**
     * Indicates whether specified class is synthetic entity or not.
     *
     * @param clazz
     * @return
     */
    public static boolean isSynthetic(final Class<?> clazz) {
	return IQueryModelProvider.class.isAssignableFrom(clazz);
    }

}
