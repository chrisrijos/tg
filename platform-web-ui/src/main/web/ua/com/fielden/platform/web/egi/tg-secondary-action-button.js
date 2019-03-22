import '/resources/polymer/@polymer/polymer/polymer-legacy.js';

import '/resources/polymer/@polymer/iron-dropdown/iron-dropdown.js';
import '/resources/polymer/@polymer/iron-flex-layout/iron-flex-layout.js';

import '/resources/polymer/@polymer/paper-icon-button/paper-icon-button.js';

import '/resources/actions/tg-ui-action.js';

import {Polymer} from '/resources/polymer/@polymer/polymer/lib/legacy/polymer-fn.js';
import {html} from '/resources/polymer/@polymer/polymer/lib/utils/html-tag.js';

import { TgFocusRestorationBehavior } from '/resources/actions/tg-focus-restoration-behavior.js';
import { TgTooltipBehavior } from '/resources/components/tg-tooltip-behavior.js';

const template = html`
    <style>
        .dropdown-content {
            background-color: white;
            box-shadow: 0px 2px 6px #ccc;
            padding: 5px;
        }
        .button-container {
            @apply --layout-vertical;
        }
        paper-icon-button {
            height: var(--tg-secondary-action-icon-button-height);
            width: var(--tg-secondary-action-icon-button-width);
            padding: var(--tg-secondary-action-icon-button-padding);
        }
        tg-ui-action {
            --tg-ui-action-icon-button-height: var(--tg-secondary-action-icon-button-height);
            --tg-ui-action-icon-button-width: var(--tg-secondary-action-icon-button-width);
            --tg-ui-action-icon-button-padding: var(--tg-secondary-action-icon-button-padding);
            --tg-ui-action-spinner-width: var(--tg-secondary-action-spinner-width);
            --tg-ui-action-spinner-height: var(--tg-secondary-action-spinner-height);
            --tg-ui-action-spinner-min-width: var(--tg-secondary-action-spinner-min-width);
            --tg-ui-action-spinner-min-height: var(--tg-secondary-action-spinner-min-height);
            --tg-ui-action-spinner-max-width: var(--tg-secondary-action-spinner-max-width);
            --tg-ui-action-spinner-max-height: var(--tg-secondary-action-spinner-max-height);
            --tg-ui-action-spinner-padding: var(--tg-secondary-action-spinner-padding);
            --tg-ui-action-spinner-margin-left: var(--tg-secondary-action-spinner-margin-left);
        }
    </style>
    <template is="dom-if" if="[[_isOnlyOneActions(actions)]]">
        <tg-ui-action class="action" show-dialog="[[actions.0.showDialog]]" current-entity="[[currentEntity]]" short-desc="[[actions.0.shortDesc]]" long-desc="[[actions.0.longDesc]]" icon="[[actions.0.icon]]" component-uri="[[actions.0.componentUri]]" element-name="[[actions.0.elementName]]" action-kind="[[actions.0.actionKind]]" number-of-action="[[actions.0.numberOfAction]]" attrs="[[actions.0.attrs]]" create-context-holder="[[actions.0.createContextHolder]]" require-selection-criteria="[[actions.0.requireSelectionCriteria]]" require-selected-entities="[[actions.0.requireSelectedEntities]]" require-master-entity="[[actions.0.requireMasterEntity]]" pre-action="[[actions.0.preAction]]" post-action-success="[[actions.0.postActionSuccess]]" post-action-error="[[actions.0.postActionError]]" should-refresh-parent-centre-after-save="[[actions.0.shouldRefreshParentCentreAfterSave]]" ui-role="[[actions.0.uiRole]]" icon-style="[[actions.0.iconStyle]]"></tg-ui-action>
    </template>
    <template is="dom-if" if="[[!_isOnlyOneActions(actions)]]">
        <paper-icon-button id="dropDownButton" icon="more-vert" on-tap="_showDropdown" tooltip-text="Opens list of available actions"></paper-icon-button>
        <iron-dropdown id="dropdown" style="color:black" on-tap="_closeDropdown" on-iron-overlay-opened="_dropdownOpened" on-iron-overlay-closed="_dropdownClosed">
            <div class="dropdown-content">
                <div class="button-container">
                    <template is="dom-repeat" items="[[actions]]" as="action">
                        <tg-ui-action show-dialog="[[action.showDialog]]" current-entity="[[currentEntity]]" short-desc="[[action.shortDesc]]" long-desc="[[action.longDesc]]" icon="[[action.icon]]" component-uri="[[action.componentUri]]" element-name="[[action.elementName]]" element-alias="[[action.elementAlias]]" action-kind="[[action.actionKind]]" number-of-action="[[action.numberOfAction]]" attrs="[[action.attrs]]" create-context-holder="[[action.createContextHolder]]" require-selection-criteria="[[action.requireSelectionCriteria]]" require-selected-entities="[[action.requireSelectedEntities]]" require-master-entity="[[action.requireMasterEntity]]" pre-action="[[action.preAction]]" post-action-success="[[action.postActionSuccess]]" post-action-error="[[action.postActionError]]" should-refresh-parent-centre-after-save="[[action.shouldRefreshParentCentreAfterSave]]" ui-role="[[action.uiRole]]"  icon-style="[[action.iconStyle]]"></tg-ui-action>
                    </template>
                </div>
            </div>
        </iron-dropdown>
    </template>`;

Polymer({

    _template: template,

    is: "tg-secondary-action-button",
    
    behaviors: [ TgTooltipBehavior, TgFocusRestorationBehavior ],

    properties: {
        actions: Array,
        /**
         * The 'currentEntity' should contain the entity that was clicked (result-set actions)
         * or the entity on which primary / secondary action was chosen. In case when no of the above cases
         * is invoking (for e.g. as in topLevel actions) -- 'currentEntity' should be empty.
         */
        currentEntity: Object
    },

    /**
     * determines whether secondary actions is only one or not.
     */
    _isOnlyOneActions: function (actions) {
        return actions.length === 1;
    },
    
    _showDropdown: function (e, detail) {
        this.persistActiveElement();
        this.$.dropdown.open();
    },

    _closeDropdown: function (e, detail) {
        this.$.dropdown.close();
    },

    _dropdownOpened: function () {
        var actions = this.shadowRoot.querySelectorAll('tg-ui-action');
        actions.forEach(function (action) {
            action._updateSpinnerIfNeeded();
        });
    },
    
    _dropdownClosed: function () {
        this.restoreActiveElement();
    }
});