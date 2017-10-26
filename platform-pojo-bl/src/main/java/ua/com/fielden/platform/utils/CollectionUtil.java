package ua.com.fielden.platform.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import ua.com.fielden.platform.entity.AbstractEntity;

/**
 * A convenience class to provide common collection related routines and algorithms.
 * 
 * @author TG Team
 * 
 */
public final class CollectionUtil {
    private CollectionUtil() {
    }

    @SafeVarargs
    public static <T> HashSet<T> setOf(final T ... elements) {
        return new HashSet<T>(Arrays.asList(elements));
    }
    
    @SafeVarargs
    public static <T> LinkedHashSet<T> linkedSetOf(final T ... elements) {
        return new LinkedHashSet<T>(Arrays.asList(elements));
    }
    
    @SafeVarargs
    public static <T> List<T> listOf(final T ... elements) {
        return new ArrayList<T>(Arrays.asList(elements));
    }

    
    /**
     * Converts collection to a string separating the elements with a provided separator.
     * <p>
     * No precaution is taken if toString representation of an element already contains a symbol equal to a separator.
     */
    public static <T> String toString(final Collection<T> collection, final String separator) {
        final StringBuffer buffer = new StringBuffer();
        for (final Iterator<T> iter = collection.iterator(); iter.hasNext();) {
            buffer.append(iter.next() + (iter.hasNext() ? separator : ""));
        }
        return buffer.toString();
    }

    /**
     * Converts collection of {@link AbstractEntity}s to a string by concatenating values of the specified property using a provided separator.
     * <p>
     * No precaution is taken if toString representation of property's value already contains a symbol equal to a separator.
     */
    public static String toString(final Collection<? extends AbstractEntity> collection, final String propertyName, final String separator) {
        final StringBuffer buffer = new StringBuffer();
        for (final Iterator<? extends AbstractEntity> iter = collection.iterator(); iter.hasNext();) {
            final AbstractEntity entity = iter.next();
            final Object value = entity != null ? entity.get(propertyName) : null;
            buffer.append(value + (iter.hasNext() ? separator : ""));
        }
        return buffer.toString();
    }

}
