import '/resources/polymer/@polymer/polymer/polymer-legacy.js';
import '/resources/polymer/@polymer/iron-icons/iron-icons.js';
import '/resources/polymer/@polymer/iron-input/iron-input.js';
import '/resources/polymer/@polymer/iron-ajax/iron-ajax.js';

import '/resources/polymer/@polymer/paper-button/paper-button.js';
import '/resources/polymer/@polymer/paper-icon-button/paper-icon-button.js';
import '/resources/polymer/@polymer/paper-spinner/paper-spinner.js';
import '/resources/polymer/@polymer/paper-styles/color.js';

import '/resources/editors/tg-entity-editor-result.js'
import '/resources/serialisation/tg-serialiser.js'

import { Polymer } from '/resources/polymer/@polymer/polymer/lib/legacy/polymer-fn.js';
import { html } from '/resources/polymer/@polymer/polymer/lib/utils/html-tag.js';

import { TgEditorBehavior , createEditorTemplate} from '/resources/editors/tg-editor-behavior.js';
import { tearDownEvent } from '/resources/reflection/tg-polymer-utils.js'

const additionalTemplate = html`
    <style>
        #input.upper-case {
            text-transform: uppercase;
        }
        .input-layer {
            cursor: text;
            text-overflow: ellipsis;
            white-space: nowrap;
            overflow: hidden;
            flex-direction: row;
            align-items: center;
        }
        .search-button {
            display: flex;
            width: 24px;
            height: 24px;
            padding: 4px;
        }
        paper-spinner {
            width: 1.5em;
            height: 1.5em;
            min-width: 1em;
            min-height: 1em;
            max-width: 2em;
            max-height: 2em;
            padding: 2px;
            margin-left: 0;
            --paper-spinner-layer-1-color: var(--paper-blue-500);
            --paper-spinner-layer-2-color: var(--paper-blue-500);
            --paper-spinner-layer-3-color: var(--paper-blue-500);
            --paper-spinner-layer-4-color: var(--paper-blue-500);
        }
        tg-editor {
             --tg-editor-default-input-layer-display: flex;
        }
    </style>
    <tg-entity-editor-result id="result"
                             tabindex="-1"
                             no-auto-focus
                             on-iron-overlay-opened="_resultOpened"
                             on-iron-overlay-closed="_resultClosed"
                             on-iron-overlay-canceled="_resultCanceled"
                             retrieve-container-sizes="[[_retrieveContainerSizes]]"
                             on-tap="_entitySelected"
                             on-dblclick="_done"
                             on-keydown="_onKeydown"></tg-entity-editor-result>
    <iron-ajax id="ajaxSearcher" loading="{{searching}}" url="[[_url]]" method="POST" handle-as="json" on-response="_processSearcherResponse" on-error="_processSearcherError"></iron-ajax>
    <tg-serialiser id="serialiser"></tg-serialiser>`;
const customInputTemplate = html`
    <iron-input slot="input" bind-value="{{_editingValue}}" class="custom-input entity-input">
        <input
            id="input"
            type="text" 
            on-blur="_blurEventHandler" 
            on-change="_onChange" 
            on-input="_onInput" 
            on-keydown="_onKeydown" 
            on-tap="_onTap" 
            on-mousedown="_onTap" 
            on-focus="_onFocus" 
            disabled$="[[_disabled]]" 
            tooltip-text$="[[_getTooltip(_editingValue, entity, focused)]]"
            autocomplete="off"/>
    </iron-input>`;
const inputLayerTemplate = html`
    <div class="input-layer" slot="input" tooltip-text$="[[_getTooltip(_editingValue, entity, focused)]]">
        <template is="dom-repeat" items="[[_customPropTitle]]">
            <span hidden$="[[!item.title]]" style="color:#737373; font-size:0.8rem; padding-right:2px;"><span>[[item.title]]</span>:  </span>
            <span style$="[[_valueStyle(item, index)]]">[[item.value]]</span>
        </template>
        <span style="color:#737373" hidden$="[[!_hasDesc(entity)]]">&nbsp;&ndash;&nbsp;<i>[[_formatDesc(entity)]]</i></span>
    </div>`;
