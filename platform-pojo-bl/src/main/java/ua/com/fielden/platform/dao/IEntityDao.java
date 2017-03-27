package ua.com.fielden.platform.dao;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.annotation.CompositeKeyMember;
import ua.com.fielden.platform.entity.fetch.IFetchProvider;
import ua.com.fielden.platform.entity.query.fluent.fetch;
import ua.com.fielden.platform.entity.query.model.EntityResultQueryModel;
import ua.com.fielden.platform.pagination.IPage;
import ua.com.fielden.platform.security.user.User;

/**
 * Defines a contract that should be implemented by any data access object being that a Hibernate or REST driven implementation.
 *
 * Business logic and UI should strictly depend only on DAO interfaces -- not the concrete implementations. This will ensure implementation flexibility of the concrete way to
 * access data.
 *
 * @author TG Team
 *
 */
public interface IEntityDao<T extends AbstractEntity<?>> extends IComputationMonitor {
    static final int DEFAULT_PAGE_CAPACITY = 25;


    /**
     * This is a mixin method that should indicate whether data retrieval should follow the instrumented or uninstrumented strategy for entity instantiation during retrieval.
     * 
     * @return
     */
    default boolean instrumented() {
        return true;
    }
    
    /**
     * A factory method that creates an instance of the same companion object it is invoked on, but with method {@link #instrumented()} returning <code>false</code>.
     * 
     * @return
     */
    default <E extends IEntityDao<T>> E uninstrumented() {
        throw new UnsupportedOperationException("This method should be overriden by descendants.");
    }
    
    /**
     * Returns provided name.
     *
     * @return
     */
    String getUsername();

    /**
     * Should return the current application user.
     *
     * @return
     */
    abstract User getUser();

    /**
     * Should return an entity type the DAO is managing.
     *
     * @return
     */
    Class<T> getEntityType();

    /**
     * Should return entity's key type.
     *
     * @return
     */
    Class<? extends Comparable> getKeyType();

    /**
     * Should return true if entity with provided id and version value is stale, i.e. its version is older then the latest persisted entity with the same id.
     *
     * @param entityId
     * @param version
     * @return
     */
    boolean isStale(final Long entityId, final Long version);

    /**
     * Finds entity by its surrogate id.
     *
     * @param id
     *            -- ID of the entity to be loaded.
     * @param models
     *            -- one or more fetching models specifying the initialisation strategy (i.e. what properties should be retrieved).
     * @return
     */
    T findById(final Long id, final fetch<T> fetchModel);
    
    default Optional<T> findByIdOptional(final Long id, final fetch<T> fetchModel) {
        return Optional.ofNullable(findById(id, fetchModel)); 
    }

    /**
     * Finds entity by its surrogate id.
     *
     * @param id
     *            -- ID of the entity to be loaded.
     * @param models
     *            -- one or more fetching models specifying the initialisation strategy (i.e. what properties should be retrieved).
     * @return
     */
    T findById(final Long id);
    
    default Optional<T> findByIdOptional(final Long id) {
        return Optional.ofNullable(findById(id));
    }

    /**
     * Finds entity by its business key. If the key is composite then values of the key components should be passed in the same order as defined in the entity class using
     * annotation {@link CompositeKeyMember}.
     *
     * @param keyValues
     * @return
     */
    T findByKey(final Object... keyValues);
    
    default Optional<T> findByKeyOptional(final Object... keyValues) {
        return Optional.ofNullable(findByKey(keyValues));
    }

    /**
     * Finds entity by its business key and enhances it according to provided fetch model. If the key is composite then values of the key components should be passed in the same
     * order as defined in the entity class using annotation {@link CompositeKeyMember}.
     *
     * @param keyValues
     * @return
     */
    T findByKeyAndFetch(final fetch<T> fetchModel, final Object... keyValues);
    
    default Optional<T> findByKeyAndFetchOptional(final fetch<T> fetchModel, final Object... keyValues) {
        return Optional.ofNullable(findByKeyAndFetch(fetchModel, keyValues));
    }

    /**
     * Finds entity by its instance and enhances it according to provided fetch model.
     *
     * @param fetchModel
     * @param entity
     * @return
     */
    T findByEntityAndFetch(final fetch<T> fetchModel, final T entity);
    
