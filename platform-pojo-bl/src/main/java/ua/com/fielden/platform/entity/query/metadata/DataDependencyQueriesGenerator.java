package ua.com.fielden.platform.entity.query.metadata;

import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.expr;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchAggregates;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchKeyAndDescOnly;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.from;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.orderBy;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;
import static ua.com.fielden.platform.utils.EntityUtils.getRealProperties;
import static ua.com.fielden.platform.utils.EntityUtils.isEntityType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ua.com.fielden.platform.dao.QueryExecutionModel;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.AbstractPersistentEntity;
import ua.com.fielden.platform.entity.annotation.MapEntityTo;
import ua.com.fielden.platform.entity.annotation.MapTo;
import ua.com.fielden.platform.entity.query.EntityAggregates;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils;
import ua.com.fielden.platform.entity.query.model.AggregatedResultQueryModel;
import ua.com.fielden.platform.entity.query.model.ExpressionModel;
import ua.com.fielden.platform.entity.query.model.PrimitiveResultQueryModel;

public class DataDependencyQueriesGenerator {

    public static QueryExecutionModel<EntityAggregates, AggregatedResultQueryModel> queryForDependentTypesSummary(final Map<Class<? extends AbstractEntity<?>>, Map<Class<? extends AbstractEntity<?>>, Set<String>>> dependenciesMetadata, final Long entityId, final Class<? extends AbstractEntity<?>> entityType) {
        final AggregatedResultQueryModel[] queries = produceQueries(dependenciesMetadata, entityType, entityId).toArray(new AggregatedResultQueryModel[] {});

        final AggregatedResultQueryModel qry = select(queries).groupBy().prop("type").yield().prop("type").as("type").yield().countAll().as("qty").modelAsAggregate();

        return from(qry).with(orderBy().yield("qty").desc().model()).model();
    }

    public static QueryExecutionModel<EntityAggregates, AggregatedResultQueryModel> queryForDependentTypeDetails(final Map<Class<? extends AbstractEntity<?>>, Map<Class<? extends AbstractEntity<?>>, Set<String>>> dependenciesMetadata, final Long entityId, final Class<? extends AbstractEntity<?>> entityType, final Class<? extends AbstractEntity<?>> detailsType) {
        final PrimitiveResultQueryModel[] detailsQueries = produceDetailsQueries(dependenciesMetadata, detailsType).toArray(new PrimitiveResultQueryModel[] {});
        final ExpressionModel hasDependencies = detailsQueries.length > 0 ? expr().caseWhen().existsAnyOf(detailsQueries).then().val("Y").otherwise().val("N").end().model()
                : expr().val("N").model();
        final AggregatedResultQueryModel qry = select(detailsType).
                where().
                anyOfProps(dependenciesMetadata.get(detailsType).get(entityType).toArray(new String[] {})).eq().val(entityId).
                yield().model(select(detailsType).where().prop("id").eq().extProp("id").model()).as("entity").
                yield().expr(hasDependencies).as("hasDependencies").
                modelAsAggregate();
        return from(qry).with(fetchAggregates().with("hasDependencies").with("entity", fetchKeyAndDescOnly(detailsType))).with(orderBy().prop("key").asc().model()).model();
    }

    @SuppressWarnings("unchecked")
    public static Map<Class<? extends AbstractEntity<?>>, Map<Class<? extends AbstractEntity<?>>, Set<String>>> produceDependenciesMetadata(final List<Class<? extends AbstractEntity<?>>> entityTypes) {
        final Map<Class<? extends AbstractEntity<?>>, Map<Class<? extends AbstractEntity<?>>, Set<String>>> result = new HashMap<>();
        for (final Class<? extends AbstractEntity<?>> entityType : entityTypes) {
            if (entityType.isAnnotationPresent(MapEntityTo.class) && AbstractPersistentEntity.class.isAssignableFrom(entityType)) {
                final Map<Class<? extends AbstractEntity<?>>, Set<String>> pmd = new HashMap<>();
                for (final Field ep : getRealProperties(entityType)) {
                    if (ep.isAnnotationPresent(MapTo.class) && isEntityType(ep.getType())) {
                        Set<String> existing = pmd.get(ep.getType());
                        if (existing == null) {
                            existing = new HashSet<String>();
                            pmd.put((Class<? extends AbstractEntity<?>>) ep.getType(), existing);
                        }
                        existing.add(ep.getName());
                    }
                }

                if (!pmd.isEmpty()) {
                    result.put(entityType, pmd);
                }
            }
        }

        return result;
    }

    private static List<AggregatedResultQueryModel> produceQueries(final Map<Class<? extends AbstractEntity<?>>, Map<Class<? extends AbstractEntity<?>>, Set<String>>> dependenciesMetadata, final Class<? extends AbstractEntity<?>> entityType, final Long entityId) {
        final List<AggregatedResultQueryModel> queries = new ArrayList<>();
        for (final Entry<Class<? extends AbstractEntity<?>>, Map<Class<? extends AbstractEntity<?>>, Set<String>>> el : dependenciesMetadata.entrySet()) {
            if (el.getValue().containsKey(entityType)) {
                final AggregatedResultQueryModel qry = select(el.getKey()).where().anyOfProps(el.getValue().get(entityType).toArray(new String[] {})).eq().val(entityId).yield().val(el.getKey().getName()).as("type").yield().prop("id").as("entity").modelAsAggregate();
                queries.add(qry);
            }
        }
        return queries;
    }

    private static List<PrimitiveResultQueryModel> produceDetailsQueries(final Map<Class<? extends AbstractEntity<?>>, Map<Class<? extends AbstractEntity<?>>, Set<String>>> dependenciesMetadata, final Class<? extends AbstractEntity<?>> entityType) {
        final List<PrimitiveResultQueryModel> queries = new ArrayList<>();
        for (final Entry<Class<? extends AbstractEntity<?>>, Map<Class<? extends AbstractEntity<?>>, Set<String>>> el : dependenciesMetadata.entrySet()) {
            if (el.getValue().containsKey(entityType)) {
                final PrimitiveResultQueryModel qry = select(el.getKey()).where().anyOfProps(el.getValue().get(entityType).toArray(new String[] {})).eq().extProp("id").yield().prop("id").modelAsPrimitive();
                queries.add(qry);
            }
        }
        return queries;
    }
}