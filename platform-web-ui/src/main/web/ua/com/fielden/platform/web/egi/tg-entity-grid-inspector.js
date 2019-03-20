import '/resources/polymer/@polymer/polymer/polymer-legacy.js';

import '/resources/polymer/@polymer/iron-flex-layout/iron-flex-layout.js';
import '/resources/polymer/@polymer/iron-flex-layout/iron-flex-layout-classes.js';
import '/resources/polymer/@polymer/iron-icon/iron-icon.js';
import '/resources/polymer/@polymer/iron-icons/iron-icons.js';

import '/resources/polymer/@polymer/paper-checkbox/paper-checkbox.js';
import '/resources/polymer/@polymer/paper-icon-button/paper-icon-button.js';
import "/resources/polymer/@polymer/paper-styles/element-styles/paper-material-styles.js";
import '/resources/polymer/@polymer/paper-progress/paper-progress.js';
import '/resources/polymer/@polymer/paper-styles/color.js';

import '/resources/images/tg-icons.js';

import '/resources/actions/tg-ui-action.js';
import '/resources/egi/tg-secondary-action-button.js';
import '/resources/egi/tg-egi-cell.js';

import {Polymer} from '/resources/polymer/@polymer/polymer/lib/legacy/polymer-fn.js';
import {html} from '/resources/polymer/@polymer/polymer/lib/utils/html-tag.js';
import { IronA11yKeysBehavior } from '/resources/polymer/@polymer/iron-a11y-keys-behavior/iron-a11y-keys-behavior.js';
import { IronResizableBehavior } from '/resources/polymer/@polymer/iron-resizable-behavior/iron-resizable-behavior.js';

import { TgTooltipBehavior } from '/resources/components/tg-tooltip-behavior.js';
import { TgDragFromBehavior } from '/resources/components/tg-drag-from-behavior.js';
import { TgShortcutProcessingBehavior } from '/resources/actions/tg-shortcut-processing-behavior.js';
import '/resources/reflection/tg-polymer-utils.js';
import '/resources/reflection/tg-date-utils.js';
import { TgReflector } from '/app/tg-reflector.js';
import { TgAppConfig } from '/app/tg-app-config.js';
import { TgSerialiser } from '/resources/serialisation/tg-serialiser.js';