    default Optional<T> findByEntityAndFetchOptional(final fetch<T> fetchModel, final T entity) {
        return Optional.ofNullable(findByEntityAndFetch(fetchModel, entity));
    }

    /**
     * Should return a reference to the first page of the specified size containing entity instances.
     *
     * @param pageCapacity
     * @return
     */
    IPage<T> firstPage(final int pageCapacity);

    /**
     * Should return a reference to the first page of the specified size containing entity instances retrieved using the provided query model (new EntityQuery).
     *
     * @param pageCapacity
     * @param query
     * @return
     */
    IPage<T> firstPage(final QueryExecutionModel<T, ?> query, final int pageCapacity);

    /**
     * Should return a reference to the first page of the specified size containing entity instances retrieved using the provided <code>summaryModel</code> and the summary
     * information based on <code>summaryModel</code>.
     *
     * @param model
     * @param summaryModel
     * @param pageCapacity
     * @return
     */
    default IPage<T> firstPage(final QueryExecutionModel<T, ?> model, final QueryExecutionModel<T, ?> summaryModel, final int pageCapacity) {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Returns a reference to a page with requested number and capacity holding entity instances retrieved sequentially ordered by ID.
     *
     * @param Equery
     * @param pageCapacity
     * @param pageNo
     * @return
     */
    IPage<T> getPage(final int pageNo, final int pageCapacity);

    /**
     * Returns a reference to a page with requested number and capacity holding entity instances matching the provided query model (new EntityQuery).
     *
     * @param query
     * @param pageCapacity
     * @param pageNo
     * @return
     */
    IPage<T> getPage(final QueryExecutionModel<T, ?> query, final int pageNo, final int pageCapacity);

    /**
     * Same as above, but the actual implementation could take into account the page count information.
     *
     * @param query
     * @param pageNo
     * @param pageCount
     * @param pageCapacity
     * @return
     */
    IPage<T> getPage(final QueryExecutionModel<T, ?> query, final int pageNo, final int pageCount, final int pageCapacity);

    /**
     * A convenient method for retrieving exactly one entity instance determined by the model. If more than one instance was found an exception is thrown. If there is no entity
     * found then a null value is returned.
     *
     * @param model
     * @return
     */
    T getEntity(final QueryExecutionModel<T, ?> model);

    default Optional<T> getEntityOptional(final QueryExecutionModel<T, ?> model) {
        return Optional.ofNullable(getEntity(model));
    }
    
    /**
     * Returns all entities produced by the provided query.
     *
     * @param quert
     * @return
     */
    List<T> getAllEntities(final QueryExecutionModel<T, ?> query);

    /**
     * Returns first entities produced by the provided query.
     *
     * @param quert
     * @return
     */
    List<T> getFirstEntities(final QueryExecutionModel<T, ?> query, final int numberOfEntities);

    /**
     * Returns a non-parallel stream with the data based on the provided query.
     * The returned stream must always be wrapped into <code>try with resources</code> clause to ensure that the underlying resultset is closed.
     * 
     * @param qem -- EQL model
     * @param fetchSize -- a batch size for retrieve the next lot of data to feed the stream
     * @return
     */
    Stream<T> stream(final QueryExecutionModel<T, ?> qem, final int fetchSize);
    
    /**
     * A convenience method based on {@link #stream(QueryExecutionModel, int), but with a default fetch size. 
     * The returned stream must always be wrapped into <code>try with resources</code> clause to ensure that the underlying resultset is closed.
     * 
     * @param qem
     * @return
     */
    Stream<T> stream(final QueryExecutionModel<T, ?> qem);
    
    /**
     * Persists (saves/updates) the entity and returns the updated entity back.
     * For safety consideration the passed in and the returned entity instances should NOT be considered reference equivalent.
     * The returned entity should be thought of as a newer equivalent of the passed in instance and used everywhere in the downstream logic of the callee.
     *
     * @param entity
     * @return
     */
    T save(final T entity);

    
    /**
     * Similar to method {@link #save(AbstractEntity)}, but returns an <code>id</code> of the saved entity.
     * The implication is that this method should execute faster by skipping the steps that are required to instantiate a resultant entity.
     * 
     * @param entity
     * @return
     */
    default long quickSave(final T entity) {
        throw new UnsupportedOperationException(); 
    }

    
    /**
     * Deletes entity instance by its id. Currently, in most cases it is not supported since deletion is not a trivial operation.
     *
     * @param entity
     */
    default void delete(final T entity) {
        throw new UnsupportedOperationException("By default deletion is not supported.");
    }

    /**
     * Deletes entities returned by provided query model with provided param values.
     *
     * @param model
     */
    default void delete(final EntityResultQueryModel<T> model, final Map<String, Object> paramValues) {
        throw new UnsupportedOperationException("By default deletion is not supported.");
    }

    /**
     * Deletes entities returned by provided query model
     *
     * @param model
     */
    default void delete(final EntityResultQueryModel<T> model) {
        delete(model, Collections.<String, Object> emptyMap());
    }

    /**
     * Performs batch deletion of entities returned by provided query model
     *
     * @param model
     */
    default int batchDelete(final EntityResultQueryModel<T> model) {
        throw new UnsupportedOperationException("Batch deletion should be implemented in descendants.");
    }

    /**
     * Performs batch deletion of entities returned by provided query model with provided param values.
     *
     * @param model
     */
    default int batchDelete(final EntityResultQueryModel<T> model, final Map<String, Object> paramValues) {
        throw new UnsupportedOperationException("Batch deletion should be implemented in descendants.");
    }

    /**
     * Performs batch deletion of entities, which ids are provided by collection.
     *
     * @param entitiesIds
     */
    default int batchDelete(final Collection<Long> entitiesIds) {
        throw new UnsupportedOperationException("By default batch deletion is not supported.");
    }

    /**
     * Performs batch deletion of entities from provided list.
     *
     * @param entitiesIds
     */
    default int batchDelete(final List<T> entities){
        throw new UnsupportedOperationException("By default batch deletion is not supported.");
    }
    
    /**
     * Performs batch deletion of entities, which entity property values are within provided collection.
     * 
     * @param propName
     * @param propEntitiesIds
     * @return
     */
    default int batchDeleteByPropertyValues(final String propName, final Collection<Long> propEntitiesIds) {
        throw new UnsupportedOperationException("By default batch deletion is not supported.");
    }

    /**
     * Performs batch deletion of entities, which entity property values are within provided list.
     * 
     * @param propName
     * @param propEntities
     * @return
     */
    default int batchDeleteByPropertyValues(final String propName, final List<T> propEntities){
        throw new UnsupportedOperationException("By default batch deletion is not supported.");
    }

    /**
     * Should return true if the passed entity exists in the persistent state.
     *
     * @param entity
     * @return
     */
    boolean entityExists(final T entity);

    /**
     * Check whether entity of the managed by this DAO type and the provided ID exists.
     *
     * @param id
     * @return
     */
    boolean entityExists(final Long id);

    /**
     * Should return true if an entity with the provided key exists in the persistent state.
     *
     * @param entity
     * @return
     */
    boolean entityWithKeyExists(final Object... keyValues);

    /**
     * Returns a number of entities retrieved using the provided model.
     *
     * @param model
     * @return
     */
    int count(final EntityResultQueryModel<T> model, final Map<String, Object> paramValues);

    int count(final EntityResultQueryModel<T> model);

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
    byte[] export(final QueryExecutionModel<T, ?> query, final String[] propertyNames, final String[] propertyTitles) throws IOException;

    /**
     * Returns default {@link FetchProvider} for the entity.
     * <p>
     * This fetch provider represents the 'aggregated' variant of all fetch providers needed mainly for entity master actions (and potentially others): <br>
     * <br>
     * 1. visual representation of entity properties in entity master UI <br>
     * 2. validation / modification processes with BCE / ACE / conversions handling <br>
     * 3. autocompletion of entity-typed properties
     *
     * @return
     */
    IFetchProvider<T> getFetchProvider();
 
    /**
     * Instantiates an new entity of the type for which this object is a companion.
     *
     * @return
     */
    T new_();

}