package org.example.backend.service;

import org.springframework.stereotype.Component;

@Component
public class DistanceCalculator {

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {

        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);


        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;


        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));


        double EARTH_RADIUS = 6371;
        double distance = EARTH_RADIUS * c;

        // ✅ Debug output
        System.out.println("Calculating distance:");
        System.out.println("From: (" + lat1 + ", " + lon1 + ") → To: (" + lat2 + ", " + lon2 + ")");
        System.out.println("Distance: " + distance + " km");


        return distance;
    }
}
