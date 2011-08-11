package ua.com.fielden.platform.dao;

import java.io.IOException;
import java.util.List;

import ua.com.fielden.platform.equery.EntityAggregates;
import ua.com.fielden.platform.equery.fetch;
import ua.com.fielden.platform.equery.interfaces.IQueryOrderedModel;
import ua.com.fielden.platform.pagination.IPage;

public interface IEntityAggregatesDao {

    /**
     * Username should be provided for every DAO instance in order to support data filtering and auditing.
     */
    void setUsername(final String username);
    String getUsername();


    /**
     * Returns results from running given aggregation query.
     *
     * @param aggregatesQueryModel
     * @return
     */
    //List<EntityAggregates> listAggregates(final IQueryOrderedModel<EntityAggregates> aggregatesQueryModel);

    List<EntityAggregates> listAggregates(final IQueryOrderedModel<EntityAggregates> aggregatesQueryModel, fetch<EntityAggregates> fetchModel);

    /**
     * Should return a reference to the first page of the specified size containing entity instances.
     *
     * @param pageCapacity
     * @return
     */
    IPage<EntityAggregates> firstPage(final int pageCapacity);

    /**
     * Should return a reference to the first page of the specified size containing entity instances retrieved using the provided query model (new EntityQuery).
     *
     * @param pageCapacity
     * @param query
     * @return
     */
    //IPage<EntityAggregates> firstPage(final IQueryOrderedModel<EntityAggregates> query, final int pageCapacity);

    IPage<EntityAggregates> firstPage(final IQueryOrderedModel<EntityAggregates> query, fetch<EntityAggregates> fetchModel, final int pageCapacity);

    /**
     * Returns a reference to a page with requested number and capacity holding entity instances retrieved sequentially ordered by ID.
     *
     * @param query
     *            *
     * @param pageNo
     * @param pageCapacity
     * @return
     */
    IPage<EntityAggregates> getPage(final int pageNo, final int pageCapacity);

    /**
     * Returns a reference to a page with requested number and capacity holding entity instances matching the provided query model (new EntityQuery).
     *
     * @param model
     * @param pageNo
     * @param pageCapacity
     * @return
     */
    //    IPage<EntityAggregates> getPage(final IQueryOrderedModel<EntityAggregates> model, final int pageNo, final int pageCapacity);

    IPage<EntityAggregates> getPage(final IQueryOrderedModel<EntityAggregates> model, fetch<EntityAggregates> fetchModel, final int pageNo, final int pageCapacity);

    /**
     * The same as {@link #getPage(IQueryOrderedModel, int, int)}, but with page count information, which could be taken into account during implementation.
     *
     * @param model
     * @param pageNo
     * @param pageCount
     * @param pageCapacity
     * @return
     */
    //    IPage<EntityAggregates> getPage(final IQueryOrderedModel<EntityAggregates> model, final int pageNo, final int pageCount, final int pageCapacity);

    IPage<EntityAggregates> getPage(final IQueryOrderedModel<EntityAggregates> model, fetch<EntityAggregates> fetchModel, final int pageNo, final int pageCount, final int pageCapacity);

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
    //    byte[] export(final IQueryOrderedModel<EntityAggregates> query, final String[] propertyNames, final String[] propertyTitles) throws IOException;

    byte[] export(final IQueryOrderedModel<EntityAggregates> query, fetch<EntityAggregates> fetchModel, final String[] propertyNames, final String[] propertyTitles)
	    throws IOException;
}
