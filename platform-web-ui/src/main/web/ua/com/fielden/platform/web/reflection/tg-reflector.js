import '/resources/polymer/@polymer/polymer/polymer-legacy.js';

import { Polymer } from '/resources/polymer/@polymer/polymer/lib/legacy/polymer-fn.js';

/**
 * Used for decimal and money formatting. If the scale value for formatting wasn't specified then the default one is used.
 */
const DEFAULT_SCALE = 2;
/**
 * If the precion for entity type property wasn't defined then the default one should be used.
 */
const DEFAULT_PRECISION = 18;
/**
 * If the string property length wasn't defined then the default one should be used.
 */
const DEFAULT_LENGTH = 0;
/**
 * Used for decimal nad money formatting. If trailing Zeros property wasn't defined
 */
const DEFAULT_TRAILING_ZEROS = true;

const _UNDEFINED_CONFIG_TITLE = '_______________________undefined';
const _LINK_CONFIG_TITLE = '_______________________link';

/**
 * Determines whether the result represents the error.
 */
var _isError0 = function (result) {
    return result !== null
        && (result["@resultType"] === "ua.com.fielden.platform.error.Result" || result["@resultType"] === "ua.com.fielden.platform.web.utils.PropertyConflict")
        && (typeof result.ex !== 'undefined');
};

const _simpleClassName = function (fullClassName) {
    const index = fullClassName.lastIndexOf('.') + 1;
    return fullClassName.substring(index);
};

var _isContinuationError0 = function (result) {
    return _isError0(result) && (typeof result.ex.continuationType !== 'undefined');
}

/**
 * Determines whether the result represents the warning.
 */
var _isWarning0 = function (result) {
    return result !== null && result["@resultType"] === "ua.com.fielden.platform.error.Warning";
};

/**
 * 'EntityTypeProp' creator. Dependencies: none.
 */
var _createEntityTypePropPrototype = function () {
    ////////////////////////////////////////// THE PROTOTYPE FOR EntityTypeProp ////////////////////////////////////////// 
    var EntityTypeProp = function (rawObject) {
        Object.call(this);

        // copy all properties from rawObject after deserialisation
        for (var prop in rawObject) {
            this[prop] = rawObject[prop];
        }
    };
    EntityTypeProp.prototype = Object.create(Object.prototype);
    EntityTypeProp.prototype.constructor = EntityTypeProp;

    /**
     * Returns specific time-zone for the property of type date.
     *
     * IMPORTANT: do not use '_timeZone' field directly!
     */
    EntityTypeProp.prototype.timeZone = function () {
        return typeof this._timeZone === 'undefined' ? null : this._timeZone;
    }

    /**
     * Returns 'true' when the type property is secrete, false otherwise.
     *
     * IMPORTANT: do not use '_secrete' field directly!
     */
    EntityTypeProp.prototype.isSecrete = function () {
        return typeof this._secrete === 'undefined' ? false : this._secrete;
    }

    /**
     * Returns 'true' when the type property is upperCase, false otherwise.
     *
     * IMPORTANT: do not use '_upperCase' field directly!
     */
    EntityTypeProp.prototype.isUpperCase = function () {
        return typeof this._upperCase === 'undefined' ? false : this._upperCase;
    }

    /**
     * Returns 'true' when the property should be displayed only with date portion, false otherwise.
     *
     * IMPORTANT: do not use '_date' field directly!
     */
    EntityTypeProp.prototype.isDate = function () {
        return typeof this._date === 'undefined' ? false : this._date;
    }

    /**
     * Returns 'true' when the property should be displayed only with time portion, false otherwise.
     *
     * IMPORTANT: do not use '_time' field directly!
     */
    EntityTypeProp.prototype.isTime = function () {
        return typeof this._time === 'undefined' ? false : this._time;
    }

    /** 
     * Returns entity type prop title.
     */
    EntityTypeProp.prototype.title = function () {
        return this._title;
    }

    /** 
     * Returns entity type prop description.
     */
    EntityTypeProp.prototype.desc = function () {
        return this._desc;
    }

    /**
     * Returns 'true' when the type property is critOnly, false otherwise.
     *
     * IMPORTANT: do not use '_critOnly' field directly!
     */
    EntityTypeProp.prototype.isCritOnly = function () {
        return typeof this._critOnly === 'undefined' ? false : this._critOnly;
    }

    /**
     * Returns 'true' when the type property is resultOnly, false otherwise.
     *
     * IMPORTANT: do not use '_resultOnly' field directly!
     */
    EntityTypeProp.prototype.isResultOnly = function () {
        return typeof this._resultOnly === 'undefined' ? false : this._resultOnly;
    }

    /**
     * Returns 'true' when the type property is 'ignore', false otherwise.
     *
     * IMPORTANT: do not use '_ignore' field directly!
     */
    EntityTypeProp.prototype.isIgnore = function () {
        return typeof this._ignore === 'undefined' ? false : this._ignore;
    }

    /** 
     * Returns entity type prop length.
     */
    EntityTypeProp.prototype.length = function () {
        return typeof this._length === 'undefined' ? DEFAULT_LENGTH : this._length;
    }

    /** 
     * Returns entity type prop precision.
     */
    EntityTypeProp.prototype.precision = function () {
        return typeof this._precision === 'undefined' ? DEFAULT_PRECISION : this._precision;
    }

    /** 
     * Returns entity type prop scale.
     */
    EntityTypeProp.prototype.scale = function () {
        return typeof this._scale === 'undefined' ? DEFAULT_SCALE : this._scale;
    }

    /** 
     * Returns entity type prop trailingZeros value.
     */
    EntityTypeProp.prototype.trailingZeros = function () {
        return typeof this._trailingZeros === 'undefined' ? DEFAULT_TRAILING_ZEROS : this._trailingZeros;
    }

    return EntityTypeProp;
};

/**
 * 'EntityInstanceProp' creator. Dependencies: none.
 */
