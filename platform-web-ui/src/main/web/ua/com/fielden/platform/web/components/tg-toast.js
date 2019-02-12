import '/resources/polymer/@polymer/iron-flex-layout/iron-flex-layout-classes.js'; // FIXME not needed?
import '/resources/polymer/@polymer/paper-styles/color.js';
import '/resources/polymer/@polymer/paper-button/paper-button.js';
import '/resources/polymer/@polymer/paper-toast/paper-toast.js'; // FIXME does not exist yet
import '/resources/polymer/@polymer/paper-spinner/paper-spinner.js';
import '/resources/polymer/@polymer/polymer/lib/elements/dom-bind.js';

import { tearDownEvent } from '/resources/reflection/tg-polymer-utils.js';

import { Polymer } from '/resources/polymer/@polymer/polymer/lib/legacy/polymer-fn.js';
import { html } from '/resources/polymer/@polymer/polymer/lib/utils/html-tag.js';

const template = html`
    <!--custom-style>
        <style include="iron-flex iron-flex-reverse iron-flex-alignment iron-flex-factors iron-positioning"></style>
    </custom-style-->
    <paper-toast id="toast" class="paper-toast" text="[[_text]]" on-tap="_showMoreIfPossible" always-on-top duration="0">
        <!-- TODO responsive-width="250px" -->
        <paper-spinner id="spinner" hidden$="[[_skipShowProgress]]" active alt="in progress..." tabIndex="-1"></paper-spinner>
        <div id='btnMore' hidden$="[[_skipShowMore(_showProgress, _hasMore)]]" class="more" on-tap="_showMessageDlg">MORE</div>
    </paper-toast>
`;

const PROGRESS_DURATION = 3600000; // 1 hour
const CRITICAL_DURATION = 5000; // 5 seconds
const MORE_DURATION = 4000; // 4 seconds
const STANDARD_DURATION = 2000; // 2 seconds

function containsRestictedTags(htmlText) {
    const offensiveTag = new RegExp('<html|<body|<script|<img|<a', 'mi');
    return offensiveTag.exec(htmlText) !== null;
}

