import { L } from '/resources/gis/leaflet/leaflet-lib.js';
import { esri } from '/resources/gis/leaflet/esri/esri-leaflet-lib.js';
import { googleMutant } from '/resources/gis/leaflet/providers/leaflet-google-maps-lib.js';

export const BaseLayers = function () {
    const self = this;

    const osmLink = '<a href="http://openstreetmap.org">OpenStreetMap</a>';
    const thunLink = '<a href="http://thunderforest.com/">Thunderforest</a>';

    const osmUrl = 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
    const osmAttrib = '&copy; ' + osmLink + ' Contributors';
    const landUrl = 'https://{s}.tile.thunderforest.com/landscape/{z}/{x}/{y}.png';
    const thunAttrib = '&copy; ' + osmLink + ' Contributors & ' + thunLink;
    
    const esriStreetsMap = esri.basemapLayer('Streets');

    // initialise different BaseLayers for different tile providers
    const osmMap = L.tileLayer(osmUrl, {
        maxZoom: 19,
        minZoom: 0,
        attribution: osmAttrib
    });
    const landMap = L.tileLayer(landUrl, {
        maxZoom: 18,
        minZoom: 0,
        attribution: thunAttrib
    });
    // var bingMap = new L.BingLayer("YOUR_BING_API_KEY"); -- need an API key to use it 
    // http://stackoverflow.com/questions/14442055/use-bing-maps-tiles-with-leaflet
    const googleRoadMap = googleMutant({
        maxZoom: 21,
        type:'roadmap'
    });
    const googleSatelliteMap = googleMutant({
        maxZoom: 19,
        type:'satellite'
    });
    const googleHybridMap = googleMutant({
        maxZoom: 19,
        type:'hybrid'
    });
    const googleTerrainMap = googleMutant({
        maxZoom: 15,
        type:'terrain'
    });

    /*const googleRoadMap = new Google('ROADMAP', {
        maxZoom: 21,
        minZoom: 0
    });
    const googleSatelliteMap = new Google('SATELLITE', {
        maxZoom: 19,
        minZoom: 0
    });
    const googleHybridMap = new Google('HYBRID', {
        maxZoom: 19,
        minZoom: 0
    });
    const googleTerrainMap = new Google('TERRAIN', {
        maxZoom: 15,
        minZoom: 0
    });*/
    self._baseLayers = {
        "Esri Streets": esriStreetsMap,
        "OpenStreetMap": osmMap,
        "Landscape": landMap,
        // "Bing": bingMap,	
        "Google Roadmap": googleRoadMap,
        "Google Sattelite": googleSatelliteMap,
        "Google Hybrid": googleHybridMap,
        "Google Terrain": googleTerrainMap
    };
};

/** Provides a base layer by its name. */
BaseLayers.prototype.getBaseLayer = function (name) {
    return this._baseLayers[name];
}

/** Returns all base layers as an object. */
BaseLayers.prototype.getBaseLayers = function () {
    return this._baseLayers;
}