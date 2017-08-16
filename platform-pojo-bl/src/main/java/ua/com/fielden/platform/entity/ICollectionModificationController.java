package ua.com.fielden.platform.entity;

import java.util.Collection;
import java.util.Optional;

import ua.com.fielden.platform.web.centre.CentreContext;

/**
 * An interface for controlling how to interact with collection modification functional entity.
 * 
 * @author TG Team
 *
 */
public interface ICollectionModificationController<MASTER_TYPE extends AbstractEntity<?>, T extends AbstractFunctionalEntityForCollectionModification<ID_TYPE>, ID_TYPE, ITEM extends AbstractEntity<?>> {
    
    /**
     * Retrieves master entity from context.
     * 
     * @param context
     * @return
     */
    MASTER_TYPE getMasterEntityFromContext(final CentreContext<?, ?> context);
    
    /**
     * Retrieves master entity from action.
     * 
     * @param action
     * @return
     */
    default MASTER_TYPE getMasterEntityFromAction(final T action) {
        return (MASTER_TYPE) action.getMasterEntity();
    }
    
    /**
     * Re-fetches master entity.
     * 
     * @param masterEntity
     * @return
     */
    default MASTER_TYPE refetchMasterEntity(final MASTER_TYPE masterEntity) {
        return masterEntity;
    };
    
    /**
     * Re-fetches action entity related to <code>masterEntity</code>. Must be implemented for persistent functional actions.
     * 
     * @param masterEntity
     * @return
     */
    default T refetchActionEntity(final MASTER_TYPE masterEntity) {
        throw new CollectionModificationException("Not implemented.");
    }
    
    /**
     * Re-fetches available items related to <code>masterEntity</code>. Must be implemented for persistent functional actions.
     * 
     * @param masterEntity
     * @return
     */
    default Collection<ITEM> refetchAvailableItems(final MASTER_TYPE masterEntity) {
        throw new CollectionModificationException("Not implemented.");
    }
    
    /**
     * Sets available items into action entity. Must be implemented for persistent functional actions.
     * 
     * @param action
     * @param availableItems
     * @return
     */
    default T setAvailableItems(final T action, final Collection<ITEM> availableItems) {
        throw new CollectionModificationException("Not implemented.");
    }
    
    /**
     * Returns current version of existing (persisted) collection modification action for concrete <code>masterEntityId</code>.
     * If there is no existing action in the database then -1L is returned.
     * Must be implemented for persistent functional actions.
     * 
     * @param masterEntityId
     * @return
     */
    default Long persistedActionVersion(final Long masterEntityId) {
        throw new CollectionModificationException("Not implemented.");
    }
    
    /**
     * By default, collection modification is prohibited in case of dirty (persisted and changed, new) entity. However, there are edge-cases where collection modification is a part
     * of master entity saving process through the use of continuation. In those cases the master entity will be dirty and the check on dirtiness should be relaxed.
     * <p>
     * This method provides such point of customization.
     * <p>
     * The context is present during producing and is not present during saving.
     * Some computations and corresponding action initialisation could be performed in this method during producing.
     * Results of such computations could be used during saving.
     * 
     * @param action -- collection modification functional entity 
     * @param actionContext -- optional context, which contain in general case computation part to be able to differentiate functional action
     *            origin, for example a) master property action b) continuation etc.
     *        
     * @return
     */
    default boolean skipDirtyChecking(final T action, final Optional<CentreContext<MASTER_TYPE, AbstractEntity<?>>> actionContext) {
        return false;
    }
    
}
