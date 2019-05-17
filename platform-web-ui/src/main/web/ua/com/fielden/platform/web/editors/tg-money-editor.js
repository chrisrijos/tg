import '/resources/polymer/@polymer/polymer/polymer-legacy.js';
import '/resources/polymer/@polymer/iron-input/iron-input.js';

import '/app/tg-app-config.js';

import { Polymer } from '/resources/polymer/@polymer/polymer/lib/legacy/polymer-fn.js';
import { html } from '/resources/polymer/@polymer/polymer/lib/utils/html-tag.js';

import { TgEditorBehavior,  createEditorTemplate} from '/resources/editors/tg-editor-behavior.js';
import { truncateInsignificantZeros } from '/resources/reflection/tg-numeric-utils.js';

const additionalTemplate = html`
    <style>
        /* Styles for integer and decimal property editors. */
        input[type=number]::-webkit-outer-spin-button,
        input[type=number]::-webkit-inner-spin-button {
            -webkit-appearance: none;
            margin: 0;
        }
        input[type=number] {
            -moz-appearance: textfield;
        }
        .input-layer {
            cursor: text;
            text-align: right;
            text-overflow: ellipsis;
            white-space: nowrap;
            overflow: hidden;
        }
    </style>
    <tg-app-config id="appConfig"></tg-app-config>`;
const customInputTemplate = html`
    <iron-input bind-value="{{_editingValue}}" class="custom-input-wrapper money-input">
        <input
            id="input"
            class="custom-input"
            type="number"
            step="any"
            on-change="_onChange"
            on-input="_onInput"
            on-keydown="_onKeydown"
            on-tap="_onTap"
            on-mousedown="_onTap"
            on-focus="_onFocus"
            on-blur="_outFocus"
            disabled$="[[_disabled}}"
            tooltip-text$="[[_getTooltip(_editingValue)]]"
            autocomplete="off"/>
    </iron-input>`;
const inputLayerTemplate = html`<div class="input-layer" tooltip-text$="[[_getTooltip(_editingValue)]]">[[_formatText(_editingValue)]]</div>`;
const propertyActionTemplate = html`<slot name="property-action"></slot>`;

Polymer({
    _template: createEditorTemplate(additionalTemplate, html``, customInputTemplate, inputLayerTemplate, html``, propertyActionTemplate),

    is: 'tg-money-editor',

    behaviors: [ TgEditorBehavior ],

    created: function () {
        this._hasLayer = true;
    },
    
   /**
     * Converts the value into string representation (which is used in editing / comm values).
     */
    convertToString: function (value) {
        return value === null ? "" : "" + value.amount;
    },
    
    /**
     * Converts the value from string representation (which is used in editing / comm values) into concrete type of this editor component (Number).
     */
    convertFromString: function (strValue) {
        if (strValue === '') {
            return null;
        }
        // var convertedNumber = (+strValue);
        if (isNaN(strValue)) {
            throw "The entered amount is not a valid number.";
        }
        
        // TODO currency and tax are ignored at this stage, but their support should most likely be implemented at some
        //      there is a need to have a better more general understanding of the role for currency and tax at the platfrom level
        var amount = (+strValue) 
        return {'amount': amount};
    },
    
    _formatText: function(valueToFormat) {
        var value = this.convertFromString(valueToFormat);
        if (value !== null) {
            const metaProp = this.reflector().getEntityTypeProp(this.reflector()._getValueFor(this.entity, ''), this.propertyName);
            return this.reflector().formatMoney(value, this.$.appConfig.locale, metaProp && metaProp.scale(), metaProp && metaProp.trailingZeros());
        }
        return '';
    },
    
    /**
     * Overridden to provide value corrections.
     */
    _commitForDescendants: function () {
        const correctedValue = truncateInsignificantZeros(this._editingValue);
        if (!this.reflector().equalsEx(correctedValue, this._editingValue)) {
            this._editingValue = correctedValue;
        }
    }
});