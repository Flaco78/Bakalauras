export async function geocodeAddress(address) {
    const query = `${address}, Lietuva`;
    const url = `https://nominatim.openstreetmap.org/search?format=json&countrycodes=lt&limit=1&q=${encodeURIComponent(query)}`;

    try {
        const res = await fetch(url);
        const data = await res.json();
        if (data && data.length > 0) {
            return {
                lat: parseFloat(data[0].lat),
                lng: parseFloat(data[0].lon)
            };
        }
        return null;
    } catch (error) {
        console.error("Geocoding error:", error);
        return null;
    }
}