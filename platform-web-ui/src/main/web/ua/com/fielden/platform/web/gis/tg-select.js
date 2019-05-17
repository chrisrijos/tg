import { L } from '/resources/gis/leaflet/leaflet-lib.js';

export const Select = function (_map, _getLayerById, _markerFactory, tgMap) {
    const self = this;

    self._map = _map;
    self._getLayerById = _getLayerById;
    self._markerFactory = _markerFactory;

    self._featureToLeafletIds = {};
    self._prevId = null;
    self._tgMap = tgMap;

    self._centreSelectionHandler = function (newSelection) {
        if (newSelection.entities.length == 1) {
            const selectionEntity = newSelection.entities[0];
            if (selectionEntity.select) {
                self._silentlySelectById(selectionEntity.entity.properties.layerId);
            } else {
                self._silentlyDeselectById(selectionEntity.entity.properties.layerId)
            }
        }
    };
    self._tgMap.centreSelectionHandler = self._centreSelectionHandler;
};

Select.prototype.select = function (layerId) {
    if (this._prevId !== layerId) {
        const details = [];
        const prevId = this._prevId;
        //selecting entity.
        this._silentlySelect(layerId);
        this._prevId = layerId;
        details.push({
            entity: this._getLayerById(layerId).feature,
            select: true
        });
        if (prevId !== null) {
            this._silentlyDeselect(prevId);
            details.push({
                entity: this._getLayerById(prevId).feature,
                select: false
            });
        }
        if (details.length > 0) {
            const event = new CustomEvent("tg-entity-selected", {
                detail: {
                    shouldScrollToSelected: true,
                    entities: details
                },
                bubbles: true,
                cancelable: true
            });
            this._tgMap.getCustomEventTarget().dispatchEvent(event);
        }
    }
}

Select.prototype._deselect = function (layerId) {
    const layer = this._getLayerById(layerId);
    const feature = layer.feature;

    const event = new CustomEvent("tg-entity-selected", {
        detail: {
            shouldScrollToSelected: true,
            entities: [{
                entity: feature,
                select: false
            }]
        },
        bubbles: true,
        cancelable: true
    });
    this._tgMap.getCustomEventTarget().dispatchEvent(event);
}

Select.prototype._silentlySelectById = function (layerId) {
    if (this._prevId !== layerId) {
        const prevId = this._prevId;
        this._silentlySelect(layerId);
        this._prevId = layerId;

        if (prevId !== null) { // at the moment of selecting the feature - there has been other previously selected feature (or, perhaps, the same) 
            this._deselect(prevId);
        }
    }
}

Select.prototype._silentlyDeselectById = function (layerId) {
    if (this._prevId === layerId) {
        this._prevId = null;
    }
    this._silentlyDeselect(layerId);
}

Select.prototype._silentlyDeselect = function (layerId) {
    const layer = this._getLayerById(layerId);
    if (layer instanceof this._markerFactory.ArrowMarker || layer instanceof this._markerFactory.CircleMarker) {
        layer.setSelected(false);
    }
}

Select.prototype._silentlySelect = function (layerId) {
    const layer = this._getLayerById(layerId);
    if (layer instanceof this._markerFactory.ArrowMarker || layer instanceof this._markerFactory.CircleMarker) {
        this._map.panTo(layer.getLatLng()); // centering of the marker (fitToBounds is not needed)
        layer.setSelected(true);
    } else if (layer instanceof L.Polygon) {
        this._map.fitBounds(layer.getBounds());
        // map.panTo(layer.getBounds().getCenter()); // fitToBounds?
    }
}