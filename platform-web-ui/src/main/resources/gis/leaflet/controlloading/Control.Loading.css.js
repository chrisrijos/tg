export default String.raw`
.leaflet-control-loading {
    /* This is where your loading indicator would go */
    background: url("resources/gis/leaflet/controlloading/images/loading.gif"); /* was images/loading.gif */
}

.leaflet-control-loading,
.leaflet-control-zoom a.leaflet-control-loading ,
.leaflet-control-zoomslider a.leaflet-control-loading {
    display: none;
}

.leaflet-control-loading.is-loading,
.leaflet-control-zoom a.leaflet-control-loading.is-loading,
.leaflet-control-zoomslider a.leaflet-control-loading.is-loading  {
    display: block;
}

/* Necessary for display consistency in Leaflet >= 0.6 */
.leaflet-bar-part-bottom {
    border-bottom: medium none;
    border-bottom-left-radius: 4px;
    border-bottom-right-radius: 4px;
}
`;