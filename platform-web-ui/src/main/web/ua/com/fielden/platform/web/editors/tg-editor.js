import '/resources/polymer/@polymer/polymer/polymer-legacy.js';
import '/resources/polymer/@polymer/iron-flex-layout/iron-flex-layout.js';
import '/resources/polymer/@polymer/iron-flex-layout/iron-flex-layout-classes.js';
import '/resources/polymer/@polymer/iron-icon/iron-icon.js';
import '/resources/polymer/@polymer/iron-icons/iron-icons.js';
import '/resources/polymer/@polymer/paper-input/paper-input-container.js'
import '/resources/polymer/@polymer/paper-input/paper-input-error.js'
import '/resources/polymer/@polymer/paper-input/paper-input-char-counter.js'

import '/app/tg-reflector.js'

import {Polymer} from '/resources/polymer/@polymer/polymer/lib/legacy/polymer-fn.js';
import {html} from '/resources/polymer/@polymer/polymer/lib/utils/html-tag.js';

import {TgTooltipBehavior} from '/resources/components/tg-tooltip-behavior.js'

const template = html`
    <style>
        .main-container {
            @apply --layout-horizontal;
            @apply --layout-center;
            @apply --tg-editor-main-container-mixin;
        }
        
        #input-container {
            @apply --layout-flex;
            @apply --tg-editor-input-container-mixin;
        }
        
        #input-container[disabled] {
            @apply --tg-editor-input-container-disabled-mixin;
        }

        #input-container ::slotted(.input-layer) {
            font-size: 16px;
            line-height: 24px;
            font-weight: 500;
            display: none;
            position: absolute;
            background-color: inherit;
            pointer-events: none;
            top: 0;
            bottom: 0;
            left: 0;
            right: 0;
        }
        #input-container[disabled] ::slotted(.input-layer) {
            pointer-events: auto;
        }
        #input-container[has-layer][disabled] ::slotted(.input-layer),
        #input-container[has-layer]:not([focused]) ::slotted(.input-layer) {
            display: var(--tg-editor-default-input-layer-display, inherit);
        }
        #input-container ::slotted(.input-layer) {
            color: var(--paper-input-container-input-color, var(--primary-text-color));
        }
        #input-container[has-layer][disabled] ::slotted(.custom-input),
        #input-container[has-layer]:not([focused]) ::slotted(.custom-input) {
            opacity: 0;
        }
        .main-container ::slotted(.custom-icon-buttons), 
        .main-container ::slotted(.property-action) {
            padding-bottom: 1px;
        }
        paper-input-container::shadow .add-on-content {
            position: relative;
        }
        
        paper-input-container::shadow .label-and-input-container {
            min-width: 0px;
            @apply --tg-editor-label-and-input-container-mixin;
        }
        
        paper-input-container::shadow .input-content {
            @apply --tg-editor-input-content-mixin;
        }
        
        paper-input-container {
            
            --paper-input-container-input: {
                font-weight: 500;
            };
              
            @apply --tg-editor-paper-input-container-mixin;
        }
            
        paper-input-error {
            width: 100%;
            position: absolute;
        }
            
        paper-char-counter {
            position: absolute;
            right: 0;
            top: 0;
        }
        
        /* style requiredness */
        paper-input-container.required {
            --paper-input-container-color: #03A9F4;
            --paper-input-container-focus-color: #03A9F4;
        }
        
        paper-input-container.decorator-disabled.required::shadow :not(.is-invalid) .unfocused-line {
        	border-bottom: 1px dashed;
      		background: transparent;
      		opacity: 1;
        	border-color: #03A9F4;
        }
        
        /* style warning */
        paper-input-container.warning {
            --paper-input-container-invalid-color: #FFA000;
        }
        
        paper-input-container.decorator-disabled.warning::shadow .is-invalid .unfocused-line {
        	border-bottom: 1px dashed;
      		background: transparent;
      		opacity: 1;
        	border-color: #FFA000;
        }
        
        paper-input-container.decorator-disabled.warning::shadow .is-invalid .focused-line {
      		background: transparent !important;
        	border-color: transparent !important;
        }
        
        /* style error */
        paper-input-container.decorator-disabled:not(.required):not(.warning)::shadow .is-invalid .unfocused-line {
        	border-bottom: 1px dashed;
      		background: transparent;
      		opacity: 1;
        	border-color: var(--google-red-500);
        }
        
        paper-input-container.decorator-disabled:not(.required):not(.warning)::shadow .is-invalid .focused-line {
      		background: transparent !important;
        	border-color: transparent !important;
        }
        
        /* style not required, not warning and not error -- regular one */
        paper-input-container.decorator-disabled:not(.required):not(.warning)::shadow :not(.is-invalid) .unfocused-line {
        	border-bottom: 1px dashed;
      		background: transparent;
      		opacity: 1;
        	border-color: var(--secondary-text-color);
        }
        
        /* The next style chunk is applied on all 'add-on content', for e.g. char-counter, error message etc. */
        paper-input-container.decorator-disabled::shadow .add-on-content ::slotted() {
      		opacity: 1;
        }
    </style>
    <custom-style>
        <style include="iron-flex iron-flex-reverse iron-flex-alignment iron-flex-factors iron-positioning"></style>
    </custom-style>
    <paper-input-container id="decorator" always-float-label>
        <!-- flex auto  for textarea! -->
        <label style$="[[_calcLabelStyle(_editorKind, _disabled)]]" disabled$="[[_disabled]]">[[propTitle]]</label>
        <div class="main-container">
            <slot name="prefix-custom-attributes"></slot>
            <div id="input-container" class="relative" style$="[[_calcDecoratorPartStyle(_disabled)]]" has-layer$="[[_hasLayer]]" disabled$="[[_disabled]]" focused$="[[_focused]]">
                <slot name="custom-input"></slot>
                <slot id="layer_selector" name="input-layer"></slot>
            </div>
            <slot name="custom-icon-buttons"></slot>
            <slot name="property-action"></slot>
        </div>
        <!-- 'autoValidate' attribute for paper-input-container is 'false' -- all validation is performed manually and is bound to paper-input-error, which could be hidden in case of empty '_error' property -->
        <paper-input-error hidden$="[[!_error]]" disabled$="[[_disabled]]" tooltip-text$="[[_error]]">[[_error]]</paper-input-error>
        <!-- paper-input-char-counter addon is updated whenever 'bindValue' property of child '#input' element is changed -->
        <paper-input-char-counter id="inputCounter" class="footer" hidden$="[[!_isMultilineText(_editorKind)]]" disabled$="[[_disabled]]"></paper-input-char-counter>
    </paper-input-container>
    
    <tg-reflector id="reflector"></tg-reflector>

    <template is="dom-if" if="[[debug]]">
        <p>_editingValue: <i>[[_editingValue]]</i>
        </p>
        <p>_commValue: <i>[[_commValue]]</i>
        </p>
        <p>_acceptedValue: <i>[[_acceptedValue]]</i>
        </p>
    </template>`;

