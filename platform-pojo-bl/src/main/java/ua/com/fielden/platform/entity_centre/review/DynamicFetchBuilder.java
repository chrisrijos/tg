package ua.com.fielden.platform.entity_centre.review;

import static ua.com.fielden.platform.entity.AbstractPersistentEntity.LAST_UPDATED_BY;
import static ua.com.fielden.platform.entity.AbstractPersistentEntity.LAST_UPDATED_DATE;
import static ua.com.fielden.platform.entity.AbstractPersistentEntity.LAST_UPDATED_TRANSACTION_GUID;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchOnly;
import static ua.com.fielden.platform.utils.EntityUtils.isEntityType;
import static ua.com.fielden.platform.utils.EntityUtils.isSyntheticEntityType;

import java.util.Set;

import ua.com.fielden.platform.dynamictree.DynamicEntityTree;
import ua.com.fielden.platform.dynamictree.DynamicEntityTreeNode;
import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.AbstractPersistentEntity;
import ua.com.fielden.platform.entity.ActivatableAbstractEntity;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils;
import ua.com.fielden.platform.entity.query.fluent.fetch;

public class DynamicFetchBuilder {
    
    private DynamicFetchBuilder() {}

    /**
     * Creates "fetch property" model for entity query criteria.
     *
     * @return
     */
    public static <T extends AbstractEntity<?>> fetch<T> createFetchOnlyModel(final Class<T> managedType, final Set<String> fetchProperties) {
        return fetch(managedType, fetchProperties, true);
    }

    /**
     * Creates "fetch property" model for entity query criteria.
     *
     * @return
     */
    public static <T extends AbstractEntity<?>> fetch<T> createFetchModel(final Class<T> managedType, final Set<String> fetchProperties) {
        return fetch(managedType, fetchProperties, false);
    }

    /**
     * Creates "fetch property" model for entity query criteria totals.
     *
     * @return
     */
    public static <T extends AbstractEntity<?>> fetch<T> createTotalFetchModel(final Class<T> managedType, final Set<String> fetchProperties) {
        final fetch<T> result = fetch(managedType, fetchProperties, true);
        return isSyntheticEntityType(managedType) ? result : withoutLowLevelProps(managedType, result);
    }

    /**
     * Constructs a fetch strategy based on the provided <code>uncompletedFetch</code> without so called <code>low level</code> properties,
     * such as <code>id</code>, <code>version</code> for all entity types and <code>active</code>, <code>refCount</code> for activatable entity types.
     *
     * @param managedType
     * @param uncompletedFetch
     * @return
     */
    private static <T extends AbstractEntity<?>> fetch<T> withoutLowLevelProps(final Class<T> managedType, final fetch<T> uncompletedFetch) {
        final fetch<T> result;
        if (ActivatableAbstractEntity.class.isAssignableFrom(managedType)) {
            result = uncompletedFetch.without(AbstractEntity.ID).without(AbstractEntity.VERSION)
                    .without(ActivatableAbstractEntity.ACTIVE).without(ActivatableAbstractEntity.REF_COUNT)
                    .without(LAST_UPDATED_BY).without(LAST_UPDATED_DATE).without(LAST_UPDATED_TRANSACTION_GUID);
        } else if (AbstractPersistentEntity.class.isAssignableFrom(managedType)) {
            result = uncompletedFetch.without(AbstractEntity.ID).without(AbstractEntity.VERSION)
                    .without(LAST_UPDATED_BY).without(LAST_UPDATED_DATE).without(LAST_UPDATED_TRANSACTION_GUID);
        } else {
            result = uncompletedFetch.without(AbstractEntity.ID).without(AbstractEntity.VERSION);
        }

        return result;
    }

    /**
     * Creates general fetch model for passed properties and type.
     *
     * @param managedType
     * @param fetchProperties
     */
    private static <T extends AbstractEntity<?>> fetch<T> fetch(final Class<T> managedType, final Set<String> fetchProperties, final boolean fetchOnly) {
        try {
            final DynamicEntityTree<T> fetchTree = new DynamicEntityTree<T>(fetchProperties, managedType);
            final fetch<T> main = buildFetchModels(managedType, fetchTree.getRoot(), fetchOnly);
            return main;
        } catch (final Exception e1) {
            throw new RuntimeException(e1);
        }
    }

    /**
     * Builds the fetch model for subtree specified with treeNode parameter.
     *
     * @param entityType
     *            - The type for fetch model.
     * @param treeNode
     *            - the root of subtree for which fetch model must be build.
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private static <T extends AbstractEntity<?>> fetch<T> buildFetchModels(final Class<T> entityType, final DynamicEntityTreeNode treeNode, final boolean fetchOnly)
            throws Exception {
        fetch<T> fetchModel = fetchOnly ? fetchOnly(entityType) : EntityQueryUtils.fetch(entityType);

        if (treeNode == null || treeNode.getChildCount() == 0) {
            return fetchModel;
        }

        for (final DynamicEntityTreeNode dynamicTreeNode : treeNode.getChildren()) {
            final Class<?> propertyType = dynamicTreeNode.getType();
            if (!isEntityType(propertyType)) {
                fetchModel = fetchModel.with(dynamicTreeNode.getName());
            } else {
                final fetch<? extends AbstractEntity<?>> fetchSubModel = buildFetchModels((Class<? extends AbstractEntity<?>>) propertyType, dynamicTreeNode, fetchOnly);
                fetchModel = fetchModel.with(dynamicTreeNode.getName(), fetchSubModel);
            }
        }
        return fetchModel;
    }

}
