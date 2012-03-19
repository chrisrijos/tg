package ua.com.fielden.platform.persistence.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.entity.meta.PropertyDescriptor;
import ua.com.fielden.platform.types.markers.IPropertyDescriptorType;

/**
 * Class that helps Hibernate to map {@link PropertyDescriptor} class into the database.
 *
 * @author TG Team
 */
public class PropertyDescriptorType implements UserType, IPropertyDescriptorType {

    private static final int[] SQL_TYPES = { Types.VARCHAR };

    public int[] sqlTypes() {
	return SQL_TYPES;
    }

    @SuppressWarnings("unchecked")
    public Class returnedClass() {
	return PropertyDescriptor.class;
    }

    public Object nullSafeGet(final ResultSet resultSet, final String[] names, final Object owner) throws HibernateException, SQLException {
	final String propertyDescriptor = resultSet.getString(names[0]);
	Object result = null;
	if (!resultSet.wasNull()) {
	    try {
		final EntityFactory factory = ((AbstractEntity) owner).getEntityFactory();
		result = PropertyDescriptor.fromString(propertyDescriptor, factory);
	    } catch (final Exception e) {
		e.printStackTrace();
		throw new HibernateException("Could not restore '" + propertyDescriptor + "' due to: " + e.getMessage());
	    }
	}
	return result;
    }

    @Override
    public Object instantiate(final Object argument, final EntityFactory factory) {
	if (argument == null) {
	    return null;
	}

	try {
	    return PropertyDescriptor.fromString((String) argument, factory);
	} catch (final Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException("Could not instantiate instance of '" + PropertyDescriptor.class.getName() + " with value [" + argument + "] due to: " + e.getMessage());
	}
    }

    public void nullSafeSet(final PreparedStatement preparedStatement, final Object value, final int index) throws HibernateException, SQLException {
	if (null == value) {
	    preparedStatement.setNull(index, Types.VARCHAR);
	} else {
	    preparedStatement.setString(index, value.toString());
	}
    }

    public Object deepCopy(final Object value) throws HibernateException {
	return value;
    }

    public boolean isMutable() {
	return false;
    }

    public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
	return cached;
    }

    public Serializable disassemble(final Object value) throws HibernateException {
	return (Serializable) value;
    }

    public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
	return original;
    }

    public int hashCode(final Object x) throws HibernateException {
	return x.hashCode();
    }

    public boolean equals(final Object x, final Object y) throws HibernateException {
	if (x == y) {
	    return true;
	}
	if (null == x || null == y) {
	    return false;
	}
	return x.equals(y);
    }
}