const template = html`
    <style>
        :host {
            @apply --layout-vertical;
        }
        .paper-material {
            border-radius: 2px;
            @apply --layout-vertical;
        }
        .grid-toolbar {
            position: relative;
            overflow: hidden;
            @apply --layout-horizontal;
            @apply --layout-wrap;
        }
        paper-progress {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            width: auto;
        }
        paper-progress.uploading {
            --paper-progress-active-color: var(--paper-light-green-500);
        }
        paper-progress.processing {
            --paper-progress-active-color: var(--paper-orange-500);
        }
        .grid-toolbar-content {
            @apply --layout-horizontal;
            @apply --layout-center;
        }
        .grid-toolbar-content::slotted(*) {
            margin-top: 8px;
        }
        #baseContainer {
            min-height: 0;
            @apply --layout-vertical;
            @apply --layout-flex;
            @apply --layout-relative;
        }
        .table-header-row {
            font-size: 0.9rem;
            font-weight: 400;
            color: #757575;
            height: 3rem;
            -webkit-font-smoothing: antialiased;
            text-rendering: optimizeLegibility;
            min-width: fit-content;
            @apply --layout-horizontal;
        }
        .table-data-row {
            font-size: 1rem;
            font-weight: 400;
            color: #212121;
            height: var(--egi-row-height, 1.5rem);
            border-top: 1px solid #e3e3e3;
            -webkit-font-smoothing: antialiased;
            text-rendering: optimizeLegibility;
            min-width: fit-content;
            @apply --layout-horizontal;
        }
        .table-data-row:hover {
            background-color: #EEEEEE;
        }
        .table-data-row[selected] {
            background-color: #F5F5F5;
        }
        .fixed-columns-container {
            @apply --layout-horizontal;
        }
        .drag-anchor {
            --iron-icon-width: 1.5rem;
            --iron-icon-height: 1.5rem;
            @apply(--layout-self-center);
        }
        .drag-anchor[selected]:hover {
            cursor: move;
            /* fallback if grab cursor is unsupported */
            cursor: grab;
            cursor: -moz-grab;
            cursor: -webkit-grab;
        }
        .drag-anchor[selected]:active {
            cursor: grabbing;
            cursor: -moz-grabbing;
            cursor: -webkit-grabbing;
        }
        .dummy-drag-box {
            width: 1.5rem;
            height: 1.5rem;
        }
        paper-checkbox {
            --paper-checkbox-label: {
                display:none;
            };
        }
        paper-checkbox.blue {
            --paper-checkbox-checked-color: var(--paper-light-blue-700);
            --paper-checkbox-checked-ink-color: var(--paper-light-blue-700);
        }
        paper-checkbox.header {
            --paper-checkbox-unchecked-color: var(--paper-grey-600);
            --paper-checkbox-unchecked-ink-color: var(--paper-grey-600);
        }
        paper-checkbox.body {
            --paper-checkbox-unchecked-color: var(--paper-grey-900);
            --paper-checkbox-unchecked-ink-color: var(--paper-grey-900);
        }
        .table-cell {
            @apply --layout-horizontal;
            @apply --layout-center;
            @apply --layout-relative;
            padding: 0 0.6rem;
        }
        tg-egi-cell[with-action] {
            cursor:pointer;
        }
        .action-cell {
            @apply --layout-horizontal;
            @apply --layout-center;
            width: 20px;
            padding: 0 0.3rem;
        }
        .drag-anchor {
            --iron-icon-width: 1.5rem;
            --iron-icon-height: 1.5rem;
            @apply(--layout-self-center);
        }
    </style>
    <custom-style>
        <style include="iron-flex iron-flex-reverse iron-flex-alignment iron-flex-factors iron-positioning paper-material-styles"></style>
    </custom-style>
    <!--configuring slotted elements-->
    <slot id="column_selector" name="property-column" hidden></slot>
    <slot id="primary_action_selector" name="primary-action" hidden></slot>
    <slot id="secondary_action_selector" name="secondary-action" hidden></slot>
    <!--EGI template-->
    <div class="paper-material" elevation="1">
        <!--Table toolbar-->
        <div class="grid-toolbar">
            <paper-progress id="progressBar" hidden$="[[!_showProgress]]"></paper-progress>
            <div class="grid-toolbar-content">
                <slot id="top_action_selctor" name="entity-specific-action"></slot>
            </div>
            <div class="grid-toolbar-content" style="margin-left:auto">
                <slot name="standart-action"></slot>
            </div>
        </div>
        <div id="baseContainer">
            <!-- Table header -->
            <div class="table-header-row">
                <div class="dummy-drag-box" hidden$="[[!canDragFrom]]" style$="[[_calcDragBoxStyle(dragAnchorFixed, headerFixed)]]"></div>
                <div class="table-cell" style$="[[_calcSelectCheckBoxStyle(canDragFrom, checkboxesFixed, headerFixed)]]" hidden$="[[!checkboxVisible]]" tooltip-text$="[[_selectAllTooltip(selectedAll)]]">
                    <paper-checkbox class="all-checkbox blue header" checked="[[selectedAll]]" on-change="_allSelectionChanged"></paper-checkbox>
                </div>
                <div class="action-cell" hidden$="[[!primaryAction]]" style$="[[_calcPrimaryActionStyle(canDragFrom, checkboxVisible, checkboxesWithPrimaryActionsFixed, headerFixed)]]">
                    <!--Primary action stub header goes here-->
                </div>
                <div class="fixed-columns-container" hidden$="[[!numOfFixedCols]]" style$="[[_calcFixedColumnContainerStyle(canDragFrom, checkboxVisible, primaryAction, numOfFixedCols, headerFixed)]]">
                    <template is="dom-repeat" items="[[fixedColumns]]">
                        <div class="table-cell" style$="[[_calcColumnHeaderStyle(item, item.width, item.growFactor, index, numOfFixedCols)]]" on-down="_makeEgiUnselectable" on-up="_makeEgiSelectable" on-track="_changeColumnSize" tooltip-text$="[[item.columnDesc]]" is-resizing$="[[_columnResizingObject]]" is-mobile$="[[mobile]]">
                            <div class="truncate" style="width:100%">[[item.columnTitle]]</div>
                            <div class="resizing-box"></div>
                        </div>
                    </template>
                </div>
                <template is="dom-repeat" items="[[columns]]">
                    <div class="table-cell" style$="[[_calcColumnHeaderStyle(item, item.width, item.growFactor, index, numOfFixedCols)]]" on-down="_makeEgiUnselectable" on-up="_makeEgiSelectable" on-track="_changeColumnSize" tooltip-text$="[[item.columnDesc]]" is-resizing$="[[_columnResizingObject]]" is-mobile$="[[mobile]]">
                        <div class="truncate" style="width:100%">[[item.columnTitle]]</div>
                        <div class="resizing-box"></div>
                    </div>
                </template>
                <div class="action-cell" hidden$="[[!_isSecondaryActionsPresent(secondaryActions)]]" style$="[[_calcSecondaryActionStyle(secondaryActionsFixed, headerFixed)]]">
                    <!--Secondary actions header goes here-->
                </div>
            </div>
            <template is="dom-repeat" items="[[egiModel]]" as="egiEntity" index-as="entityIndex" >
                <div class="table-data-row" selected$="[[egiEntity.selected]]">
                    <iron-icon draggable="true" class="drag-anchor" selected$="[[egiEntity.selected]]" hidden$="[[!canDragFrom]]" style$="[[_calcDragBoxStyle(dragAnchorFixed)]]" icon="tg-icons:dragVertical"></iron-icon>
                    <div class="table-cell" style$="[[_calcSelectCheckBoxStyle(canDragFrom, checkboxesFixed)]]" hidden$="[[!checkboxVisible]]" tooltip-text$="[[_selectTooltip(egiEntity.selected)]]">
                        <paper-checkbox class="blue body" checked="[[egiEntity.selected]]" on-change="_selectionChanged" on-mousedown="_checkSelectionState" on-keydown="_checkSelectionState"></paper-checkbox>
                    </div>
                    <div class="action-cell" hidden$="[[!primaryAction]]" style$="[[_calcPrimaryActionStyle(canDragFrom, checkboxVisible, checkboxesWithPrimaryActionsFixed)]]">
                        <tg-ui-action class="action" show-dialog="[[primaryAction.showDialog]]" current-entity="[[egiEntity.entity]]" short-desc="[[primaryAction.shortDesc]]" long-desc="[[primaryAction.longDesc]]" icon="[[primaryAction.icon]]" component-uri="[[primaryAction.componentUri]]" element-name="[[primaryAction.elementName]]" action-kind="[[primaryAction.actionKind]]" number-of-action="[[primaryAction.numberOfAction]]" attrs="[[primaryAction.attrs]]" create-context-holder="[[primaryAction.createContextHolder]]" require-selection-criteria="[[primaryAction.requireSelectionCriteria]]" require-selected-entities="[[primaryAction.requireSelectedEntities]]" require-master-entity="[[primaryAction.requireMasterEntity]]" pre-action="[[primaryAction.preAction]]" post-action-success="[[primaryAction.postActionSuccess]]" post-action-error="[[primaryAction.postActionError]]" should-refresh-parent-centre-after-save="[[primaryAction.shouldRefreshParentCentreAfterSave]]" ui-role="[[primaryAction.uiRole]]" icon-style="[[primaryAction.iconStyle]]"></tg-ui-action>
                    </div>
                    <div class="fixed-columns-container" hidden$="[[!numOfFixedCols]]" style$="[[_calcFixedColumnContainerStyle(canDragFrom, checkboxVisible, primaryAction, numOfFixedCols)]]">
                        <template is="dom-repeat" items="[[fixedColumns]]">
                            <tg-egi-cell column="[[column]]" entity="[[egiEntity.entity]]" with-action$="[[hasAction(egiEntity.entity, column)]]"></tg-egi-cell>
                        </template>
                    </div>
                    <template is="dom-repeat" items="[[columns]]" as="column">
                        <tg-egi-cell column="[[column]]" entity="[[egiEntity.entity]]" with-action$="[[hasAction(egiEntity.entity, column)]]"></tg-egi-cell>
                    </template>
                    <div class="action-cell layout horizontal center no-flexible">
                        <div class="action"></div>
                    </div>
                </div>
            </template>
        </div>
    </div>`;

