import React, { useEffect, useState } from 'react';
import 'ol/ol.css'; // Import OpenLayers CSS
import { Map, View } from 'ol'; // Correctly import OpenLayers components
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';
import { fromLonLat } from 'ol/proj';
import Feature from 'ol/Feature'; // Import Feature
import Point from 'ol/geom/Point'; // Import Point
import VectorSource from 'ol/source/Vector'; // Import VectorSource
import VectorLayer from 'ol/layer/Vector'; // Import VectorLayer

const OpenLayersMap = ({ locations, center }) => {
    const [map, setMap] = useState(null);

    useEffect(() => {
        if (!map) {
            // Create the map instance
            const olMap = new Map({
                target: 'openlayers-map',  // The div id where map will be rendered
                layers: [
                    new TileLayer({
                        source: new OSM()
                    })
                ],
                view: new View({
                    center: fromLonLat([25.2797, 54.6872]),  // Default center (Vilnius)
                    zoom: 13
                })
            });
            setMap(olMap);
        }
    }, [map]);

    useEffect(() => {
        if (map && center) {
            map.getView().setCenter(fromLonLat([center.lng, center.lat]));
        }
    }, [center, map]);

    // Optionally, render markers
    const addMarkers = () => {
        if (locations && map) {
            locations.forEach(location => {
                const marker = new Feature({
                    geometry: new Point(fromLonLat([location.lng, location.lat])),
                });

                const vectorSource = new VectorSource({
                    features: [marker],
                });

                const markerLayer = new VectorLayer({
                    source: vectorSource,
                });

                map.addLayer(markerLayer);
            });
        }
    };

    useEffect(() => {
        addMarkers();
    }, [locations, map]);

    return <div id="openlayers-map" style={{ width: '100%', height: '100%' }} />;
};

export default OpenLayersMap;