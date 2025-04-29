import React from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { Icon } from 'leaflet';
import { useMap } from 'react-leaflet';
import { useEffect } from 'react';

const defaultCoords = [54.6872, 25.2797];

// Paprasta markerio ikona (Leaflet default)
const markerIcon = new Icon({
    iconUrl: 'https://unpkg.com/leaflet@1.9.3/dist/images/marker-icon.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
});


const userMarkerIcon = new Icon({
    iconUrl: 'https://static-00.iconduck.com/assets.00/map-marker-icon-342x512-gd1hf1rz.png', // gali pasirinkti kitą
    iconSize: [25, 41],
    iconAnchor: [12, 41],
});

const MapComponent = ({ locations, center, userCoords, hoveredActivity }) => {
    // Jei nėra vietų, grąžinti null
    if (locations.length === 0) return null;

    // Patikriname, ar hoverinamoji veikla yra, ir ją rodyti
    const hoverCoords = hoveredActivity ? hoveredActivity.coordinates : center || defaultCoords;

    const RecenterMap = ({ center }) => {
        const map = useMap();

        useEffect(() => {
            if (center) {
                map.setView(center, map.getZoom(), {
                    animate: true
                });
            }
        }, [center]);

        return null;
    };

    return (
        <MapContainer
            center={hoverCoords}
            zoom={13}
            scrollWheelZoom={false}
            style={{ height: '100%', width: '100%' }}
        >
            <RecenterMap center={hoverCoords} />
            <TileLayer
                attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a>'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            {locations.map(loc => (
                <Marker key={loc.id} position={loc.coordinates} icon={markerIcon}>
                    <Popup>
                        <strong>{loc.title}</strong><br />
                        {loc.address}
                    </Popup>
                </Marker>
            ))}
            {userCoords && (
                <Marker position={userCoords.coordinates} icon={userMarkerIcon}>
                    <Popup>
                        <strong>{userCoords.title}</strong><br />
                        {userCoords.address}
                    </Popup>
                </Marker>
            )}
        </MapContainer>
    );
};

export default MapComponent;