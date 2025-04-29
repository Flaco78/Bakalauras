package org.example.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GeoLocationServiceTest {

    private GeoLocationService geoLocationService;

    @BeforeEach
    void setUp() {
        geoLocationService = new GeoLocationService();
    }

    @Test
    void testGetCoordinatesFromAddress_realApi() throws Exception {
        double[] coords = geoLocationService.getCoordinatesFromAddress("Vilnius, Lietuva");
        assertNotNull(coords);
        assertEquals(2, coords.length);
        assertTrue(coords[0] > 54 && coords[0] < 56);
        assertTrue(coords[1] > 23 && coords[1] < 26);
    }

    @Test
    void testCacheWorks() throws Exception {
        Field cacheField = GeoLocationService.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, double[]> cache = (Map<String, double[]>) cacheField.get(geoLocationService);

        String address = "Test Address, Lietuva";
        double[] coords = new double[]{1.0, 2.0};
        cache.put(address, coords);

        double[] result = geoLocationService.getCoordinatesFromAddress(address);
        assertArrayEquals(coords, result, "Should return cached coordinates");
    }

    @Test
    void testThrowsOnBlankAddress() {
        Exception ex = assertThrows(Exception.class, () -> geoLocationService.getCoordinatesFromAddress("   "));
        assertTrue(ex.getMessage().toLowerCase().contains("tuščias") || ex.getMessage().toLowerCase().contains("nenurodytas"));
    }
}