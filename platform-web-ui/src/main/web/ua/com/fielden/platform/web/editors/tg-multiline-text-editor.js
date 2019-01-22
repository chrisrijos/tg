import '/resources/polymer/@polymer/polymer/polymer-legacy.js';
import '/resources/polymer/@polymer/iron-flex-layout/iron-flex-layout.js';
import '/resources/polymer/@polymer/iron-autogrow-textarea/iron-autogrow-textarea.js';

import '/resources/editors/tg-editor.js';

import { Polymer } from '/resources/polymer/@polymer/polymer/lib/legacy/polymer-fn.js';
import { html } from '/resources/polymer/@polymer/polymer/lib/utils/html-tag.js';

import { TgEditorBehavior } from '/resources/editors/tg-editor-behavior.js';

const template = html`
    <style>
        iron-autogrow-textarea {
            @apply --layout-flex;
            min-height: fit-content;
            overflow: hidden;
        }
        .upper-case {
            --iron-autogrow-textarea: {
                text-transform: uppercase;
            };
        }
        :host{
            /*min-height: fit-content;*/
            @apply --layout-vertical;
        }
        tg-editor {
            @apply --layout-vertical;
            @apply --layout-flex-auto;
            --tg-editor-paper-input-container-mixin: {
                @apply --layout-vertical;
                flex: 1 0 auto;
            };
            --tg-editor-input-container-disabled-mixin: {
                cursor: text;
            };
            --tg-editor-input-content-mixin: {
                @apply --layout-flex-auto;
                @apply --layout-vertical;
                @apply --layout-start;
            };
            --tg-editor-label-and-input-container-mixin: {
                @apply --layout-horizontal;
            };
            --tg-editor-main-container-mixin: {
                @apply --layout-start ;
                @apply --layout-flex;
            };
            --tg-editor-input-container-mixin: {
                @apply --layout-flex-auto;
                @apply --layout-vertical;
                @apply --layout-self-stretch;
                overflow: auto;
            };
        }
    </style>
    <tg-editor
        id="editorDom"
        prop-title="[[propTitle]]"
        _disabled="[[_disabled]]"
        _editing-value="{{_editingValue}}"
        action="[[action]]"
        _error="[[_error]]"
        _comm-value="[[_commValue]]"
        _accepted-value="[[_acceptedValue]]"
        debug="[[debug]]"
        tooltip-text$="[[_getTooltip(_editingValue)]]">
        <iron-autogrow-textarea
            id="input"
            class="paper-input-input custom-input multiline-text-input"
            max-rows="[[maxRows]]"
            bind-value="{{_editingValue}}"
            max-length="[[maxLength]]"
            on-change="_onChange"
            on-input="_onInput"
            on-tap="_onTap"
            on-mousedown="_onTap"
            on-keydown="_onKeydown"
            disabled$="[[_disabled]]"
            tooltip-text$="[[_getTooltip(_editingValue)]]">
        </iron-autogrow-textarea>
        <slot name="property-action"></slot>
    </tg-editor>`;

Polymer({
    _template: template,

    is: 'tg-multiline-text-editor',

    behaviors: [ TgEditorBehavior ],

    ready: function () {
        this.$.editorDom._editorKind = "MULTILINE_TEXT";

        // this.decorator().querySelector('#inputCounter').target = this.decorator().$.input;
    },

    properties: {
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////// EXTERNAL PROPERTIES //////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        // These mandatory properties must be specified in attributes, when constructing <tg-*-editor>s.       //
        // No default values are allowed in this case.														   //
        /////////////////////////////////////////////////////////////////////////////////////////////////////////

        /**
         * The maximum number of characters for this text editor
         */
        maxLength: {
            type: Number
        },

        /**
         * The maximum count for textarea rows.
         */
        maxRows: {
            type: Number,
            value: 5
        },

        _onTap: {
            type: Function,
            value: function () {
                return (function (event) {
                    if (document.activeElement !== this.decoratedInput().textarea) {
                        this.decoratedInput().textarea.select();
                        this._preventEventBubbling(event);
                    }
                }).bind(this);
            }
        },

        /**
         * OVERRIDDEN FROM TgEditorBehavior: this specific textArea's event is invoked after some key has been pressed.
         *
         * Designated to be bound to child elements.
         */
        _onKeydown: {
            type: Function,
            value: function () {
                return (function (event) {
                // need to invoke base function-property? Just do it like this:
                //   var parentFunction = Polymer.TgBehaviors.TgEditorBehavior.properties._onKeydown.value.call(this);
                //   parentFunction.call(this, event);
                //console.log("_onKeydown (for text area):", event);
                    // TODO potentially, commit on CTRL+Enter?
                }).bind(this);
            }
        }
    },

    /**
     * Converts the value into string representation (which is used in edititing / comm values).
     */
    convertToString: function (value) {
        return value === null ? "" : "" + value;
    },

    /**
     * Converts the value from string representation (which is used in edititing / comm values) into concrete type of this editor component (String).
     */
    convertFromString: function (strValue) {
        return strValue === '' ? null : strValue;
    }
});