var _createEntityInstancePropPrototype = function () {
    ////////////////////////////////////////// THE PROTOTYPE FOR EntityInstanceProp ////////////////////////////////////////// 
    var EntityInstanceProp = function () {
        Object.call(this);
    };
    EntityInstanceProp.prototype = Object.create(Object.prototype);
    EntityInstanceProp.prototype.constructor = EntityInstanceProp;

    /**
     * Returns 'true' when the instance property is editable, false otherwise.
     *
     * IMPORTANT: do not use '_editable' field directly!
     */
    EntityInstanceProp.prototype.isEditable = function () {
        return typeof this._editable === 'undefined' ? true : this._editable;
    }

    /**
     * Returns 'true' when the instance property is changed from original, false otherwise.
     *
     * IMPORTANT: do not use '_cfo' field directly!
     */
    EntityInstanceProp.prototype.isChangedFromOriginal = function () {
        return typeof this._cfo === 'undefined' ? false : this._cfo;
    }

    /**
     * Returns original value in case when the property is changed from original and the entity is persisted.
     *
     * IMPORTANT: do not use '_originalVal' field directly!
     */
    EntityInstanceProp.prototype.originalValue = function () {
        if (!this.isChangedFromOriginal()) {
            throw "No one should access originalValue for not changed from original property.";
        }

        if (typeof this['_originalVal'] === 'undefined') {
            throw "instanceMetaProperty has no _originalVal when it is crucial!";
        }

        return this._originalVal;
    }

    /**
     * Returns 'true' when the instance property is required, false otherwise.
     *
     * IMPORTANT: do not use '_required' field directly!
     */
    EntityInstanceProp.prototype.isRequired = function () {
        return typeof this._required === 'undefined' ? false : this._required;
    }

    /**
     * Returns 'true' when the instance property is visible, false otherwise.
     *
     * IMPORTANT: do not use '_visible' field directly!
     */
    EntityInstanceProp.prototype.isVisible = function () {
        return typeof this._visible === 'undefined' ? true : this._visible;
    }

    /**
     * Returns validation result (failure or warning) for the instance property or 'null' if successful without warnings.
     *
     * IMPORTANT: do not use '_validationResult' field directly!
     */
    EntityInstanceProp.prototype.validationResult = function () {
        return typeof this._validationResult === 'undefined' ? null : this._validationResult;
    }

    /**
     * Returns the max possible length in case of string property, 'undefined' otherwise.
     *
     * IMPORTANT: do not use '_max' field directly!
     */
    EntityInstanceProp.prototype.stringLength = function () {
        // if (this._isString()) { 
        return typeof this._max === 'undefined' ? 0 : this._max;
        // }
        // return undefined;
    }

    /**
     * Returns the max possible integer value in case of integer property (null means unlimited), 'undefined' otherwise.
     *
     * IMPORTANT: do not use '_max' field directly!
     */
    EntityInstanceProp.prototype.integerMax = function () {
        // if (!this._isString()) {
        return typeof this._max === 'undefined' ? null : this._max;
        // }
        // return undefined;
    }

    /**
     * Returns the min possible integer value in case of integer property (null means unlimited), 'undefined' otherwise.
     *
     * IMPORTANT: do not use '_min' field directly!
     */
    EntityInstanceProp.prototype.integerMin = function () {
        // if (!this._isString()) {
        return typeof this._min === 'undefined' ? null : this._min;
        // }
        // return undefined;
    }

    return EntityInstanceProp;
};

/**
 * 'Entity' creator. Dependencies: 'EntityInstanceProp'.
 */
const _createEntityPrototype = function (EntityInstanceProp, StrictProxyException, _isError0, _isWarning0, DynamicEntityKey) {
    ////////////////////////////////////////// THE PROTOTYPE FOR Entity ////////////////////////////////////////// 
    var Entity = function (rawObject) { // rawObject
        Object.call(this);

        if (rawObject) {
            // copy all properties from rawObject if it is not empty
            for (var prop in rawObject) {
                this[prop] = rawObject[prop];
            }
        }
    };
    Entity.prototype = Object.create(Object.prototype);
    Entity.prototype.constructor = Entity;

    /**
     * Returns the type for the entity.
     *
     * IMPORTANT: do not use '_type' field directly!
     */
    Entity.prototype.type = function () {
        return this._type;
    }

    /**
     * Returns the instance prop for the entity.
     *
     * IMPORTANT: do not use '@prop' field directly!
     */
    Entity.prototype.prop = function (name) {
        this.get(name); // ensures that the instance prop of the 'fetched' property is accessed
        if (this._isObjectUndefined("@" + name)) {
            this["@" + name] = new EntityInstanceProp(); // lazily initialise entity instance prop in case when it was not JSON-serialised (all information was 'default')
        }
        return this["@" + name];
    }

    /**
     * Returns the property value for the entity.
     *
     * IMPORTANT: do not use property field directly!
     */
    Entity.prototype.get = function (name) {
        if (name === '') { // empty property name means 'entity itself'
            return this;
        }
        var dotIndex = name.indexOf(".");
        if (dotIndex > -1) {
            var first = name.slice(0, dotIndex);
            var rest = name.slice(dotIndex + 1);
            var firstVal = this.get(first);
            if (firstVal === null) {
                return null;
            } else if (_isEntity(firstVal)) {
                return firstVal.get(rest);
            } else if (firstVal instanceof Array) {
                var internalList = [];
                for (var index = 0; index < firstVal.length; index++) {
                    internalList.push(firstVal[index].get(rest));
                }
                return internalList;
            } else {
                throw 'Unsupported dot-notation [' + name + '] in type [' + this.type().fullClassName() + '].';
            }
        } else {
            if ('key' === name && this.constructor.prototype.type.call(this).isCompositeEntity()) {
                const dynamicKey = new DynamicEntityKey();
                dynamicKey._entity = this;
                return dynamicKey;
            } else if ((this.constructor.prototype.type.call(this)).isUnionEntity() && ['id', 'key', 'desc'].indexOf(name) !== -1) {
                // In case of union entity, its [key / desc / id] should return the [key / desc / id] of corresponding 'active entity'.
                // This slightly deviates from Java 'AbstractUnionEntity' logic in two aspects:
                // 1) Here the key is exactly equal to key of active entity, but in Java the key is equal to String representation of the key of active entity.
                // 2) In case where [key / desc / id] is accessed from empty union entity -- here empty values (aka nulls) are returned, but in Java -- exception is thrown. 
                const activeEntity = this._activeEntity();
                return activeEntity === null ? null : activeEntity.get(name);
            } else if (this._isObjectUndefined(name)) {
                throw new StrictProxyException(name, (this.constructor.prototype.type.call(this))._simpleClassName());
            } else if (this._isIdOnlyProxy(name)) {
                throw new StrictProxyException(name, this.type()._simpleClassName(), true);
            }
            return this[name];
        }
    }

    /**
     * Returns 'active entity' in this union entity.
     * 
     * This method closely resembles methods 'AbstractUnionEntity.activeEntity' and 'AbstractUnionEntity.getNameOfAssignedUnionProperty'.
     */
    Entity.prototype._activeEntity = function () {
        const self = this;
        let activeEntity = null;
        this.traverseProperties(function (name) {
            if (['key', 'desc', 'referencesCount', 'referenced'].indexOf(name) /*AbstractEntity.COMMON_PROPS*/ === -1 && self.get(name) !== null) {
                activeEntity = self.get(name);
            }
        });
        return activeEntity;
    }

    /**
     * Returns the original property value for the entity.
     *
     */
    Entity.prototype.getOriginal = function (propName) {
        var value = this.get(propName);
        var instanceMetaProperty = this.constructor.prototype.prop.call(this, propName);
        if (instanceMetaProperty.isChangedFromOriginal()) {
            return instanceMetaProperty.originalValue();
        } else {
            return value;
        }
    }

    /**
     * Returns 'true' if there is an object member defined (not a function!) with a specified name, 'false' otherwise.
     *
     */
    Entity.prototype._isObjectUndefined = function (name) {
        return (typeof this[name] === 'undefined') || (typeof this[name] === 'function');
    }

    /**
     * Returns 'true' if property represents id-only proxy instance, 'false' otherwise.
     *
     */
    Entity.prototype._isIdOnlyProxy = function (name) {
        return typeof this[name] === 'string' && (this[name].lastIndexOf('_______id_only_proxy_______', 0) === 0); // starts with 'id-only' prefix
    }

    /**
     * Sets the property value for the entity.
     *
     * IMPORTANT: do not use property field directly!
     */
    Entity.prototype.set = function (name, value) {
        this.get(name); // ensures that the instance prop of the 'fetched' property is accessed
        return this[name] = value;
    }

    /**
     * Sets the property value for the [binding!]entity and registers property 'touch'.
     *
     * In case where user interaction takes place, this method registers such 'property touch' for the purposes 
     * of collecting the queue of 'touched' properties.
     *
     * The property that was touched last will reside last in that queue. Also the count of 'touches' is recorded
     * for each property (mainly for logging purposes).
     *
     * Please, note that 'touched' property does not mean 'modified' from technical perspective.
     * But, even if it is not modified -- such property will be forced to be mutated on server (with its origVal) 
     * to have properly invoked its ACE handlers.
     *
     * IMPORTANT: this method is applicable only to binding entities (not fully-fledged)!
     */
    Entity.prototype.setAndRegisterPropertyTouch = function (propertyName, value) {
        const result = this.set(propertyName, value);

        const touched = this["@@touchedProps"];
        const names = touched.names;
        const values = touched.values;
        const counts = touched.counts;
        const index = names.indexOf(propertyName);
        if (index > -1) {
            const prevCount = counts[index];
            names.splice(index, 1);
            values.splice(index, 1);
            counts.splice(index, 1);
            names.push(propertyName);
            values.push(value);
            counts.push(prevCount + 1);
        } else {
            names.push(propertyName);
            values.push(value);
            counts.push(1);
        }
        // need to reset previously cached ID after the property was modified (touched) by the user -- the cached ID becomes stale in that case, and server-side reconstruction of entity-typed property should be KEY-based instead of ID-based. 
        if (typeof this['@' + propertyName + '_id'] !== 'undefined') {
            delete this['@' + propertyName + '_id'];
        }
        console.debug('Just TOUCHED', propertyName, '(', counts[counts.length - 1], ' time). Result:', touched);

        return result;
    }

    /**
     * Traverses all fetched properties in entity. It does not include 'id', 'version', '_type' and '@prop' instance meta-props.
     * 
     * Proxy: 
     *    a) proxied properties are missing in serialised entity graph -- this method disregards such properties;
     *    b) id-only proxy properties exist (foe e.g. '_______id_only_proxy_______673') -- this method disregards such properties too.
     *
     * @param propertyCallback -- function(propertyName) to be called on each property
     */
    Entity.prototype.traverseProperties = function (propertyCallback) {
        var entity = this;
        for (var membName in entity) {
            if (entity.hasOwnProperty(membName) && membName[0] !== "@" && membName !== "_type" && membName !== "id" && membName !== "version") {
                if (!entity._isObjectUndefined(membName) && !entity._isIdOnlyProxy(membName)) {
                    propertyCallback(membName);
                }
            }
        }
    }

    /**
     * Determines whether the entity is valid, which means that there are no invalid properties.
     *
     */
    Entity.prototype.isValid = function () {
        return this.firstFailure() === null;
    }

    /**
     * Determines whether the entity is valid (which means that there are no invalid properties) and no exception has been occured during some server-side process behind the entity (master entity saving, centre selection-crit entity running etc.).
     *
     */
    Entity.prototype.isValidWithoutException = function () {
        return this.isValid() && !this.exceptionOccured();
    }

    /**
     * Determines whether the top-level result, that wraps this entity was invalid, which means that some exception on server has been occured (e.g. saving exception).
     *
     */
    Entity.prototype.exceptionOccured = function () {
        return (typeof this['@@___exception-occured'] === 'undefined') ? null : this['@@___exception-occured'];
    }

    /**
     * Provides a value 'exceptionOccured' flag, which determines whether the top-level result, that wraps this entity was invalid, which means that some exception on server has been occured (e.g. saving exception).
     *
     */
    Entity.prototype._setExceptionOccured = function (exceptionOccured) {
        return this['@@___exception-occured'] = exceptionOccured;
    }

    /**
     * Finds the first failure for the properties of this entity, if any.
     *
     */
    Entity.prototype.firstFailure = function () {
        var self = this;
        var first = null;
        self.traverseProperties(function (propName) {
            if (_isError0(self.prop(propName).validationResult())) {
                first = self.prop(propName).validationResult();
                return;
            }
        });
        return first;
    }

    /**
     * Determines whether the entity is valid with warning, which means that there are no invalid properties but the properties with warnings exist.
     *
     */
    Entity.prototype.isValidWithWarning = function () {
        return this.firstFailure() === null && this.firstWarning() !== null;
    }

    /**
     * Finds the first warning for the properties of this entity, if any.
     *
     */
    Entity.prototype.firstWarning = function () {
        var self = this;
        var first = null;
        self.traverseProperties(function (propName) {
            if (_isWarning0(self.prop(propName).validationResult())) {
                first = self.prop(propName).validationResult();
                return;
            }
        });
        return first;
    }

    /**
     * Returns 'true' if the entity was persisted before and 'false' otherwise.
     */
    Entity.prototype.isPersisted = function () {
        return this.get('id') !== null;
    }
    return Entity;
};

