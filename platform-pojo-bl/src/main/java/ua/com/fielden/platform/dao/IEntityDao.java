package ua.com.fielden.platform.dao;

import static ua.com.fielden.platform.annotations.companion.Category.Operation.OTHER;
import static ua.com.fielden.platform.annotations.companion.Category.Operation.READ;

import java.io.IOException;

import ua.com.fielden.platform.annotations.companion.Category;
import ua.com.fielden.platform.companion.IEntityInstantiator;
import ua.com.fielden.platform.companion.IEntityReader;
import ua.com.fielden.platform.companion.IPersistentEntityMutator;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.security.user.User;

/**
 * The contract for any Entity Companion object to implement. It extends both {@link IEntityReader} and {@link IPersistentEntityMutator} contracts.
 *
 * @author TG Team
 *
 */
public interface IEntityDao<T extends AbstractEntity<?>> extends IEntityReader<T>, IPersistentEntityMutator<T>, IEntityInstantiator<T>, IComputationMonitor {
    static final int DEFAULT_PAGE_CAPACITY = 25;

    /**
     * A factory method that creates an instance of a companion object for the specified entity type.
     * The reader methods of such companion return <code>uninstrumented</code> entities.
     *
     * @return
     */
    @Category(OTHER)
    default <C extends IEntityReader<E>, E extends AbstractEntity<?>> C co(final Class<E> type) {
        throw new UnsupportedOperationException("This method should be overriden by descendants.");
    }

    /**
     * A factory method that creates an instance of a companion object for the specified entity type.
     * The reader methods of such companion return <code>instrumented</code> entities, which are suitable for mutation and saving.
     *
     * @return
     */
    @Category(OTHER)
    default <C extends IEntityDao<E>, E extends AbstractEntity<?>> C co$(final Class<E> type) {
        throw new UnsupportedOperationException("This method should be overriden by descendants.");
    }

    /**
     * Returns provided name.
     *
     * @return
     */
    @Category(READ)
    String getUsername();

    /**
     * Should return the current application user.
     *
     * @return
     */
    @Category(READ)
    abstract User getUser();

    /**
     * Should return a byte array representation the exported data in a format envisaged by the specific implementation.
     * <p>
     * For example it could be a byte array of GZipped Excel data.
     *
     * @param query
     *            -- query result of which should be exported.
     * @param propertyNames
     *            -- names of properties, including dot notated properties, which should be used in the export.
     * @param propertyTitles
     *            -- titles corresponding to the properties being exported, which are used as headers of columns.
     * @return
     */
    @Category(READ)
    byte[] export(final QueryExecutionModel<T, ?> query, final String[] propertyNames, final String[] propertyTitles) throws IOException;

}