const customIconButtonsTemplate = html`
    <paper-icon-button id="searcherButton" slot="suffix" hidden$="[[searchingOrOpen]]" on-tap="_searchOnTap" icon="search" class="search-button custom-icon-buttons" tabIndex="-1" disabled$="[[_disabled]]" tooltip-text="Show search result"></paper-icon-button>
    <paper-icon-button slot="suffix" hidden$="[[searchingOrClosed]]" on-down="_done" icon="done" class="search-button custom-icon-buttons" tabIndex="-1" disabled$="[[_disabled]]" tooltip-text="Accept the selected entries"></paper-icon-button>
    <paper-spinner slot="suffix" active hidden$="[[!searching]]" class="custom-icon-buttons" tabIndex="-1" alt="searching..." disabled$="[[_disabled]]"></paper-spinner>`;
const propertyActionTemplate = html`<slot slot="suffix" name="property-action"></slot>`;

(function () {
    /* several helper functions for string manipulation */
    function escapeRegExp(str) {
        return str.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
    }

    function replaceAll(find, replace, str) {
        // 'g' is the flag for global match,
        // 'i' is the flag to ignore the case during matching
        return str.replace(new RegExp(escapeRegExp(find), 'g', 'i'), replace);
    }

    Polymer({
        _template: createEditorTemplate(additionalTemplate, html``, customInputTemplate, inputLayerTemplate, customIconButtonsTemplate, propertyActionTemplate),

        is: 'tg-entity-editor',

        behaviors: [TgEditorBehavior],

        /********************************************************
         ********************************************************
         *********************** PROPERTIES *********************
         ********************************************************
         ********************************************************/
        properties: {
            /* Indicates whether a search is in progress. This property controls visibility of the progress indecator.
             * It is bound to iron-ajax property loading, which basicaly controlls spinner visibility. */
            searching: {
                type: Boolean,
                value: false
            },

            _searchQuery: {
                type: String,
                value: ''
            },

            /*
             * A string with comma separated property names that shoould be displayed in addition to key.
             */
            additionalProperties: {
                type: String,
                value: ''
            },

            _asyncSearchHandle: {
                type: Object,
                value: null
            },

            /**
             * Property that indicated whether the result overlay is open or closed.
             */
            opened: {
                type: Boolean,
                value: false
            },

            searchingOrOpen: {
                type: Boolean,
                computed: '_computeSearchingOrOpened(searching, opened)'
            },

            searchingOrClosed: {
                type: Boolean,
                computed: '_computeSearchingOrClosed(searching, opened)'
            },

            /* Indicates whether multiple (true) or a single (false, default) value is acceptable. */
            multi: {
                type: Boolean,
                value: false
            },

            /**
             * The type that identifies the master (entity type) or centre (miType + saveAsName).
             */
            autocompletionType: {
                type: String
            },

            /**
             * Returns 'true' if this editor is a part of Entity Master, 'false' in case if it is a part of Entity Centre.
             *
             * Should not be null, should be initialised using generation logic.
             */
            asPartOfEntityMaster: {
                type: Boolean
            },

            /**
             * Default implementation for unsuccessful postSearched callback (external property from tg-entity-binder).
             */
            postSearchedDefaultError: {
                type: Function
            },

            /**
             * External utility function for processing responses (from tg-entity-binder).
             */
            processResponse: {
                type: Function
            },

            /**
             * External utility function for processing unsuccessful responses (from tg-entity-binder).
             */
            processError: {
                type: Function
            },

            /**
             * The function which creates 'modifiedPropertiesHolder' for the autocompletion context.
             */
            createModifiedPropertiesHolder: {
                type: Function
            },

            /**
             * In case if new entity is operated on, this instance holds an original fully-fledged contextually produced entity, otherwise 'null'.
             * It is updated everytime when refresh process successfully completes.
             */
            originallyProducedEntity: {
                type: Object
            },

            /**
             * Determines whether the selection criteria entity are required to be send inside the centre context.
             *
             * 'null' -- if not applicable, for e.g. this is a master's (not centre's) editor, or in Centre DSL end-app dev has not been marked 'selectionCrit' as relevant for context.
             */
            requireSelectionCriteria: {
                type: String
            },
            /**
             * Determines whether the selected entities are required to be send inside the centre context.
             *
             * 'null' -- if not applicable, for e.g. this is a master's (not centre's) editor, or in Centre DSL end-app dev has not been marked 'selectedEntities' as relevant for context.
             */
            requireSelectedEntities: {
                type: String
            },
            /**
             * Determines whether the master entity (main entity for dependent centre) are required to be send inside the centre context.
             *
             * 'null' -- if not applicable, for e.g. this is a master's (not centre's) editor, or in Centre DSL end-app dev has not been marked 'masterEntity' as relevant for context.
             */
            requireMasterEntity: {
                type: String
            },

            /**
             * The external function to be bound from tg-selection-criteria for retrieving 'selected entities as part of the context'.
             */
            getSelectedEntities: {
                type: Function
            },

            /**
             * The external function to be bound from tg-selection-criteria for retrieving 'master entity as part of the context'.
             */
            getMasterEntity: {
                type: Function
            },

            _replaceFromIndex: {
                type: Number,
                value: 0
            },

            _replaceToIndex: {
                type: Number,
                value: 0
            },

            /** A state to maintain information about the page number of the matching values to be retrieved. */
            _dataPage: {
                type: Number,
                value: 1
            },

            separator: {
                type: String,
                value: ","
            },

            _blurEventHandler: {
                type: Function
            },

            /**
             * OVERRIDDEN FROM TgEditorBehavior: this specific textArea's event is invoked after some key has been pressed.
             *
             * Designated to be bound to child elements.
             */
            _onInput: {
                type: Function,
                value: function () {
                    return (function (event) {
                        // clear any search request in already in progress
                        this._cancelSearch();
                        this._asyncSearchHandle = this.async(this._searchForOnInput, 700);
                    }).bind(this);
                }
            },

            /**
             * OVERRIDDEN FROM TgEditorBehavior: this specific <input> event is invoked after some key has been pressed.
             *
             * This keydown handler implements navigation over the list of matching values, selection of multiple values with Space (this is automatic) and acceptance of selected value with Enter.
             * This is also the keydown event handler for the resul list.
             */
            _onKeydown: {
                type: Function,
                value: function () {
                    return (function (event) {
                        if (event.keyCode === 13 && this.opened === true) { // 'Enter' has been pressed
                            this._done();
                        } else if ((event.keyCode === 38 /*up*/ || event.keyCode === 40 /*down*/) && !event.ctrlKey) { // up/down arrow keys
                            // By devault up/down arrow keys work like home/end for and input field
                            // That's why this event should be suppressed.
                            tearDownEvent(event);

                            // Let's now handle the up/down logic that should perform search result list navigation
                            if (event.keyCode === 38) {
                                this._selectPrevOnKeyUp(event);
                            } else if (event.keyCode === 40) {
                                this._selectNextOnKeyDown(event);
                            }

                            // return false as part of stopping the event from propagation
                            return false;
                        }
                    }).bind(this);
                }
            },

            _url: {
                type: String,
                computed: '_computeUrl(autocompletionType, propertyName)'
            },

            /**
             * OVERRIDDEN FROM TgEditorBehavior: this specific entityEditor's event was overridden to prevent commiting the value prematurely.
             */
            _onChange: {
                type: Function,
                value: function () {
                    return (function (event) {
                        console.log("_onChange (for entity editor):", event);

                        if (this.opened === false) {
                            var parentFunction = TgEditorBehavior.properties._onChange.value.call(this);
                            parentFunction.call(this, event);
                        }
                    }).bind(this);
                }
            },

            /**
             * The function that retrives boundClientRect and offsetHeight from wrapping decorator (paper-input-container).
             */
            _retrieveContainerSizes: {
                type: Function
            }
        },

        observers: ["_changeTitle(entity)"],

        created: function () {
            this._hasLayer = true;
        },

        ready: function () {
            const result = this.$.result;
            result.acceptValues = this._done.bind(this);
            result.loadMore = this._loadMore.bind(this);
            result.multi = this.multi;
            if (this.additionalProperties) {
                result.additionalProperties = JSON.parse(this.additionalProperties);
            }
            this._blurEventHandler = (function (e) {
                this._outFocus(e);
                // There is no need to proceed with search if user moved out of the search field
                this._cancelSearch();

                // check whether relatedTarget has anything to do with this.$.result
                // if it is then there is no need to cancel the overlay, which is this.$.result
                let parent = e.relatedTarget;
                while (parent !== null && parent !== this.$.result) {
                    parent = parent.parentElement;
                }
                if (this.$.result.opened && parent !== this.$.result) {
                    this.$.result.cancel(e);
                }
            }).bind(this);

            this._retrieveContainerSizes = function () {
                const container = this.decorator();
                return [container.getBoundingClientRect(), container.offsetHeight];
            }.bind(this);
        },

        /**
         * Computes URL for 'ajaxSearcher'.
         */
        _computeUrl: function (autocompletionType, propertyName) {
            return "/autocompletion/" + autocompletionType + "/" + propertyName;
        },

        _computeSearchingOrOpened: function (searching, opened) {
            return searching === true || opened == true;
        },

        _computeSearchingOrClosed: function (searching, opened) {
            return searching === true || opened == false;
        },

        /**
         * Cleans input text.
         */
        _prepInput: function (str) {
            if (str) {
                return str.replace(/\*\*/g, "*");
            }
            return str;
        },

        /* Invokes _search with '*' as the default search value, so than when nothing was typed, but
         * the search button has been pressed then the search happens as if wildcard has been typed. */
        _searchOnTap: function (e) {
            // need to execute the tap action on async to ensure committing of any uncommitted
            // values in other property editors that might influence the matching logic at the server side
            this.async((function () {
                this._search('*');
            }).bind(this), 10);

        },

        /* Invokes _search with an emply string as the default search value, which ensures what no search would happen
         * if nothing was typed into the input field. */
        _searchForOnInput: function () {
            this._search('');
        },

        /** Loads more matching values. */
        _loadMore: function () {
            if (!this.searching) {
                this._dataPage = this._dataPage + 1;
                this._search(this._searchQuery, this._dataPage);
            }
        },

        _search: function (defaultSearchQuery, dataPage) {
            // What is the query string?
            let inputText = ''; // default value
            if (this.multi === false) {
                // assign the actual search string
                inputText = this._prepInput(this.decoratedInput().value) || defaultSearchQuery;
            } else {
                // The following manipulations with indexes are required in case of multi selection
                // in order to detremine what part of the input text should be used for search and
                // also for later insertion of selected values (this._replaceFromIndex and this._replaceToIndex govern this).

                const text = this.decoratedInput().value;
                const caretPos = this.decoratedInput().selectionStart;
                const toIndex = text.indexOf(this.separator, caretPos) < 0 ? text.length : text.indexOf(this.separator, caretPos);
                const startOfText = text.substring(0, caretPos);
                const fromIndex = startOfText.lastIndexOf(this.separator) < 0? -1 : startOfText.lastIndexOf(this.separator); // just to make sure that it is -1

                this._replaceFromIndex = fromIndex;
                this._replaceToIndex = toIndex;

                // assign the actual search string
                inputText = this._prepInput(text.substring(fromIndex + 1, toIndex).trim()) || defaultSearchQuery;
            }

            // prep this.searchQuery for highlighting of the matching parts in the search result
            if (!inputText) {
                this._searchQuery = "";
            } else {
                this._searchQuery = replaceAll('*', '%', inputText.toUpperCase());
            }

            // collect new matching values
            const self = this;
            const result = self.$.result;
            result.searchQuery = self._searchQuery;
            const container = self.$.container;

            if (self._searchQuery /*&& this.hasFocus === true*/ ) {
                // if this is not a request to load more data then let's clear the current result, if any
                if (!dataPage) {
                    result.clearSelection();
                }
                // prepare the AJAX request based on the raw search string
                const serialisedSearchQuery = self.$.serialiser.serialise(self.createContextHolder(self._searchQuery, dataPage));
                self.$.ajaxSearcher.body = JSON.stringify(serialisedSearchQuery);
                self.$.ajaxSearcher.generateRequest();
            } else if (result.opened) { // make sure overlay is closed if no search is performed
                result.close();
                this._focusInput();
            }
        },

        /*
         * Displays the search result.
         */
        _onFound: function (entities) {
            const result = this.$.result;
            // make sure to assign reflector to the result object
            result.reflector = this.reflector();

            let wasNewValueObserved = false;
            let indexOfFirstNewValue = -1;
            for (let index = 0; index < entities.length; index++) {
                // Entity is converted to a string representation of its key.
                // This includes correct conversion of simple, composite and union entities
                const key = this.reflector().convert(entities[index]);
                entities[index].key = key;
                const isNew = result.pushValue(entities[index]);
                // if a new value was observed for the first time then capture its index
                // so that later this new item could be focused
                if (!wasNewValueObserved && isNew) {
                    indexOfFirstNewValue = index;
                }
                wasNewValueObserved = isNew || wasNewValueObserved;
            }

            // if no new values were observed then there is no more to load
            // let's disable the load more action in this case
            result.enableLoadMore = wasNewValueObserved;

            // There is a need to check whether element already exists before appending it to document.body.
            // Under Microsoft Edge appending the same element more than once blows up with exception HierarchyRequestError.
            const elementExists = document.body.querySelector("#result");
            if (!elementExists) {
                document.body.appendChild(this.$.result);
            }

            this.async(function () {
                if (result.opened) {
                    this._resultOpened();
                } else {
                    if (result.visibleHeightUnderEditorIsSmall()) {
                        this.scrollIntoView({block: "center", inline: "center", behavior: "smooth"}); // Safari (WebKit) does not support options object (smooth scrolling). We are aiming Chrome for iOS devices at this stage.
                        this.async(function () { // need to wait at least 300 ms for scrolling to complete..
                            result.open();
                        }.bind(this), 300);
                    } else {
                        result.open();
                    }
                }
                result.notifyResize();

                // focus a new item, if any
                // this should happen only if new values were loaded after pressing MORE
                if (document.activeElement == result.loadMoreButton() && indexOfFirstNewValue >= 0) {
                    result.focusItemWithIndex(indexOfFirstNewValue);
                }
            }.bind(this), 100);
        },

        /**
         * Create context holder with custom '@@searchString' property ('tg-entity-editor' and 'tg-entity-search-criteria' only).
         */
        createContextHolder: function (inputText, dataPage) {
            let contextHolder = null;
            if (this.multi === false && this.asPartOfEntityMaster) {
                const modifHolder = this.createModifiedPropertiesHolder();
                const originallyProducedEntity = this.reflector()._validateOriginallyProducedEntity(this.originallyProducedEntity, modifHolder.id);
                contextHolder = this.reflector().createContextHolder(
                    "true", null, null,
                    function () { return modifHolder; }, null, null,
                    originallyProducedEntity
                );
                this.reflector().setCustomProperty(contextHolder, "@@searchString", inputText);
            } else {
                contextHolder = TgEditorBehavior.createContextHolder.call(this, inputText);
            }

            this.reflector().setCustomProperty(contextHolder, "@@dataPage", dataPage);
            return contextHolder;
        },

        _cancelSearch: function () {
            if (this._asyncSearchHandle) {
                this.cancelAsync(this._asyncSearchHandle);
                this._asyncSearchHandle = null;
            }
        },

        _resultOpened: function (e) {
            const activeElement = document.activeElement;
            if (this.$.searcherButton === activeElement ||     /* if autocompleter's button is in focus (this occurs in iOs when tapping on that button) */
                this.$.input === activeElement          ||     /* or if autocompleter's input is in focus */
                document.body === activeElement         ||     /* or no other input or button in focus then show found values */
                "loadMoreButton" === activeElement.id   ||     /* or was the loadMoreButton tapped or */
                activeElement.classList.contains("tg-item")) { /* or if a list item is an active element */  
                this.opened = true;
                this.$.result.highlightMatchedParts();
            } else {
                this.opened = true;
                this.$.result.cancel(e);
            }
        },

        _resultClosed: function (e) {
            // property this.opened controls whether overlay on-close event should
            // perfrom on-change event handler that does all the validation megic
            this._dataPage = 1;
            if (this.opened === true) {
                this.opened = false;
                this._onChange();
            }
            const elementExists = document.body.querySelector("#result");
            if (elementExists) {
                document.body.removeChild(this.$.result);
            }
        },

        _resultCanceled : function (event, detail) {
            this._dataPage = 1;
            if (detail.keyCode && detail.keyCode === 27) {
                this._focusInput();
            }
        },

        _entitySelected: function () {
            // if this this is non-multi mode and the tap happened on a result item then it should be selected
            if (!this.multi && document.activeElement.classList.contains("tg-item")) {
                this._done();
            }
        },

        /* This method handles an explicit user action for accepting selected values from an autocompleted list.
         * However, there is no guarantee that there are actually selected values.
         */
        _done: function () {
            console.log("_done:");
            const input = this.decoratedInput();
            const result = this.$.result;

            // let's make sure that at least one matching value is selected if _done()
            // this is mainly relevant for the case of multi autocompleter that has been focused, no values selected, but Enter/Accept is pressed
            result.selectCurrentIfNoneSelected();
            const hasValuesToProcess = Object.keys(result.selectedValues).length > 0;

            // should close automatcially, but just in case let's make sure the result overlay gets closed
            this.opened = false;
            result.close();

            // value accpetance logic...
            if (hasValuesToProcess) {
                // compose a string value, which would be a comma separated string in case of multi
                const selectedValuesAsStr = _.map(result.selectedValues, function (obj) {
                    return obj.key; // 'key' field contains converted representation of the entity
                }).join(this.separator);

                if (!this.multi) {
                    // if this is a single selection config then need to simply assign the value
                    this._editingValue = selectedValuesAsStr;
                } else {
                    // in case of multi selection config things get a little more interesting
                    // as we need to insert the value into the right position of an existing text in the input field
                    const before = this._editingValue.substring(0, this._replaceFromIndex + 1);
                    const after = this._editingValue.substr(this._replaceToIndex);
                    const newEditingValue = before + selectedValuesAsStr + after;

                    this._editingValue = newEditingValue;

                    // let's highlight the inseted values
                    input.selectionStart = this._replaceFromIndex;
                    input.selectionEnd = input.selectionStart + selectedValuesAsStr.length + 1;

                }
            }

            result.clearSelection();
            // The input value could have been changed manually or as a result of selection (the above logic).
            // Therefore, need to fire the change event.
            this._onChange();

            // at the end let's focus the input...
            this._focusInput();
        },

        /* This method is used to make sure the input is in focus */
        _focusInput: function () {
            // at the end let's focus the input...
            const input = this.decoratedInput();
            this.async(function () {
                input.focus();
            }, 100);
        },

        get parent() {
            if (this.parentNode.nodeType === Node.DOCUMENT_FRAGMENT_NODE) {
                return this.parentNode.host;
            }
            return this.parentNode;
        },

        /**
         * The bound to this editor property of the bound entity should be of String type (if not multi) or of String array type (if multi).
         *
         * Therefore, it is expected that the passed in value is either a null or a String (if not multi)
         * or an empty array [] or an array of Strings (if multi).
         */
        convertToString: function (value) {
            if (this.multi === true) {
                return value.join(this.separator); // for empty array it will return "". 'value' should never be 'null'!
            } else {
                return value === null ? "" : "" + value;
            }
        },

        /**
         * The bound to this editor property of the bound entity should be of String type (if not multi) or of String array type (if multi).
         *
         * Multi: the string value from the editor should be split by separator and the resulting Strig array returned.
         *
         * Single: the string value from the editor should either be taken as is if it is not empty,
         * or converted to null due to the fact that there should be no empty string representing an entity key.
         */
        convertFromString: function (strValue) {
            if (this.multi === true) {
                if (strValue === '') {
                    return []; // missing value for multi autocompliter is empty array []!
                } else {
                    var arr = strValue.split(this.separator);
                    return arr;
                }
            } else {
                return strValue === '' ? null : strValue;
            }
        },

        _processSearcherResponse: function (e) {
            const self = this;
            self.processResponse(e, "search", function (foundEntities) {
                self._onFound(foundEntities);
            });
        },

        _processSearcherError: function (e) {
            const self = this;
            self.opened = true;
            self.$.result.close();
            self.processError(e, "search", function (errorResult) {
                   if (self.postSearchedDefaultError) {
                    self.postSearchedDefaultError(errorResult);
                   }
            });
        },

        _selectNextOnKeyDown: function (e) {
            if (this.$.result.opened) {
                console.log('select next');
                this.$.result.selectNext();
            } else {
                this._searchOnTap();
            }
        },

        _selectPrevOnKeyUp: function (e) {
            if (this.$.result.opened) {
                console.log('select prev');
                this.$.result.selectPrev();
            }
        },

        _getTooltip: function (_editingValue, entity, focused) {
            var valueToFormat, fullEntity;
            if (!focused && entity !== null) {
                fullEntity = this.reflector()._getValueFor(entity, "");
                if (this.reflector().isError(fullEntity.prop(this.propertyName).validationResult())) {
                    valueToFormat = _editingValue;
                } else {
                    valueToFormat = this.reflector()._getValueFor(entity, this.propertyName);
                }
                return TgEditorBehavior._getTooltip.call(this, valueToFormat);
            }
            return "";
        },

        _formatTooltipText: function (valueToFormat) {
            if (valueToFormat !== null) {
                if (Array.isArray(valueToFormat)) {
                    return valueToFormat.length > 0 ? ("<b>" + valueToFormat.join(this.separator) + "</b>") : '';
                } else if (typeof valueToFormat === 'string'){
                    return "<b>" + valueToFormat + "</b>";
                } else if (this.reflector().isEntity(valueToFormat)) {
                    return this._createEntityTooltip(valueToFormat)
                } else {
                    return '';
                }
            }
            return '';
        },

        _createEntityTooltip: function (entity) {
            const titles = this._createEntityTitleObject(entity);
            if (titles.length === 1) {
                return "<b>" + this.reflector().convert(entity) + "</b>" + (entity.get('desc') ? "<br>" + entity.get('desc') : "");
            } else {
                return "<table style='border-collapse: collapse;'>" +
                    titles.map(entry => "<tr><td valign='top' style='padding-left:0'>" + entry.title + ": </td><td valign='top' style='padding-right:0'><b>" + entry.value + "</b></td></tr>").join("") +
                "</table>"  + (entity.get('desc') ? "<br>" + entity.get('desc') : "");
            }
        },

        _changeTitle: function (entity) {
            this._customPropTitle = this._createTitleObject(entity);
        },

        _valueStyle: function (item, index) {
            if (this._customPropTitle && this._customPropTitle.length > 1) {
                if (index < this._customPropTitle.length - 1) {
                    return "padding-right: 5px";
                }
            }
            return "";
        },

        _createTitleObject: function (entity) {
            if (entity !== null) {
                var entityValue = this.reflector()._getValueFor(entity, this.propertyName);
                if (entityValue !== null && !Array.isArray(entityValue) && entityValue.type().shouldDisplayDescription()) {
                    return this._createEntityTitleObject(entityValue);
                }
            }
            return [{value: ""}];
        },

        _createEntityTitleObject: function (entity) {
            if (entity.type().isCompositeEntity()) {
                return this._createCompositeTitle(entity);
            }
            return this._createSimpleTitle(entity);
        },

        _createCompositeTitle: function (entityValue) {
            const titles = [];
            const entityType = entityValue.type();
            entityType.compositeKeyNames().forEach(keyName => {
                if (entityValue.get(keyName)) {
                    titles.push({
                        title: entityType.prop(keyName).title(),
                        value: this.reflector().convert(entityValue.get(keyName))
                    });
                }
            });
            if (titles.length === 1) {
                return [{value: titles[0].value}];
            }
            return titles;
        },

        _createSimpleTitle: function (entityValue) {
            return [{value: this.reflector().convert(entityValue)}];
        },

        _hasDesc: function (entity) {
            if (entity !== null) {
                var entityValue = this.reflector()._getValueFor(entity, this.propertyName);
                if (entityValue !== null && !Array.isArray(entityValue) && entityValue.type().shouldDisplayDescription()) {
                    return !!entityValue.get('desc');
                }
            }
            return false;
        },

        _formatDesc: function (entity) {
            if (entity !== null) {
                var entityValue = this.reflector()._getValueFor(entity, this.propertyName);
                if (entityValue !== null && !Array.isArray(entityValue) && entityValue.type().shouldDisplayDescription() && entityValue.get('desc')) {
                    return entityValue.get('desc');
                }
            }
            return '';
        },

        _hasLayer: function (_editingValue, entity) {
            if (entity !== null) {
                var entityValue = this.reflector()._getValueFor(entity, this.propertyName);
                return entityValue !== null && this.convertToString(this.reflector().convert(entityValue)) === _editingValue && !Array.isArray(entityValue) && entityValue.type().shouldDisplayDescription();
            }
            return false;
        }
    });
})();