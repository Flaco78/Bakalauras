package org.example.backend.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeoLocationService {
    private final Map<String, double[]> cache = new HashMap<>();
    public double[] getCoordinatesFromAddress(String address) throws Exception {
        if (cache.containsKey(address)) return cache.get(address);

        if (address == null || address.isBlank())
            throw new Exception("Adresas yra tuščias arba nenurodytas.");

        if (!address.toLowerCase().contains("lietuva")) {
            address += ", Lietuva";
        }

        String apiKey = "6eb32634ce30463d85c81391d6d8b55f";
        String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String urlString = "https://api.opencagedata.com/geocode/v1/json?q=" + encoded
                + "&key=" + apiKey + "&language=lt&countrycode=lt&limit=1";

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) response.append(line);
        reader.close();

        try {
            JsonObject root = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject location = root.getAsJsonArray("results").get(0).getAsJsonObject()
                    .getAsJsonObject("geometry");

            double[] coords = new double[2];
            coords[0] = location.get("lat").getAsDouble();
            coords[1] = location.get("lng").getAsDouble();


            System.out.println("Coordinates for '" + address + "': " + coords[0] + ", " + coords[1]);

            cache.put(address, coords);
            return coords;
        } catch (Exception e) {
            throw new Exception("Nepavyko rasti koordinatų su OpenCage.");
        }
    }
}
