package ua.com.fielden.platform.web.action.pre;

import ua.com.fielden.platform.entity.EntityNavigationAction;
import ua.com.fielden.platform.web.minijs.JsCode;
import ua.com.fielden.platform.web.view.master.api.actions.pre.IPreAction;

/**
 * This pre-action implementation should be used only with sequential edit action.
 *
 * @author TG Team
 *
 */
public class EntityNavigationPreAction implements IPreAction {

    private final String navigationType;

    public EntityNavigationPreAction(final String navigationType) {
        this.navigationType = navigationType;
    }

    @Override
    public JsCode build() {
        return new JsCode(String.format("%n"
                + "if (!action.supportsNavigation) {%n"
                + "    action.supportsNavigation = true;%n"
                + "    action.navigationType = '%s';%n"
                + "    action._oldRestoreActiveElement = action.restoreActiveElement;%n"
                + "    action.restoreActiveElement = function () {%n"
                + "        action._oldRestoreActiveElement();%n"
                + "        this.$.egi.editEntity(null);%n"
                + "        action.currentEntity = action.oldCurrentEntity;%n"
                + "        action.currentlyLoadedEntity = null;%n"
                + "        this.removeEventListener('tg-entity-centre-refreshed', action._updateNavigationProps);%n"
                + "        delete action.count;%n"
                + "        delete action.entInd;%n"
                + "        delete action.hasPrev;%n"
                + "        delete action.hasNext;%n"
                + "    }.bind(self);%n"
                + "    action._findNextEntityTo = function (entityIndex) {%n"
                + "        if (action.chosenProperty) {%n"
                + "            return this.$.egi.filteredEntities.slice(entityIndex + 1).find(ent => ent.get(action.chosenProperty));%n"
                + "        }%n"
                + "        return this.$.egi.filteredEntities[entityIndex + 1];%n"
                + "    }.bind(self);%n"
                + "    action._findPreviousEntityTo = function (entityIndex) {%n"
                + "        if (action.chosenProperty) {%n"
                + "            return this.$.egi.filteredEntities.slice(0, entityIndex).reverse().find(ent => ent.get(action.chosenProperty));%n"
                + "        }%n"
                + "        return this.$.egi.filteredEntities[entityIndex - 1];%n"
                + "    }.bind(self);%n"
                + "    action._findFirstEntity = function () {%n"
                + "        if (action.chosenProperty) {%n"
                + "            return this.$.egi.filteredEntities.find(ent => ent.get(action.chosenProperty));%n"
                + "        }%n"
                + "        return this.$.egi.filteredEntities[0];%n"
                + "    }.bind(self);%n"
                + "    action._findLastEntity = function () {%n"
                + "        if (action.chosenProperty) {%n"
                + "            return this.$.egi.filteredEntities.slice().reverse().find(ent => ent.get(action.chosenProperty));%n"
                + "        }%n"
                + "        return this.$.egi.filteredEntities[this.$.egi.filteredEntities.length - 1];%n"
                + "    }.bind(self);%n"
                + "    action._setEntityAndReload = function (entity) {%n"
                + "        if (entity) {%n"
                + "            action.currentlyLoadedEntity = entity;%n"
                + "            action.currentEntity = entity;%n"
                + "            this.$.egi.editEntity(entity);%n"
                + "            const master = action._masterReferenceForTesting;%n"
                + "            if (master) {%n"
                + "                master.savingContext = action._createContextHolderForAction();%n"
                + "                master.retrieve(master.savingContext).then(function(ironRequest) {%n"
                + "                    if (action.modifyFunctionalEntity) {%n"
                + "                        action.modifyFunctionalEntity(master._currBindingEntity, master, action);%n"
                + "                    }%n"
                + "                    master.save().then(function (ironRequest) {%n"
                + "                        action._updateNavigationProps();%n"
                + "                    }.bind(self));%n"
                + "                }.bind(self));%n"
                + "            }%n"
                + "         }%n"
                + "    }.bind(self);%n"
                + "    action.firstEntry = function() {%n"
                + "        action._setEntityAndReload(action._findFirstEntity());%n"
                + "    }.bind(self);%n"
                + "    action.previousEntry = function() {%n"
                + "        const entityIndex = this.$.egi.findEntityIndex(action.currentlyLoadedEntity);%n"
                + "        if (entityIndex >= 0) {%n"
                + "            action._setEntityAndReload(action._findPreviousEntityTo(entityIndex));%n"
                + "        } else {%n"
                + "            action._setEntityAndReload(action._findFirstEntity());%n"
                + "        }%n"
                + "    }.bind(self);%n"
                + "    action.nextEntry = function() {%n"
                + "        const entityIndex = this.$.egi.findEntityIndex(action.currentlyLoadedEntity);%n"
                + "        if (entityIndex >= 0) {%n"
                + "            action._setEntityAndReload(action._findNextEntityTo(entityIndex));%n"
                + "        } else {%n"
                + "            action._setEntityAndReload(action._findFirstEntity());%n"
                + "        }%n"
                + "    }.bind(self);%n"
                + "    action.lastEntry = function() {%n"
                + "        action._setEntityAndReload(action._findLastEntity());%n"
                + "    }.bind(self);%n"
                + "    action.hasPreviousEntry = function() {%n"
                + "        const thisPageInd = this.$.egi.findEntityIndex(action.currentlyLoadedEntity);%n"
                + "        if (thisPageInd >= 0) {%n"
                + "            return action._findFirstEntity().get('id') !== action.currentlyLoadedEntity.get('id');%n"
                + "        }%n"
                + "        return true;%n"
                + "    }.bind(self);%n"
                + "    action.hasNextEntry = function(entitiesCount, entityIndex) {%n"
                + "        const thisPageInd = this.$.egi.findEntityIndex(action.currentlyLoadedEntity);%n"
                + "        if (thisPageInd >= 0) {%n"
                + "            return action._findLastEntity().get('id') !== action.currentlyLoadedEntity.get('id');%n"
                + "        }%n"
                + "        return true;%n"
                + "    }.bind(self);%n"
                + "    action._updateNavigationProps = function (e) {%n"
                + "        const pageNumber = this.$.selection_criteria.pageNumber;%n"
                + "        const pageCapacity = this.$.selection_criteria.pageCapacity;%n"
                + "        const thisPageCapacity = this.$.egi.entities.length;%n"
                + "        const thisPageInd = this.$.egi.findEntityIndex(action.currentlyLoadedEntity);%n"
                + "        const totalCount = pageNumber * pageCapacity + thisPageCapacity;%n"
                + "        action.count = action.count && action.count > totalCount? action.count : totalCount;%n"
                + "        action.entInd = thisPageInd >= 0 ? pageNumber * pageCapacity + thisPageInd : action.entInd;%n"
                + "        action.hasPrev  = action.hasPreviousEntry();%n"
                + "        action.hasNext = action.hasNextEntry();%n"
                + "        action.fire('tg-action-navigation-changed', {%n"
                + "            hasPrev: action.hasPrev,%n"
                + "            hasNext: action.hasNext,%n"
                + "            count: action.count,%n"
                + "            entInd: action.entInd,%n"
                + "        });%n"
                + "    }.bind(self);%n"
                + "    self.addEventListener('tg-entity-centre-refreshed', action._updateNavigationProps);%n"
                + "}%n"
                + "if (action.currentEntity) {%n"
                + "    action.oldCurrentEntity = action.currentEntity;%n"
                + "    self.$.egi.editEntity(action.currentEntity);%n"
                + "    action.currentlyLoadedEntity = action.currentEntity;%n"
                + "} else if (action.count > 0) {%n"
                + "    action.currentEntity = action._findFirstEntity();%n"
                + "    action.currentlyLoadedEntity = action.currentEntity;%n"
                + "    self.$.egi.editEntity(action.currentEntity);%n"
                + "}%n"
                + "action._updateNavigationProps();%n", navigationType, EntityNavigationAction.class.getName()));
    }

}