template.setAttribute('strip-whitespace', '');

Polymer({
    _template: template,

    is: 'tg-editor',

    properties: {
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////// EXTERNAL PROPERTIES //////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        // These mandatory properties must be specified in attributes, when constructing <tg-*-editor>s.       //
        // No default values are allowed in this case.														   //
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        propTitle: String,
        _focused: Boolean,
        _disabled: {
            type: Boolean,
            observer: '_disabledChanged'
        },
        _hasLayer: Boolean,
        _editingValue: {
            type: String,
            notify: true
        },
        _error: String,
        _commValue: String,
        _acceptedValue: String,
        debug: Boolean,
        _editorKind: String
    },
    
    behaviors: [Polymer.TgBehaviors.TgTooltipBehavior],

    ready: function () {
        this._hasLayer = this.$.layer_selector.assignedNodes().length > 0;
    },
    
    attached: function () {
        this.async((function () {
            if (!this._editorKind) {
                this._editorKind = 'NOT_MULTILINETEXT_OR_BOOLEAN';
            }
        }).bind(this), 1);
    },
    
    isInWarning: function () {
        return this.$.decorator.classList.contains("warning");
    },
    
    _isMultilineText: function (editorKind) {
        return 'MULTILINE_TEXT' === editorKind;
    },
    
    /**
     * Calculates the style for container's label.
     */
    _calcLabelStyle: function (editorKind, _disabled) {
        var style = "";
        if ("BOOLEAN" === editorKind) {
            style += "visibility: hidden;"
        }
        if (_disabled === true) {
            style += "opacity: 1;"
        }
        return style;
    },
    
    /**
     * Calculates the style for decorator inner parts, based on '_disabled' property.
     */
    _calcDecoratorPartStyle: function (_disabled) {
        var style = "min-width: 0px;";
        if (_disabled === true) {
            style += "opacity: 1;"
        }
        return style;
    },
    
    /**
     * The observer to the '_disabled' property, which maintains appropriately the class list of the decorator (regarding the class 'decorator-disabled').
     */
    _disabledChanged: function (newValue, oldValue) {
        if (newValue === true) {
            this.$.decorator.classList.add("decorator-disabled");
        } else {
            this.$.decorator.classList.remove("decorator-disabled");
        }
        this.updateStyles();
    }
});