Polymer({
    // attributes="msgHeading -- TODO was this ever needed?"
    _template: template,

    is: 'tg-toast',

    properties: {
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////// EXTERNAL PROPERTIES //////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        // These mandatory properties must be specified in attributes, when constructing descendant elements.  //
        // No default values are allowed in this case.														   //
        /////////////////////////////////////////////////////////////////////////////////////////////////////////

        text: {
            type: String
        },

        msgText: {
            type: String,
            value: ''
        },

        showProgress: {
            type: Boolean
        },

        hasMore: {
            type: Boolean
        },

        isCritical: {
            type: Boolean,
            value: false
        },
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////// INNER PROPERTIES, THAT GOVERN CHILDREN /////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        // These properties derive from other properties and are considered as 'private' -- need to have '_'   //
        //   prefix. 																				           //
        // Also, these properties are designed to be bound to children element properties -- it is necessary to//
        //   populate their default values in ready callback (to have these values populated in children)!     //
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        _text: {
            type: String
        },

        _msgText: {
            type: String,
            value: ''
        },

        _showProgress: {
            type: Boolean
        },

        _hasMore: {
            type: Boolean,
            observer: '_hasMoreChanged'
        },

        _isCritical: {
            type: Boolean,
            value: false
        },

        _skipShowProgress: {
            type: Boolean,
            computed: '_shouldSkipProgress(_showProgress, _hasMore)'
        }
    },

    ready: function () {
        //Indicates whether toast overlay can be closed via history (back button or not)
        this.$.toast.skipHistoryAction = true;
    },

    _hasMoreChanged: function (newValue, oldValue) {
        // let's set a cursor for the whole toast if it can be "clicked"
        const cursor = newValue === true ? 'pointer' : 'inherit';
        this.$.toast.style.cursor = cursor;
    },

    _showMoreIfPossible: function (e) {
        if (this._hasMore) {
            this._showMessageDlg(e);
        }
    },

    _showMessageDlg: function (event) {
        const self = this;
        // need to open dialog asynchronously for it to open on mobile devices
        this.async(function () {
            // build and display the dialog
            const domBind = document.createElement('dom-bind');

            domBind._dialogClosed = function () {
                document.body.removeChild(this);
                // Polymer.dom.flush(); FIXME
            }.bind(domBind);

            const paperDialog = domBind.content.ownerDocument.createElement('paper-dialog'); // FIXME
            paperDialog.setAttribute("class", "toast-dialog");
            paperDialog.setAttribute("id", "msgDialog");
            paperDialog.setAttribute("on-iron-overlay-closed", "_dialogClosed");
            paperDialog.setAttribute("with-backdrop", "true");
            paperDialog.setAttribute("entry-animation", "scale-up-animation");
            paperDialog.setAttribute("exit-animation", "fade-out-animation");

            const paperDialogScrollable = domBind.content.ownerDocument.createElement('paper-dialog-scrollable'); // FIXME
            const msgPar = domBind.content.ownerDocument.createElement('p'); // FIXME
            msgPar.setAttribute("style", "padding: 10px");
            if (containsRestictedTags(this._msgText) === true) {
                msgPar.textContent = this._msgText;
            } else {
                msgPar.innerHTML = this._msgText;
            }
            paperDialogScrollable.appendChild(msgPar);

            const buttonsDiv = domBind.content.ownerDocument.createElement('div'); // FIXME
            buttonsDiv.setAttribute("class", "buttons");

            const buttons = domBind.content.ownerDocument.createElement('paper-button'); // FIXME
            buttons.setAttribute("dialog-confirm", "dialog-confirm");
            buttons.setAttribute("affirmative", "affirmative");
            buttons.setAttribute("autofocus", "autofocus");

            const textSpan = domBind.content.ownerDocument.createElement('span'); // FIXME
            textSpan.textContent = 'Close';
            buttons.appendChild(textSpan);

            buttonsDiv.appendChild(buttons);

            paperDialog.appendChild(paperDialogScrollable);
            paperDialog.appendChild(buttonsDiv);

            domBind.content.appendChild(paperDialog); // FIXME
            document.body.appendChild(domBind);
            // FIXME Polymer.dom.flush();

            this.async(function () {
                domBind.$.msgDialog.open();
            }, 100);

            self.$.toast.close();
        }, 100);

        tearDownEvent(event);
    },

    _shouldSkipProgress: function (progress, hasMore) {
        return !progress || hasMore;
    },

    _skipShowMore: function (progress, hasMore) {
        return progress || !hasMore;
    },

    _getPreviousToast() {
        const toasts = document.querySelectorAll('#toast');
        let toast = null;
        let existingToastCount = 0;
        for (let index = 0; index < toasts.length; index++) {
            const currToast = toasts.item(index);
            if (currToast.parentNode === document.body) {
                existingToastCount++;
                if (existingToastCount > 1) {
                    throw 'More than one toast exist in body direct children.';
                }
                toast = currToast;
            }
        }
        return toast;
    },

    show: function () {
        const previousToast = this._getPreviousToast();
        if (!previousToast) {
            // initial values
            this.$.toast.error = false;
            this.$.toast._autoCloseCallBack = null; // must NOT interfere with _autoClose of paper-toast
            // Override refit function for paper-toast which behaves really weird (Maybe next releas of paper-toast iron-fit-behavior and iron-overlay-behavior will change this weird behaviour).
            this.$.toast.refit = function () { };
            Polymer.dom(document.body).appendChild(this.$.toast);
            Polymer.dom().flush();
            this._showNewToast();
        } else if (previousToast.error === true && previousToast.opened && this.isCritical === false) { // discard new toast if existing toast is critical and new one is not; however if new one is critical -- do not discard it -- show overridden information
            console.warn('    toast show: DISCARDED: text = ', this.text + ', critical = ' + this.isCritical);
        } else {
            // 'dataHost' is used to detemine 'tg-toast' instance from 'previousToast' found on body (parent of 'previousToast' is body, that is why there is a need to use other accessing method).
            // WARNING: 'dataHost' is not a public Polymer API.
            const previousTgToast = previousToast.dataHost; // FIXME

            if (previousTgToast !== this) {
                previousTgToast.text = this.text;
                previousTgToast.msgText = this.msgText;
                previousTgToast.showProgress = this.showProgress;
                previousTgToast.hasMore = this.hasMore;
                previousTgToast.isCritical = this.isCritical;
            }
            previousTgToast._showNewToast();
        }
    },

    _showNewToast: function () {
        this._text = this.text;
        this._msgText = this.msgText;
        this._showProgress = this.showProgress;
        this._hasMore = this.hasMore;
        this._isCritical = this.isCritical;

        let customDuration;
        if (this._showProgress && !this._hasMore) {
            customDuration = PROGRESS_DURATION;
        } else if (this._isCritical === true) {
            customDuration = CRITICAL_DURATION;
        } else if (this._hasMore === true) {
            customDuration = MORE_DURATION;
        } else {
            customDuration = STANDARD_DURATION;
        }
        if (this._isCritical === true || this.$.toast.error === false) { // if critical toast arrived then delay its closing; also delay closing if toast is not critical but old toast is not critical too
            if (this.$.toast._autoCloseCallBack !== null) {
                this.$.toast.cancelAsync(this.$.toast._autoCloseCallBack);
                this.$.toast._autoCloseCallBack = null;
            }
            this.$.toast._autoCloseCallBack = this.$.toast.async(this._closeToast.bind(this), customDuration);
        }

        this.$.toast.error = this._isCritical;
        if (this._isCritical === true) {
            this.$.toast.style.background = '#D50000';
            this.$.btnMore.style.color = '#FFCDD2';
        } else {
            this.$.toast.style.background = 'black';
            this.$.btnMore.style.color = '#03A9F4';
        }
        this.$.toast.show();
    },

    _closeToast: function () {
        this.$.toast.close();
        this.$.toast._autoCloseCallBack = null;
    }
});