/**
 * 'DynamicEntityKey' creator.
 */
const _createDynamicEntityKeyPrototype = function () {
    ////////////////////////////////////////// THE PROTOTYPE FOR DynamicEntityKey ////////////////////////////////////////// 
    const DynamicEntityKey = function () {
        Object.call(this);
    };
    DynamicEntityKey.prototype = Object.create(Object.prototype);
    DynamicEntityKey.prototype.constructor = DynamicEntityKey;

    /**
     * The method to convert dynamic entity key (key of composite entity) to String.
     *
     * IMPORTANT: this is the mirror of the java method DynamicEntityKey.toString(). So, please be carefull and maintain it
     * in accordance with java counterpart.
     */
    DynamicEntityKey.prototype._convertDynamicEntityKey = function () {
        const compositeEntity = this._entity;
        const compositeKeyNames = compositeEntity.type().compositeKeyNames();
        const compositeKeySeparator = compositeEntity.type().compositeKeySeparator();

        let str = "";
        let first = true;
        for (let i = 0; i < compositeKeyNames.length; i++) {
            const compositePartName = compositeKeyNames[i];
            const compositePart = compositeEntity.get(compositePartName);
            if (compositePart !== null) {
                if (first) {
                    str = str + _convert(compositePart);
                    first = false;
                } else {
                    str = str + compositeKeySeparator + _convert(compositePart);
                }
            }
        }
        return str;
    };

    /**
     * Returns 'true' if this equals to dynamicEntityKey2, 'false' otherwise.
     *
     * IMPORTANT: this is the mirror of the java method DynamicEntityKey.compareTo(). So, please be carefull and maintain it
     * in accordance with java counterparts.
     */
    DynamicEntityKey.prototype._dynamicEntityKeyEqualsTo = function (dynamicEntityKey2) {
        if (this === dynamicEntityKey2) {
            return true;
        }
        if (!_isDynamicEntityKey(dynamicEntityKey2)) {
            return false;
        }
        const entity1 = this._entity;
        const entity2 = dynamicEntityKey2._entity;
        const compositeKeyNames = entity1.type().compositeKeyNames();
        for (let i = 0; i < compositeKeyNames.length; i++) {
            const compositePartName = compositeKeyNames[i];
            let compositePart1, compositePart2;
            try {
                compositePart1 = entity1.get(compositePartName);
            } catch (strictProxyEx1) {
                throw 'Comparison of entities [' + entity1 + ', ' + entity2 + '] failed. Composite key part [' + compositePartName + '] was not fetched in first entity, please check fetching strategy.' + strictProxyEx1;
            }
            try {
                compositePart2 = entity2.get(compositePartName);
            } catch (strictProxyEx2) {
                throw 'Comparison of entities [' + entity1 + ', ' + entity2 + '] failed. Composite key part [' + compositePartName + '] was not fetched in second entity, please check fetching strategy. ' + strictProxyEx2;
            }

            if (!_equalsEx(compositePart1, compositePart2)) {
                return false;
            }
        }
        return true;
    };

    return DynamicEntityKey;
};

/**
 * 'EntityType' creator. Dependencies: 'EntityTypeProp'.
 */
