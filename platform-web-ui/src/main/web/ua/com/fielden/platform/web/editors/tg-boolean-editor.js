import '/resources/polymer/@polymer/polymer/polymer-legacy.js';
import '/resources/polymer/@polymer/iron-flex-layout/iron-flex-layout.js';
import '/resources/polymer/@polymer/iron-flex-layout/iron-flex-layout-classes.js';
import '/resources/polymer/@polymer/paper-checkbox/paper-checkbox.js'

import {Polymer} from '/resources/polymer/@polymer/polymer/lib/legacy/polymer-fn.js';
import {html} from '/resources/polymer/@polymer/polymer/lib/utils/html-tag.js';

import {TgEditorBehavior, TgEditorBehaviorImpl, createEditorTemplate} from '/resources/editors/tg-editor-behavior.js'

const additionalTemplate = html`
    <style>
        /* Styles for boolean property editors. */
        paper-checkbox {
            -moz-user-select: none;
            -webkit-user-select: none;
            -ms-user-select: none;
            user-select: none;
            -o-user-select: none;
            font-family: 'Roboto', 'Noto', sans-serif;
            --paper-checkbox-checked-color: var(--paper-light-blue-700);
            --paper-checkbox-checked-ink-color: var(--paper-light-blue-700);
            height: 24px;
            --paper-checkbox-label: {
                display:grid !important;
                transform:scale(0.75);
                transform-origin: left;
                /*TODO consider adding width:130% as the lable was scaled down*/
                font-weight: 400;
                -webkit-font-smoothing: antialiased;
                text-rendering: optimizeLegibility;
                color: #757575 !important;
            };
        }
        
        .truncate {
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
    </style>
    <custom-style>
        <style include="iron-flex iron-flex-reverse iron-flex-alignment iron-flex-factors iron-positioning"></style>
    </custom-style>`;
const customInputTemplate = html`
    <paper-checkbox
            id="input"
            class="paper-input-input custom-input boolean-input layout horizontal center"
            checked="[[_isBooleanChecked(_editingValue)]]"
            disabled$="[[_disabled]]"
            on-change="_onChange"
            tooltip-text$="[[_getTooltip(_editingValue)]]"><span class="truncate">[[propTitle]]</span></paper-checkbox>`;
const propertyActionTemplate = html`<slot name="property-action"></slot>`;

Polymer({
    _template: createEditorTemplate(additionalTemplate, html``, customInputTemplate, html``, html``, propertyActionTemplate),

    is: 'tg-boolean-editor',

    behaviors: [TgEditorBehavior],

    properties: {
        _onChange: {
            type: Function
        },
        
        _isBooleanChecked: {
            type: Function
        }
    },

    created: function () {
        this._editorKind = "BOOLEAN";
    },

    ready: function () {
        this._onChange = (function (e) {
            console.log("_onChange:", e);
            var target = e.target || e.srcElement;
            this._editingValue = this.convertToString(target.checked);
            
            var parentFunction = TgEditorBehaviorImpl.properties._onChange.value.call(this);
            parentFunction.call(this, e);
        }).bind(this);
        
        this._isBooleanChecked = (function (editingValue) {
            return editingValue === 'true';
        }).bind(this);
    },
    
    /**
     * This method returns a default value for '_editingValue', which is used 
     *  for representing the value when no entity was bound to this editor yet.
     *
     * Overriden to return 'false' as the value that will be used when no entity is bound to this editor yet.
     */
    _defaultEditingValue: function () {
        return 'false';
    },
    
    /**
     * Converts the value into string representation (which is used in edititing / comm values).
     */
    convertToString: function (value) {
        return "" + value;
    },

    /**
     * Converts the value from string representation (which is used in edititing / comm values) into concrete type of this editor component (String).
     */
    convertFromString: function (strValue) {
        if (strValue !== "false" && strValue !== "true") {
            throw "The entered check value is incorrect [" + strValue + "].";
        }
        return strValue === "true" ? true : false;
    }
});