Polymer({

    _template: template,

    is: 'tg-entity-grid-inspector',
    
    properties: {
        mobile: Boolean,
        /** An extrenally assigned function that accepts an instance of type Attachment as an argument and starts the download of the associated file. */
        downloadAttachment: {
            type: Function
        },

        entities: {
            type: Array,
            observer: "_entitiesChanged"
        },
        filteredEntities: {
            type: Array,
            observer: "_filteredEntitiesChanged"
        },
        selectedEntities: Array,
        /** The currently editing entity*/
        editingEntity: {
            type: Object,
            value: null
        },

        totals: Object,
        
        columns: {
            type: Array,
            observer: "_columnsChanged"
        },
        allColumns: Array,

        fixedColumns: {
            type: Array,
            computed: '_computeFixedColumns(columns, numOfFixedCols)'
        },

        renderingHints: {
            type: Array,
            observer: "_renderingHintsChanged"
        },
        selectedAll: {
            type: Boolean,
            value: false
        },
        //Determines whether entities can be dragged from this EGI.
        canDragFrom: {
            type: Boolean,
            value: false
        },
        //Controls visiblity of checkboxes at the beginnig of the header and each data row.
        checkboxVisible: {
            type: Boolean,
            value: false
        },
        //Scrolling related properties.
        dragAnchorFixed: {
            type: Boolean,
            value: false
        },
        checkboxesFixed: {
            type: Boolean,
            value: false
        },
        checkboxesWithPrimaryActionsFixed: {
            type: Boolean,
            value: false
        },
        numOfFixedCols: {
            type: Number,
            value: 0
        },
        secondaryActionsFixed: {
            type: Boolean,
            value: false
        },
        headerFixed: {
            type: Boolean,
            value: false
        },
        summaryFixed: {
            type: Boolean,
            value: false
        },
        //Range selection related properties
        _rangeSelection: {
            type: Boolean,
            value: false
        },
        _lastSelectedIndex: Number
    },

    behaviors: [TgTooltipBehavior, IronResizableBehavior, IronA11yKeysBehavior, TgShortcutProcessingBehavior, TgDragFromBehavior],

    created: function () {
        this._reflector = new TgReflector();
        this._appConfig = new TgAppConfig();
        this._serialiser = new TgSerialiser();
        //Configure device profile
        this.mobile = this._appConfig.mobile;

        this._totalsRowCount = 0;
        this._showProgress = false;

        //Initialising shadows
        this._showTopShadow = false;
        this._showBottomShadow = false;
        this._showLeftShadow = false;
        this._showRightShadow = false;

        //Initilialising scrolling properties
        this._scrollLeft = 0;
        this._scrollTop = 0;

        //Initialising entities.
        this.totals = null;
        this.entities = [];

        //Initialising the egi model .
        this.egiModel = [];

        //initialising the arrays for selected entites.
        this.selectedAll = false;
        this.selectedEntities = [];

        //Initialise columns
        this.allColumns = [];
        this.columns = [];
    },

    ready: function () {
        const primaryActions = this.$.primary_action_selector.assignedNodes();

        //Initialising the primary action.
        this.primaryAction = primaryActions.length > 0 ? primaryActions[0] : null;

        //Initialising the secondary actions' list.
        this.secondaryActions = this.$.secondary_action_selector.assignedNodes();

        //Initialising event listeners.
        this.addEventListener("iron-resize", this._resizeEventListener.bind(this));

        //Observe column DOM changes
        this.$.column_selector.addEventListener('slotchange', (e) => {
            this.allColumns = this.$.column_selector.assignedNodes();
            this._adjustColumns(this.columns.filter(column => this.allColumns.some(elem => elem.property === column.property)));
            
          });
    },

    //Filtering related functions
    filter: function () {
        const tempFilteredEntities = [];
        this.entities.forEach(entity => {
            if (this.isVisible(entity)) {
                tempFilteredEntities.push(entity);
            }
        });
        this.filteredEntities = tempFilteredEntities;
    },

    hasAction: function (entity, column) {
        return column.customAction || this._isHyperlinkProp(entity, column) === true || this._getAttachmentIfPossible(entity, column);
    },

    isVisible: function (entity) {
        return true;
    },

    /**
     * Selects/unselects all entities.
     */
    selectAll: function (checked) {
        if (this.egiModel) {
            const selectionDetails = [];
            for (let i = 0; i < this.egiModel.length; i += 1) {
                if (this.egiModel[i].selected !== checked) {
                    this.set("egiModel." + i + ".selected", checked);
                    this._processEntitySelection(this.filteredEntities[i], checked);
                    selectionDetails.push({
                        entity: this.filteredEntities[i],
                        select: checked
                    });
                }
            }
            this.selectedAll = checked;
            if (selectionDetails.length > 0) {
                this.fire("tg-entity-selected", {
                    shouldScrollToSelected: false,
                    entities: selectionDetails
                });
            }
        }
    },
    //Entities changed related functions

    _entitiesChanged: function (newEntities, oldEntities) {
        this.filter();  
    },

    _filteredEntitiesChanged: function (newValue, oldValue) {
        const tempEgiModel = [];
        newValue.forEach(newEntity => {
            const selectEntInd = this._findEntity(newEntity, this.selectedEntities);
            if (selectEntInd >= 0) {
                this.selectedEntities[selectEntInd] = newEntity;
            }
            if (this.editingEntity && this._areEqual(this.editingEntity, newEntity)) {
                this.editingEntity = newEntity;
            }
        });
        newValue.forEach(newEntity => {
            const isSelected = this.selectedEntities.indexOf(newEntity) > -1;
            const oldIndex = this._findEntity(newEntity, oldValue);
            const newRendHints = oldIndex < 0 ? {} : (this.renderingHints && this.renderingHints[oldIndex]) || {};
            const egiEntity = {
                over: this._areEqual(this.editingEntity, newEntity),
                selected: isSelected,
                entity: newEntity,
                renderingHints: newRendHints
            };
            tempEgiModel.push(egiEntity);
        });
        //updateSelectAll(this, tempEgiModel);
        this.egiModel = tempEgiModel;
        //this._updateTableSizeAsync();
        this.fire("tg-egi-entities-loaded", newValue);
    },

    _adjustColumns: function (newColumns) {
        const resultantColumns = [];
        newColumns.forEach(columnName => {
            const column = this.allColumns.find(item => item.property === columnName);
            if (column) {
                resultantColumns.push(column);
            }
        });
        if (!this._reflector.equalsEx(this.columns, resultantColumns)) {
            this.columns = resultantColumns;
            const columnWithGrowFactor = this.columns.find((item, index) => index >= this.numOfFixedCols && item.growFactor > 0);
            if (!columnWithGrowFactor && this.columns.length > 0 && this.columns.length >= this.numOfFixedCols) {
                this.set("columns." + (this.columns.length - 1) + ".growFactor", 1);
                const column = this.columns[this.columns.length - 1];
                const parameters = {};
                parameters[column.property] = {
                    growFactor: 1
                }
                this.fire("tg-egi-column-change", parameters);
            }
        }
        //this._updateColumnsWidthProperties();
    },

    //Event listeners
    _resizeEventListener: function() {

    },

    _allSelectionChanged: function (e) {
        const target = e.target || e.srcElement;
        this.selectAll(target.checked);
    },

    _selectionChanged: function (e) {
        if (this.egiModel) {
            const index = e.model.entityIndex;
            var target = e.target || e.srcElement;
            //Perform selection range selection or single selection.
            if (target.checked && this._rangeSelection && this._lastSelectedIndex >= 0) {
                this._selectRange(this._lastSelectedIndex, index);
            } else {
                this.set("egiModel." + index + ".selected", target.checked);
                this._processEntitySelection(this.filteredEntities[index], target.checked);
                this.fire("tg-entity-selected", {
                    shouldScrollToSelected: false,
                    entities: [{
                        entity: this.filteredEntities[index],
                        select: target.checked
                    }]
                });
            }
            //Set up the last selection index (it will be used for range selection.)
            if (target.checked) {
                this._lastSelectedIndex = index;
            } else {
                this._lastSelectedIndex = -1;
            }
            //Set up selecteAll property.
            if (this.selectedAll && !target.checked) {
                this.selectedAll = false;
            } else if (this.egiModel.length > 0 && this.egiModel.every(elem => elem.selected)) {
                this.selectedAll = true;
            }
        }
    },

    _checkSelectionState: function (event) {
        this._rangeSelection = event.shiftKey;
    },

    //Style calculator

    _calcDragBoxStyle: function (dragAnchorFixed, headerFixed) {
        let style = dragAnchorFixed || headerFixed ? "postion: sticky;" : "";

        if (dragAnchorFixed) {
            style += "left: 0;";
        }
        if (headerFixed) {
            style += "top: 0;"
        }
        return style;
    },

    _calcSelectCheckBoxStyle: function (canDragFrom, checkboxesFixed, headerFixed) {
        let style = checkboxesFixed || headerFixed ? "postion: sticky;" : "";
        if (checkboxesFixed) {
            style += "left: " + (canDragFrom ? "1.5rem" : "0") + ";"; 
        }
        if (headerFixed) {
            style += "top: 0";
        }
        return style + "width:18px; padding-left:" + (canDragFrom ? "0;" : "0.6rem;");
    },

    _calcPrimaryActionStyle: function (canDragFrom, checkboxVisible, checkboxesWithPrimaryActionsFixed, headerFixed) {
        let style = checkboxesWithPrimaryActionsFixed || headerFixed ? "postion: sticky;" : "";
        if (checkboxesWithPrimaryActionsFixed) {
            let calcStyle = "calc(" + (canDragFrom ? "1.5rem" : "0");
            calcStyle += (checkboxVisible ? " + 18px" : " + 0") + ")";
            style += "left: " + calcStyle + ";"; 
        }
        if (headerFixed) {
            style += "top: 0";
        }
        return style;
    },

    _calcFixedColumnContainerStyle: function (canDragFrom, checkboxVisible, primaryAction, numOfFixedCols, headerFixed) {
        let style = numOfFixedCols > 0 || headerFixed ? "postion: sticky;" : "";
        if (numOfFixedCols > 0) {
            let calcStyle = "calc(" + (canDragFrom ? "1.5rem" : "0");
            calcStyle += (checkboxVisible ? " + 18px" : " + 0");
            calcStyle += (primaryAction ? " + 20px" : " + 0") + ")";
            style += "left: " + calcStyle + ";";
        }
        if (headerFixed) {
            style += "top: 0";
        }
        return style;
    },

    _calcColumnHeaderStyle: function (item, itemWidth, columnGrowFactor, index, numOfFixedCols) {
        let colStyle = "min-width: " + itemWidth + "px;" + "width: " + itemWidth + "px;"
        if (columnGrowFactor === 0 || index < numOfFixedCols) {
            colStyle += "flex-grow: 0;flex-shrink:0;";
        } else {
            colStyle += "flex-grow: " + columnGrowFactor + ";";
        }
        if (itemWidth === 0) {
            colStyle += "display: none;";
        }
        if (item.type === 'Integer' || item.type === 'BigDecimal' || item.type === 'Money') {
            colStyle += "text-align: right;"
        }
        return colStyle;
    },

    _calcSecondaryActionStyle: function (secondaryActionsFixed, headerFixed) {
        let style = secondaryActionsFixed || headerFixed ? "postion: sticky;" : "";

        if (secondaryActionsFixed) {
            style += "right: 0;";
        }
        if (headerFixed) {
            style += "top: 0;"
        }
        return style;
    },

    // Observers
    _computeFixedColumns: function (columns, numOfFixedCols) {
        this.fixedColumns = columns.slice(0, numOfFixedCols);
    },

    _isSecondaryActionsPresent: function (secondaryActions) {
        return secondaryActions && secondaryActions.length > 0;
    },

    //Tooltip related functions.
    _selectAllTooltip: function (selectedAll) {
        return (selectedAll ? 'Unselect' : 'Select') + ' all entities';
    },

    _selectTooltip: function (selected) {
        return (selected ? 'Unselect' : 'Select') + ' this entity';
    },

    //Utility methods
    _areEqual: function (a, b) {
        if (a && b && a.get('id') && b.get('id')) {
            return a.get('id') === b.get('id');
        }
        try {
            return this._reflector.equalsEx(a, b);
        } catch (e) {
            return false;
        }
    },
    
    _findEntity: function (entity, entities) {
        for (let i = 0; i < entities.length; i += 1) {
            if (this._areEqual(entity, entities[i])) {
                return i;
            }
        }
        return -1;
    },

    _processEntitySelection: function (entity, select) {
        const selectedIndex = this._findEntity(entity, this.selectedEntities);
        if (select) {
            if (selectedIndex < 0) {
                this.selectedEntities.push(entity);
            }
        } else {
            if (selectedIndex >= 0) {
                this.selectedEntities.splice(selectedIndex, 1);
            }
        }
    },

    _selectRange: function (fromIndex, toIndex) {
        const from = fromIndex < toIndex ? fromIndex : toIndex;
        const to = fromIndex < toIndex ? toIndex : fromIndex;
        const selectionDetails = [];
        for (let i = from; i <= to; i++) {
            if (!this.egiModel[i].selected) {
                this.set("egiModel." + i + ".selected", true);
                this._processEntitySelection(this.filteredEntities[i], true);
                selectionDetails.push({
                    entity: this.filteredEntities[i],
                    select: true
                });
            }
        }
        if (selectionDetails.length > 0) {
            this.fire("tg-entity-selected", {
                shouldScrollToSelected: false,
                entities: selectionDetails
            });
        }
    },

    _isHyperlinkProp: function (entity, column) {
        return column.type === 'Hyperlink' && this._getValueFromEntity(entity, column) !== null
    },

    _getAttachmentIfPossible: function (entity, column) {
        if (entity.type && entity.type().notEnhancedFullClassName() === "ua.com.fielden.platform.attachment.Attachment") {
            return entity;
        } else if (this._getValueFromEntity(entity, column) && this._getValueFromEntity(entity, column).type &&
            this._getValueFromEntity(entity, column).type().notEnhancedFullClassName() === "ua.com.fielden.platform.attachment.Attachment") {
            return this._getValueFromEntity(entity, column);
        } else if (this._reflector.entityPropOwner(entity, column)) {
            const owner = this._reflector.entityPropOwner(entity, column);
            if (owner.type().notEnhancedFullClassName() === "ua.com.fielden.platform.attachment.Attachment") {
                return owner;
            }
            return null;
        } else {
            return null;
        }
    },

    _getValueFromEntity: function (entity, column) {
        return entity && entity.get(column.property);
    }
});