var _createEntityTypePrototype = function (EntityTypeProp) {
    ////////////////////////////////////////// THE PROTOTYPE FOR EntityType ////////////////////////////////////////// 
    var EntityType = function (rawObject) {
        Object.call(this);
        // copy all properties from rawObject after deserialisation
        for (var prop in rawObject) {
            if (prop === "_props") {
                var _props = rawObject[prop];

                for (var p in _props) {
                    if (_props.hasOwnProperty(p)) {
                        var val = _props[p];
                        _props[p] = new EntityTypeProp(val);
                    }
                }

                this[prop] = _props;
            } else {
                this[prop] = rawObject[prop];
            }
        }
    };
    EntityType.prototype = Object.create(Object.prototype);
    EntityType.prototype.constructor = EntityType;

    /**
     * Returns the identifier for the entity type.
     *
     */
    EntityType.prototype.identifier = function () {
        return this._identifier;
    }

    /**
     * Returns full Java class name for the entity type.
     *
     */
    EntityType.prototype.fullClassName = function () {
        return this.key;
    }

    /**
     * Returns full not enhanced Java class name for the entity type.
     */
    EntityType.prototype.notEnhancedFullClassName = function () {
        const fullClassName = this.fullClassName();
        const enhancedIndex = fullClassName.indexOf("$$TgEntity");
        if (enhancedIndex >= 0) {
            return fullClassName.substring(0, enhancedIndex);
        }
        return fullClassName;
    }

    /**
     * Returns simple Java class name for the entity type.
     *
     */
    EntityType.prototype._simpleClassName = function () {
        return _simpleClassName(this.fullClassName());
    }

    /**
     * Returns not enhanced simple Java class name for the entity type.
     *
     */
    EntityType.prototype._notEnhancedSimpleClassName = function () {
        var ind = this.fullClassName().lastIndexOf(".") + 1,
            simpleClassName = this.fullClassName().substring(ind),
            enhancedIndex = simpleClassName.indexOf("$$TgEntity");
        if (enhancedIndex >= 0) {
            return simpleClassName.substring(0, enhancedIndex);
        }
        return simpleClassName;
    }

    /**
     * Returns 'true' when the entity type represents composite entity, 'false' otherwise.
     *
     * Use compositeKeyNames() function to determine property names for the key members.
     */
    EntityType.prototype.isCompositeEntity = function () {
        return typeof this._compositeKeyNames !== 'undefined' && this._compositeKeyNames && this._compositeKeyNames.length > 0;
    }

    /**
     * Returns 'true' if the entity type represents union entity type, 'false' otherwise.
     *
     */
    EntityType.prototype.isUnionEntity = function () {
        return typeof this['_union'] === 'undefined' ? false : this['_union'];
    }

    /** 
     * Returns the property names for the key members in case of composite entity, 'undefined' otherwise.
     */
    EntityType.prototype.compositeKeyNames = function () {
        return this._compositeKeyNames;
    }

    /** 
     * Returns the key member separator in case of composite entity, 'undefined' otherwise.
     */
    EntityType.prototype.compositeKeySeparator = function () {
        return typeof this._compositeKeySeparator === 'undefined' ? " " : this._compositeKeySeparator;
    }

    /**
     * Returns 'true' if the entity type represents a persistent entity.
     *
     */
    EntityType.prototype.isPersistent = function () {
        return this['_persistent'] === true;
    }

    /**
     * Returns 'true' if the entity type represents a continuation entity.
     *
     */
    EntityType.prototype.isContinuation = function () {
        return typeof this['_continuation'] === 'undefined' ? false : this['_continuation'];
    }

    /**
     * Returns 'true' if editors for this entity type should display description of its values when not focused, 'false' otherwise.
     *
     */
    EntityType.prototype.shouldDisplayDescription = function () {
        return typeof this['_displayDesc'] === 'undefined' ? false : this['_displayDesc'];
    }

    /** 
     * Returns entity title.
     */
    EntityType.prototype.entityTitle = function () {
        return typeof this._entityTitle === 'undefined' ? this._entityTitleDefault() : this._entityTitle;
    }

    /** 
     * Returns entity type property with the specified name.
     */
    EntityType.prototype.prop = function (name) {
        return typeof this._props !== 'undefined' && this._props && this._props[name] ? this._props[name] : null;
    }

    /** 
     * Returns entity description.
     */
    EntityType.prototype.entityDesc = function () {
        return typeof this._entityDesc === 'undefined' ? this._entityDescDefault() : this._entityDesc;
    }

    /** 
     * Returns default entity title.
     */
    EntityType.prototype._entityTitleDefault = function () {
        var title = this._breakToWords(this._simpleClassName());
        return title;
    }

    /** 
     * Returns default entity desc.
     */
    EntityType.prototype._entityDescDefault = function () {
        var title = this._breakToWords(this._simpleClassName());
        return title + " entity";
    }

    /** 
     * Breaks camelCased string onto words and separates it with ' '.
     */
    EntityType.prototype._breakToWords = function (str) { // see http://stackoverflow.com/questions/10425287/convert-string-to-camelcase-with-regular-expression
        return str.replace(/([A-Z])/g, function (match, group) {
            return " " + group;
        }).trim();
    }

    return EntityType;
};

//////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////// EXCEPTIONS /////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////

/**
 * 'StrictProxyException' creator.
 */
var _createStrictProxyExceptionPrototype = function () {
    /**
   * Exception prototype for strict proxy exceptions.
   */
    var StrictProxyException = function (propName, simpleClassName, isIdOnlyProxy) { // rawObject
        Object.call(this);

        this.message = "Strict proxy exception: property [" + propName + "] " + (isIdOnlyProxy === true ? "is id-only proxy" : "does not exist") + " in the entity of type [" + simpleClassName + "]. Please, check the fetch strategy or construction strategy of the entity object.";
    };
    StrictProxyException.prototype = Object.create(Object.prototype);
    StrictProxyException.prototype.constructor = StrictProxyException;

    /**
     * Overridden toString method to represent this exception more meaningfully than '[Object object]'.
     *
     */
    StrictProxyException.prototype.toString = function () {
        return this.message;
    }
    return StrictProxyException;
}

/**
 * 'UnsupportedConversionException' creator.
 */
var _createUnsupportedConversionExceptionPrototype = function () {
    /**
     * Exception prototype for unsupported conversions.
     */
    var UnsupportedConversionException = function (value) {
        Object.call(this);

        this.message = "Unsupported conversion exception: the conversion for value [" + value + "] is unsupported at this stage. Value typeof === " + (typeof value) + ".";
    };
    UnsupportedConversionException.prototype = Object.create(Object.prototype);
    UnsupportedConversionException.prototype.constructor = UnsupportedConversionException;

    /**
     * Overridden toString method to represent this exception more meaningfully than '[Object object]'.
     *
     */
    UnsupportedConversionException.prototype.toString = function () {
        return this.message;
    }
    return UnsupportedConversionException;
}

//////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////// EXCEPTIONS [END] /////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////

const _SPEPrototype = _createStrictProxyExceptionPrototype();
const _UCEPrototype = _createUnsupportedConversionExceptionPrototype();

const _ETPPrototype = _createEntityTypePropPrototype();
const _ETPrototype = _createEntityTypePrototype(_ETPPrototype);
const _DEKPrototype = _createDynamicEntityKeyPrototype();

/**
 * The prototype for entity's instance-scoped property. Can be reached by 'entity.prop("name")' call.
 */
const _EIPPrototype = _createEntityInstancePropPrototype();
/**
 * The prototype for entities.
 */
const _EPrototype = _createEntityPrototype(_EIPPrototype, _SPEPrototype, _isError0, _isWarning0, _DEKPrototype);

