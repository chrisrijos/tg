import '/resources/polymer/@polymer/polymer/polymer-legacy.js';
import { Polymer } from '/resources/polymer/@polymer/polymer/lib/legacy/polymer-fn.js';
import { html } from '/resources/polymer/@polymer/polymer/lib/utils/html-tag.js';

import '/resources/polymer/@polymer/iron-ajax/iron-ajax.js';
import { TgSerialiser } from '/resources/serialisation/tg-serialiser.js';
import { TgReflector } from '/app/tg-reflector.js';
import { _timeZoneHeader } from '/resources/reflection/tg-date-utils.js';

const template = html`
    <iron-ajax id="ajaxSender" headers="[[_headers]]" url="[[_url]]" method="POST" handle-as="json" on-response="_processValidatorResponse" on-error="_processValidatorError"></iron-ajax>
`;

Polymer({
    _template: template,

    is: 'tg-criteria-validator',

    properties: {
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////// EXTERNAL PROPERTIES //////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        // These mandatory properties must be specified in attributes, when constructing descendant elements.  //
        // No default values are allowed in this case.														   //
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        miType: String,
        saveAsName: String,
        postValidatedDefault: Function,
        postValidatedDefaultError: Function,
        processResponse: Function,
        processError: Function,

        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////// INNER PROPERTIES, THAT GOVERN CHILDREN /////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        // These properties derive from other properties and are considered as 'private' -- need to have '_'   //
        //   prefix. 																				           //
        // Also, these properties are designed to be bound to children element properties -- it is necessary to//
        //   populate their default values in ready callback (to have these values populated in children)!     //
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        _url: {
            type: String,
            computed: '_computeUrl(miType, saveAsName)'
        },
        
        /**
         * Additional headers for every 'iron-ajax' client-side requests. These only contain 
         * our custom 'Time-Zone' header that indicates real time-zone for the client application.
         * The time-zone then is to be assigned to threadlocal 'IDates.timeZone' to be able
         * to compute 'Now' moment properly.
         */
        _headers: {
            type: String,
            value: _timeZoneHeader
        }
    },

    created: function () {
        this._reflector = new TgReflector();
        this._serialiser = new TgSerialiser();
    },

    ready: function () {
        const self = this;

        self._processValidatorResponse = function (e) {
            self.processResponse(e, "criteria-validate", function (entityAndCustomObject) {
                self.postValidatedDefault(entityAndCustomObject);
            });
        };

        self._processValidatorError = function (e) {
            self.processError(e, "criteria-validate", function (errorResult) {
                self.postValidatedDefaultError(errorResult);
            });
        };
    },

    /**
     * Starts the process of entity validation.
     *
     * @param modifiedPropertiesHolder -- the entity with modified properties
     */
    validate: function (modifiedPropertiesHolder) {
        // console.log("validate: modifiedPropertiesHolder", modifiedPropertiesHolder);
        const ser = this._serialiser.serialise(modifiedPropertiesHolder);
        // console.log("validate: serialised modifiedPropertiesHolder", ser);
        this.$.ajaxSender.body = JSON.stringify(ser);
        return this.$.ajaxSender.generateRequest().completes;
    },

    /**
     * Cancels any unfinished validation that was requested earlier (if any).
     */
    abortValidationIfAny: function () {
        const numberOfAbortedRequests = this._reflector.discardAllRequests(this.$.ajaxSender);
        if (numberOfAbortedRequests > 0) {
            console.warn("abortValidationIfAny: number of aborted requests =", numberOfAbortedRequests);
        }
    },


    /**
     * Cancels any unfinished validation that was requested earlier (if any) except the last one and returns corresponding promise.
     */
    abortValidationExceptLastOne: function () {
        const numberOfAbortedRequests = this._reflector.discardAllRequests(this.$.ajaxSender, true);
        if (numberOfAbortedRequests > 0) {
            console.warn("abortValidationExceptLastOne: number of aborted requests =", numberOfAbortedRequests);
        }
        if (this.$.ajaxSender.activeRequests.length > 0) {
            if (this.$.ajaxSender.activeRequests.length > 1) {
                throw 'At this stage only one validation request should exist.';
            }
            return this.$.ajaxSender.activeRequests[0].completes;
        } else {
            if (numberOfAbortedRequests > 0) {
                throw 'There were aborted requests, however the last one was needed to be NOT ABORTED, but it was.';
            }
            return null;
        }
    },

    /**
     * Computes URL for 'ajaxSender'.
     */
    _computeUrl: function (miType, saveAsName) {
        return '/criteria/' + this._reflector._centreKey(miType, saveAsName);
    }
});