/**
 * Determines whether the specified object represents the entity.
 */
const _isEntity = function (obj) {
    return obj !== null && (obj instanceof _EPrototype);
};

/**
 * Determines whether the specified object represents dynamic entity key.
 */
const _isDynamicEntityKey = function (obj) {
    return obj && (obj instanceof _DEKPrototype);
};

const _isPropertyValueObject = function (value, subValueName) {
    return value !== null && typeof value === 'object' && typeof value[subValueName] !== 'undefined';
};

const _isMoney = function (value) {
    return _isPropertyValueObject(value, 'amount');
};
const _moneyVal = function (value) {
    return value['amount'];
};
const _isColour = function (value) {
    return _isPropertyValueObject(value, 'hashlessUppercasedColourValue');
};
const _colourVal = function (value) {
    return value['hashlessUppercasedColourValue'];
};
const _isHyperlink = function (value) {
    return _isPropertyValueObject(value, 'value');
};
const _hyperlinkVal = function (value) {
    return value['value'];
};

/**
 * Returns 'true' if the regular values are equal, 'false' otherwise.
 */
const _equalsEx = function (value1, value2) {
    // if (value1) {
    //     if (!value2) {
    //         return false;
    //     } else {
    //         return ...
    //     }
    // }

    // TODO 1: rectify whether this implementation is good
    // TODO 2: potentially, extend implementation to support composite types: objects?
    if (_isDynamicEntityKey(value1)) {
        return value1._dynamicEntityKeyEqualsTo(value2);
    } else if (_isEntity(value1)) {
        return _entitiesEqualsEx(value1, value2);
    } else if (Array.isArray(value1)) {
        return _arraysEqualsEx(value1, value2);
    } else if (_isMoney(value1)) {
        return _isMoney(value2) && _equalsEx(_moneyVal(value1), _moneyVal(value2));
    } else if (_isColour(value1)) {
        return _isColour(value2) && _equalsEx(_colourVal(value1), _colourVal(value2));
    } else if (_isHyperlink(value1)) {
        return _isHyperlink(value2) && _equalsEx(_hyperlinkVal(value1), _hyperlinkVal(value2));
    }
    return value1 === value2;
};

/**
 * Returns 'true' if arrays are equal, 'false' otherwise.
 */
var _arraysEqualsEx = function (array1, array2) {
    if (array1 === array2) {
        return true;
    }
    if (!Array.isArray(array2)) {
        return false;
    }

    if (array1.length !== array2.length) {
        return false;
    }

    // now can compare items
    for (var index = 0; index < array1.length; index++) {
        if (!_equalsEx(array1[index], array2[index])) {
            return false;
        }
    }
    return true;
};

/**
 * Returns 'true' if the entities are equal, 'false' otherwise.
 *
 * IMPORTANT: this is the mirror of the java methods AbstractEntity.equals() and DynamicEntityKey.compareTo(). So, please be carefull and maintain it
 * in accordance with java counterparts.
 */
const _entitiesEqualsEx = function (entity1, entity2) {
    if (entity1 === entity2) {
        return true;
    }
    if (!_isEntity(entity2)) {
        return false;
    }
    // let's ensure that types match
    const entity1Type = entity1.constructor.prototype.type.call(entity1);
    const entity2Type = entity2.constructor.prototype.type.call(entity2);
    // in most cases, two entities of the same type will be compared -- their types will be equal by reference
    // however generated types re-register on each centre run / refresh, so need to compare their base types (this will also cover the case where multiple server nodes are used and different nodes generate different types from the same base type)
    if (entity1Type !== entity2Type && entity1Type.notEnhancedFullClassName() !== entity2Type.notEnhancedFullClassName()) {
        return false;
    }
    // now can compare key values
    let key1, key2;

    try {
        key1 = entity1.get('key');
    } catch (strictProxyEx1) {
        throw 'Comparison of entities [' + entity1 + ', ' + entity2 + '] failed. Property \'key\' was not fetched in first entity, please check fetching strategy.' + strictProxyEx1;
    }

    try {
        key2 = entity2.get('key');
    } catch (strictProxyEx2) {
        throw 'Comparison of entities [' + entity1 + ', ' + entity2 + '] failed. Property \'key\' was not fetched in second entity, please check fetching strategy.' + strictProxyEx2;
    }
    return _equalsEx(key1, key2);
};

/**
 * Converts the property value, that has got from deserialised entity instance, to the form, that is suitable for editors binding.
 */
const _convert = function (value) {
    if (value === null) { // 'null' is the missing value representation for TG web editors
        return null;
    } else if (value instanceof _DEKPrototype) {
        return value._convertDynamicEntityKey();
    } else if (value instanceof _EPrototype) {
        return _convert(value.get('key'));
    } else if (typeof value === "number") { // for number value -- return the same value for editors (includes date, integer, decimal number editors)
        return value;
    } else if (typeof value === "boolean") { // for boolean value -- return the same value for editors
        return value;
    } else if (typeof value === "object" && value.hasOwnProperty("amount") && value.hasOwnProperty("currency") && value.hasOwnProperty("taxPercent")) { // for money related value -- return the same value for editors
        return value;
    } else if (typeof value === "string") { // for string value -- return the same value for editors
        return value;
    } else if (Array.isArray(value)) { // for Array value -- return the same value for tg-entity-search-criteria editor
        // Array items should be converted one-by-one (no entity-typed items should remain being entity-typed)
        var convertedArray = [];
        for (var index = 0; index < value.length; index++) {
            convertedArray.push(_convert(value[index]));
        }
        return convertedArray;
    } else if (typeof value === "object" && (value.hasOwnProperty("hashlessUppercasedColourValue") || value.hasOwnProperty("value"))) {
        return value;
    } else if (typeof value === "object" && Object.getOwnPropertyNames(value).length === 0) {
        return value;
    } else {
        throw new _UCEPrototype(value);
    }
};

/**
 * Completes the process of type table preparation -- creates instances of EntityType objects for each entity type in type table.
 */
var _providePrototypes = function (typeTable, EntityType) {
    for (var key in typeTable) {
        if (typeTable.hasOwnProperty(key)) {
            var entityType = typeTable[key];
            typeTable[key] = new EntityType(entityType);
            // entityType.prototype = EntityType.prototype;
            // entityType.__proto__ = EntityType.prototype;
        }
    }
    console.log("typeTable =", typeTable);
    return typeTable;
};

/**
 * The table for entity types.
 *
 * NOTE: 'typeTable' part will be generated by server to provide current state for the entity types table.
 */
var _typeTable = _providePrototypes(@typeTable, _ETPrototype);

export const TgReflector = Polymer({
    is: 'tg-reflector',

    getType: function (typeName) {
        return _typeTable[typeName];
    },

    /**
     * Registers new entity type inside the type table.
     */
    registerEntityType: function (newType) {
        var EntityType = this._getEntityTypePrototype();
        var registeredType = new EntityType(newType);
        _typeTable[registeredType.fullClassName()] = registeredType;
        console.log("Registering new entity type with identifier ", registeredType.identifier(), registeredType);
        return registeredType;
    },

    /**
     * Returns the entity type property instance for specified entity and dot notation property name.
     */
    getEntityTypeProp: function (entity, dotNotatedProp) {
        const lastDotIndex = dotNotatedProp.lastIndexOf(".");
        const rest = lastDotIndex > -1 ? dotNotatedProp.slice(lastDotIndex + 1) : dotNotatedProp;
        const firstVal = lastDotIndex > -1 ? entity.get(dotNotatedProp.slice(0, lastDotIndex)) : entity;
        return firstVal && rest.length > 0 ? firstVal.constructor.prototype.type.call(firstVal).prop(rest) || undefined : undefined;
    },

    /**
     * Finds the entity type by its full class name. Returns 'null' if no registered entity type for such 'typeName' exists.
     */
    findTypeByName: function (typeName) {
        var type = _typeTable[typeName];
        return type ? type : null;
    },

    /**
     * Returns the prototype for 'EntityInstanceProp'.
     *
     * Please, do not use it directly, only, perhaps, in tests.
     */
    getEntityInstancePropPrototype: function () {
        return _EIPPrototype;
    },

    /**
     * Returns the prototype for 'Entity'.
     *
     * Please, do not use it directly, only, perhaps, in tests.
     */
    getEntityPrototype: function () {
        return _EPrototype;
    },

    /**
     * Returns the prototype for 'EntityType'.
     *
     * Please, do not use it directly, only, perhaps, in tests.
     */
    _getEntityTypePrototype: function () {
        return _ETPrototype;
    },

    /**
     * Returns the prototype for 'DynamicEntityKey'.
     *
     * Please, do not use it directly, only, perhaps, in tests.
     */
    getDynamicEntityKeyPrototype: function () {
        return _DEKPrototype;
    },

    /**
     * Returns the prototype for 'StrictProxyException'.
     *
     * Please, do not use it directly, only, perhaps, in tests.
     */
    getStrictProxyExceptionPrototype: function () {
        return _SPEPrototype;
    },

    /**
     * Returns 'true' if the regular values are equal, 'false' otherwise.
     */
    equalsEx: function (value1, value2) {
        return _equalsEx(value1, value2);
    },

    /**
     * Determines whether result represents the error.
     */
    isError: function (result) {
        return _isError0(result);
    },

    /**
     * Determines whether result represents an error that indicates continuation.
     */
    isContinuationError: function (result) {
        return _isContinuationError0(result);
    },

    /**
     * Determines whether result represents the warning.
     */
    isWarning: function (result) {
        return _isWarning0(result);
    },

    //////////////////// SERVER EXCEPTIONS UTILS ////////////////////
    /**
     * Returns a meaninful representation for exception message (including user-friendly version for NPE, not just 'null').
     */
    exceptionMessage: function (exception) {
        return exception.message === null ? "Null pointer exception" : exception.message;
    },

    /**
     * Returns a meaninful representation for errorObject message.
     */
    exceptionMessageForErrorObject: function (errorObject) {
        return errorObject.message;
    },

    /**
     * Returns html representation for the specified exception trace (including 'cause' expanded, if any).
     */
    stackTrace: function (ex) {
        // collects error cause by traversing the stack into an ordered list
        var causeCollector = function (ex, causes) {
            if (ex) {
                causes = causes + "<li>" + this.exceptionMessage(ex) + "</li>";
                printStackTrace(ex);
                if (ex.cause !== null) {
                    causes = causeCollector(ex.cause, causes);
                }
            }
            return causes + "</ol>";
        }.bind(this);

        // ouputs the exception stack trace into the console as warning
        var printStackTrace = function (ex) {
            var msg = "No cause and stack trace.";
            if (ex) {
                msg = this.exceptionMessage(ex) + '\n';
                if (Array.isArray(ex.stackTrace)) {
                    for (var i = 0; i < ex.stackTrace.length; i += 1) {
                        var st = ex.stackTrace[i];
                        msg = msg + st.className + '.java:' + st.lineNumber + ':' + st.methodName + ';\n';
                    }
                }
            }
            console.warn(msg);
        }.bind(this);

        if (ex) {
            var causes = "<b>" + this.exceptionMessage(ex) + "</b>";
            printStackTrace(ex);
            if (ex.cause !== null) {
                causes = causeCollector(ex.cause, causes + "<br><br>Cause(s):<br><ol>")
            }

            return causes;
        }

    },

    /**
     * Returns html representation for the specified errorObject stack.
     */
    stackTraceForErrorObjectStack: function (stack) {
        console.log("STACK", stack);
        // TODO still "NOT IMPLEMENTED!";
        return stack.toString();
    },

    //////////////////// SERVER EXCEPTIONS UTILS [END] //////////////

    /**
     * Determines whether the specified object represents the entity.
     */
    isEntity: function (obj) {
        return _isEntity(obj);
    },

    /**
     * Creates the 'entity' without concrete type specified.
     */
    newEntityEmpty: function () {
        var Entity = this.getEntityPrototype();
        return new Entity();
    },

    /**
     * Creates the 'entity instance prop'.
     */
    newEntityInstancePropEmpty: function () {
        var EntityInstanceProp = this.getEntityInstancePropPrototype();
        return new EntityInstanceProp();
    },

    /**
     * Creates the 'entity' with concrete type, specified as 'typeName' string.
     */
    newEntity: function (typeName) {
        var newOne = this.newEntityEmpty();

        newOne["_type"] = this.findTypeByName(typeName);
        newOne["id"] = null;
        newOne["version"] = 0;
        return newOne;
    },

    /**
     * Converts the property value, that has got from deserialised entity instance, to the form, that is suitable for editors binding.
     */
    convert: function (value) {
        return _convert(value);
    },

    /**
     * Converts the value of property with 'propertyName' name from fully-fledged entity 'entity' into the 'bindingView' binding entity.
     *
     * This implementation takes care of the aspect of property validity and, in case where the property is invalid, then the values are taken from previously bound property ('previousModifiedPropertiesHolder').
     * This ensures that corresponding editor will show invalid value, that was edited by the user and did not pass server-side validation (fully fledged entity contains previous valid value in this case + validation error).
     */
    convertPropertyValue: function (bindingView, propertyName, entity, previousModifiedPropertiesHolder) {
        if (this.isError(entity.prop(propertyName).validationResult())) {
            if (previousModifiedPropertiesHolder === null) { // is a brand new instance just received from server?
                // bind the received from server property value
                this._convertFullPropertyValue(bindingView, propertyName, entity.get(propertyName));
            } else { // otherwise, this entity instance has already been received before and should be handled accordingly
                if (typeof previousModifiedPropertiesHolder[propertyName].val === 'undefined') {
                    // EDGE-CASE: if the value becomes invalid not because the action done upon this property -- 
                    //   but because the action on other property -- the previous version of modifiedPropsHolder will not hold
                    //   invalid 'attempted value' -- but originalVal exists and should be used in this case!
                    bindingView[propertyName] = previousModifiedPropertiesHolder[propertyName].origVal;
                    if (typeof previousModifiedPropertiesHolder[propertyName].origValId !== 'undefined') {
                        bindingView['@' + propertyName + '_id'] = previousModifiedPropertiesHolder[propertyName].origValId;
                    }
                } else {
                    bindingView[propertyName] = previousModifiedPropertiesHolder[propertyName].val;
                    if (typeof previousModifiedPropertiesHolder[propertyName].valId !== 'undefined') {
                        bindingView['@' + propertyName + '_id'] = previousModifiedPropertiesHolder[propertyName].valId;
                    }
                }
            }
        } else {
            var fullValue = entity.get(propertyName);
            this._convertFullPropertyValue(bindingView, propertyName, fullValue);

            const touchedProps = bindingView['@@touchedProps'];
            const touchedPropIndex = touchedProps.names.indexOf(propertyName);
            if (touchedPropIndex > -1 && !this.equalsEx(bindingView.get(propertyName), touchedProps.values[touchedPropIndex])) {
                // make the property untouched in case where its value was sucessfully mutated through definer of other property (it means that the value is valid and different from the value originated from user's touch)
                touchedProps.names.splice(touchedPropIndex, 1);
                touchedProps.counts.splice(touchedPropIndex, 1);
                touchedProps.values.splice(touchedPropIndex, 1);
            }

            if (this.isEntity(fullValue)) {
                bindingView["@" + propertyName + "_desc"] = this.convert(fullValue.get("desc"));
            }
        }
        if (typeof bindingView[propertyName] === 'undefined' || bindingView[propertyName] === undefined) {
            throw "Illegal value exception: the property [" + propertyName + "] can not be assigned as [" + bindingView[propertyName] + "].";
        }
    },

    /**
     * Converts original value of property with 'propertyName' name from fully-fledged entity 'entity' into the 'originalBindingView' binding entity.
     */
    convertOriginalPropertyValue: function (originalBindingView, propertyName, entity) {
        this._convertFullPropertyValue(originalBindingView, propertyName, entity.getOriginal(propertyName));
    },

    /**
     * Converts property's 'fullValue' into binding entity value representation. Takes care of id-based value conversion for entity-typed properties. 
     */
    _convertFullPropertyValue: function (bindingView, propertyName, fullValue) {
        bindingView[propertyName] = this.convert(fullValue);
        if (this.isEntity(fullValue) && fullValue.get('id') !== null) {
            bindingView['@' + propertyName + '_id'] = fullValue.get('id');
        }
    },

    /**
     * Formates the numbers in to string based on specified loacles. If the value is null then returns empty string.
     */
    formatNumber: function (value, locale) {
        if (value !== null) {
            return value.toLocaleString(locale);
        }
        return '';
    },

    /**
     * Formates numbers with floating point in to string based on locales. If the value is null then returns empty string.
     */
    formatDecimal: function (value, locale, scale, trailingZeros) {
        if (value !== null) {
            const definedScale = typeof scale === 'undefined' || scale === null || scale < 0 || scale > 20 /* 0 and 20 are allowed bounds for scale*/ ? DEFAULT_SCALE : scale;
            const options = { maximumFractionDigits: definedScale };
            if (trailingZeros !== false) {
                options.minimumFractionDigits = definedScale;
            }
            return value.toLocaleString(locale, options);
        }
        return '';
    },

    /**
     * Format money numbers in to string based on locales. If the value is null then returns empty string.
     */
    formatMoney: function (value, locale, scale, trailingZeros) {
        if (value !== null) {
            return '$' + this.formatDecimal(value.amount, locale, scale, trailingZeros);
        }
        return '';
    },

    /**
     * Returns the binding value for the specified 'bindingEntity' and 'dotNotatedName' of the property.
     *
     * This supports the retrieval of binding value for dot-notation properties with the use of bindingEntity's '@@origin'.
     */
    getBindingValue: function (bindingEntity, dotNotatedName) {
        return this.isDotNotated(dotNotatedName) ? this.convert(this._getValueFor(bindingEntity, dotNotatedName)) : bindingEntity.get(dotNotatedName);
    },

    /**
     * Returns the binding value for the specified 'bindingEntity' and 'dotNotatedName' of the property.
     *
     * This supports the retrieval of binding value for dot-notation properties with the use of bindingEntity's '@@origin'.
     */
    getPropertyValue: function (fullyFledgedEntity, dotNotatedName) {
        return this.convert(fullyFledgedEntity.get(dotNotatedName));
    },

    /**
     * Returns the full value for the specified 'bindingEntity' and 'dotNotatedName' of the property.
     *
     * This method does no conversion of the value to 'binding' representation.
     */
    _getValueFor: function (bindingEntity, dotNotatedName) {
        return bindingEntity["@@origin"].get(dotNotatedName);
    },

    /**
     * Convenient method for retrieving of 'customObject' from deserialised array.
     */
    customObject: function (arrayOfEntityAndCustomObject) {
        if (arrayOfEntityAndCustomObject.length >= 2) {
            return arrayOfEntityAndCustomObject[1];
        } else {
            return null;
        }
    },

    /**
     * Fills in the centre context holder with 'master entity' based on whether should 'requireMasterEntity'.
     */
    provideMasterEntity: function (requireMasterEntity, centreContextHolder, getMasterEntity) {
        if (requireMasterEntity === "true") {
            centreContextHolder["masterEntity"] = getMasterEntity();
        } else if (requireMasterEntity === "false") { // 'masterEntity' will be proxied after server-side deserialisation
        } else {
            throw "Unknown value for attribute 'requireMasterEntity': " + requireMasterEntity;
        }
    },

    /**
     * Fills in the centre context holder with 'selected entities' based on whether should 'requireSelectedEntities' (ALL, ONE or NONE).
     */
    provideSelectedEntities: function (requireSelectedEntities, centreContextHolder, getSelectedEntities) {
        if (requireSelectedEntities === "ALL") {
            centreContextHolder["selectedEntities"] = getSelectedEntities();
        } else if (requireSelectedEntities === "ONE") {
            centreContextHolder["selectedEntities"] = getSelectedEntities().length > 0 ? [getSelectedEntities()[0]] : [];
        } else if (requireSelectedEntities === "NONE") { // 'selectedEntities' will be proxied after server-side deserialisation
            centreContextHolder["selectedEntities"] = [];
        } else {
            throw "Unknown value for attribute 'requireSelectedEntities': " + requireSelectedEntities;
        }
    },

    /**
     * Returns 'true' if the propertyName is specified in 'dot-notation' syntax, otherwise 'false'.
     */
    isDotNotated: function (propertyName) {
        return propertyName.indexOf(".") > -1;
    },

    /**
     * Returns an entity instance that is the value of the property refered to by 'dotNotatedPropertyName' without the last propert part.
     * Effectively, the returned values if super-property value. 
     * For example, for 'entity.entityValuedProp.someProp' the returned value should an entity referenced by 'entity.entityValuedProp'. 
     */
    entityPropOwner: function (entity, dotNotatedPropertyName) {
        if (this.isDotNotated(dotNotatedPropertyName) === true) {
            const lastDotIndex = dotNotatedPropertyName.lastIndexOf(".");
            const propName = dotNotatedPropertyName.substring(0, lastDotIndex);
            return entity.get(propName);
        }
        return undefined;
    },

    /**
     * Extracts simple class name from full class name (removes package from it).
     */
    simpleClassName: function (fullClassName) {
        return _simpleClassName(fullClassName);
    },

    /**
     * Creates the context holder to be transferred with actions, centre autocompletion process, query enhancing process etc.
     *
     * @param originallyProducedEntity -- in case if new entity is operated on, this instance holds an original fully-fledged contextually produced entity.
     */
    createContextHolder: function (
        requireSelectionCriteria, requireSelectedEntities, requireMasterEntity,
        createModifiedPropertiesHolder, getSelectedEntities, getMasterEntity,
        originallyProducedEntity
    ) {
        var centreContextHolder = this.newEntity("ua.com.fielden.platform.entity.functional.centre.CentreContextHolder");
        centreContextHolder.id = null;
        centreContextHolder['customObject'] = {}; // should always exist, potentially empty
        centreContextHolder['key'] = 'centreContextHolder_key';
        centreContextHolder['desc'] = 'centreContextHolder description';

        if (requireSelectionCriteria !== null) {
            this.provideSelectionCriteria(requireSelectionCriteria, centreContextHolder, createModifiedPropertiesHolder);
            if (originallyProducedEntity) {
                centreContextHolder['originallyProducedEntity'] = originallyProducedEntity;
            }
        }
        if (requireSelectedEntities !== null) {
            this.provideSelectedEntities(requireSelectedEntities, centreContextHolder, getSelectedEntities);
        }
        if (requireMasterEntity !== null) {
            this.provideMasterEntity(requireMasterEntity, centreContextHolder, getMasterEntity);
        }
        return centreContextHolder;
    },

    /**
     * Fills in the centre context holder with 'selection criteria modified props holder' based on whether should 'requireSelectionCriteria'.
     */
    provideSelectionCriteria: function (requireSelectionCriteria, centreContextHolder, createModifiedPropertiesHolder) {
        if (requireSelectionCriteria === "true") {
            centreContextHolder["modifHolder"] = createModifiedPropertiesHolder();
        } else if (requireSelectionCriteria === "false") { // 'modifHolder' will be proxied after server-side deserialisation
        } else {
            throw "Unknown value for attribute 'requireSelectionCriteria': " + requireSelectionCriteria;
        }
    },

    /**
     * Creates the holder of modified properties, originallyProducedEntity and savingContext.
     *
     * There are three cases:
     *    1) modifiedPropertiesHolder.id !== null and the entity will be fetched from persistent storage (in this case originallyProducedEntity is always null, savingContext is not applicable)
     *    2) modifiedPropertiesHolder.id === null && originallyProducedEntity !== null and the entity will be deserialised from originallyProducedEntity and modifHolder applied (in this case savingContext is not applicable)
     *    3) otherwise the entity will be produced through savingContext-dependent producer (only in this case savingContext is applicable)
     *
     * @param originallyProducedEntity -- in case if new entity is operated on, this instance holds an original fully-fledged contextually produced entity.
     */
    createSavingInfoHolder: function (originallyProducedEntity, modifiedPropertiesHolder, savingContext, continuationsMap) {
        const savingInfoHolder = this.newEntity("ua.com.fielden.platform.entity.functional.centre.SavingInfoHolder");
        savingInfoHolder.id = null;
        savingInfoHolder['key'] = 'NO_KEY';
        savingInfoHolder['desc'] = 'savingInfoHolder description';
        savingInfoHolder['modifHolder'] = modifiedPropertiesHolder;
        savingInfoHolder['originallyProducedEntity'] = originallyProducedEntity;

        if (savingContext) { // if saving context was defined (not 'undefined'):
            savingInfoHolder['centreContextHolder'] = savingContext;
        }

        if (typeof continuationsMap !== 'undefined') {
            var continuations = [];
            var continuationProperties = [];
            for (var continuationProperty in continuationsMap) {
                if (continuationsMap.hasOwnProperty(continuationProperty)) {
                    continuations.push(continuationsMap[continuationProperty]);
                    continuationProperties.push(continuationProperty);
                }
            }
            savingInfoHolder['continuations'] = continuations;
            savingInfoHolder['continuationProperties'] = continuationProperties;
        }
        return savingInfoHolder;
    },

    /**
     * Provides custom property into 'centreContextHolder' internals.
     */
    setCustomProperty: function (centreContextHolder, name, value) {
        centreContextHolder["customObject"][name] = value;
    },

    /**
     * Removes custom property from 'centreContextHolder' internals, if exists.
     */
    removeCustomProperty: function (centreContextHolder, name) {
        if (typeof (centreContextHolder["customObject"])[name] !== 'undefined') {
            delete (centreContextHolder["customObject"])[name];
        }
    },

    /**
     * Discards all requests of 'ajaxElement' if any.
     *
     * @param exceptLastOne -- if 'true' disacrds all requests except the last one and returns that last request
     * Returns the number of aborted requests (or last undiscarded request in case of exceptLastOne === 'true')
     */
    discardAllRequests: function (ajaxElement, exceptLastOne) {
        var number = 0;
        if (ajaxElement.loading && ajaxElement.activeRequests.length > 0) { // need to ensure that activeRequests are not empty; if they are empty, 'loading' property of 'ajaxElement' can still be true.
            if (exceptLastOne === true && ajaxElement.activeRequests.length === 1) {
                return number;
            } else {
                // get oldest request and discard it
                var oldestRequest = ajaxElement.activeRequests[0];
                // there is a need to explicitly abort iron-request instance since _discardRequest() does not perform that action:
                oldestRequest.abort();
                number = number + 1;

                // discards oldest request in terms of 'iron-ajax' element -- after that the request is not 'active' 
                //   and is removed from consideration (for e.g., 'loading' property could be recalculated) -- but this
                //   request is not aborted!
                // TODO maybe, the private API usage should be removed, and 'debouncing' should be used instead -- please, consider
                ajaxElement._discardRequest(oldestRequest);

                // discard all other requests if there are any:
                number = number + this.discardAllRequests(ajaxElement, exceptLastOne);
            }
        }
        return number;
    },

    /**
     * Validates the presence of originallyProducedEntity based on number representation of entity id.
     */
    _validateOriginallyProducedEntity: function (originallyProducedEntity, idNumber) {
        if (idNumber === null) {
            if (!_isEntity(originallyProducedEntity)) {
                throw 'For new entities (null id) originallyProducedEntity should always exist.';
            }
        } else if (Number.isInteger(idNumber)) {
            if (_isEntity(originallyProducedEntity)) {
                throw 'For existing entities (id exists) originallyProducedEntity should always be empty.';
            }
        } else {
            throw 'Unknown id number [' + idNumber + ']';
        }
        return originallyProducedEntity;
    },

    /**
     * Validates the context based on string representation of entity id during retrieval process.
     * 
     * For non-empty context:
     *  1) if 'new' entity is retrieved then full context (CentreContextHolder) is necessary to be able to contextually restore the entity.
     *  2) if 'find_or_new' entity is retrieved then only master functional entity (SavingInfoHolder) is necessary to be able to contextually restore the entity -- empty context (CentreContextHolder)
     *     will be created on server (see EntityResource.retrieve method) and master functional entity will be set into it. This is most likely the situation of embedded master inside other master.
     */
    _validateRetrievalContext: function (context, idString) {
        if (context) {
            if (idString === 'new') {
                if (!_isEntity(context) || context.type()._simpleClassName() !== 'CentreContextHolder') {
                    throw 'Non-empty context for "new" entity during retrieval should be of type CentreContextHolder. Context = ' + context;
                }
            } else if (idString === 'find_or_new') {
                if (!_isEntity(context) || context.type()._simpleClassName() !== 'SavingInfoHolder') {
                    throw 'Non-empty context for "find_or_new" entity during retrieval should be of type SavingInfoHolder. Context = ' + context;
                }
            } else {
                throw 'Incorrect id string [' + idString + '] for non-empty context. Context = ' + context;
            }
        } else {
            if (idString === 'find_or_new') { // this occurs, for example, when Cancel button has been pressed -- context is undefined. 'new' or '830' idString is applicable.
                throw 'Incorrect id string [' + idString + '] for empty context. Context = ' + context;
            }
        }
        return context;
    },

    /**
     * Returns URI-fashined identification key for the centre.
     */
    _centreKey: function (miType, saveAsName) {
        return miType + '/default' + saveAsName;
    },

    /**
     * The surrogate title of not yet known configuration. This is used during first time centre loading.
     */
    get UNDEFINED_CONFIG_TITLE() {
        return _UNDEFINED_CONFIG_TITLE;
    },

    /**
     * The surrogate title of centre 'link' configuration. This is used when link with centre parameters opens.
     */
    get LINK_CONFIG_TITLE() {
        return _LINK_CONFIG_TITLE;
    }

});