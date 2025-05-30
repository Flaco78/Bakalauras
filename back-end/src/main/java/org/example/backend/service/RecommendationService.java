package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.ActivityDTO;
import org.example.backend.dto.RecommendedActivityDTO;
import org.example.backend.mapper.ActivityMapper;
import org.example.backend.model.Activity;
import org.example.backend.model.ChildProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final GeoLocationService geoLocationService;
    private final DistanceCalculator distanceCalculator;

    public List<RecommendedActivityDTO> recommendCloseActivities(ChildProfile child, List<Activity> allActivities) {
        List<RecommendedActivityDTO> recommendedActivities = new ArrayList<>();

        String childAddress = child.getParent() != null ? child.getParent().getAddress() : null;

        if (childAddress == null || childAddress.isBlank()) {
            System.out.println("❌ Child address is null or blank. Cannot calculate proximity.");
            return recommendedActivities;
        }

        for (Activity activity : allActivities) {
            String activityLocation = activity.getLocation();
            System.out.println("🔍 Checking activity: " + activity.getTitle() + " at location: " + activityLocation);

            if (activityLocation == null || activityLocation.isBlank()) {
                System.out.println("⛔ Skipped: Activity location is missing.");
                continue;
            }

            boolean validDeliveryMethod = activity.getDeliveryMethod() == child.getPreferredDeliveryMethod();
            if (!validDeliveryMethod) {
                System.out.println("⛔ Skipped: Delivery method doesn't match child's preference.");
            }

            double travelTime = getTravelTimeInMinutes(childAddress, activityLocation);

            if (travelTime == -1) {
                System.out.println("⚠️ Could not calculate travel time. Skipping activity.");
                continue;
            }

            double distance = getDistanceInKm(childAddress, activityLocation);

            boolean isReachable = travelTime <= child.getMaxActivityDuration();

            if (validDeliveryMethod && isReachable) {
                System.out.println("✅ Added: " + activity.getTitle() + " — Travel time: " + travelTime + " min");
                ActivityDTO dto = ActivityMapper.toDTO(activity);
                recommendedActivities.add(new RecommendedActivityDTO(dto, travelTime, distance));
            } else {
                System.out.println("❌ Skipped: " + activity.getTitle() + " — Travel time too long (" + travelTime + " min)");
            }
        }

        System.out.println("🎯 Total recommended close activities: " + recommendedActivities.size());

        return recommendedActivities;
    }

    public double getTravelTimeInMinutes(String startAddress, String destinationAddress) {
        try {
            double[] startCoordinates = geoLocationService.getCoordinatesFromAddress(startAddress);
            double[] destinationCoordinates = geoLocationService.getCoordinatesFromAddress(destinationAddress);

            double lat1 = startCoordinates[0];
            double lon1 = startCoordinates[1];
            double lat2 = destinationCoordinates[0];
            double lon2 = destinationCoordinates[1];

            System.out.println("Start coordinates for address '" + startAddress + "': " + lat1 + ", " + lon1);
            System.out.println("Destination coordinates for address '" + destinationAddress + "': " + lat2 + ", " + lon2);

            double distance = distanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);
            System.out.println("Calculated distance between addresses: " + distance + " km");

            double avgSpeedKmh = 30.0;
            double estimatedTimeMinutes = (distance / avgSpeedKmh) * 60;
            System.out.println("Estimated travel time: " + estimatedTimeMinutes + " minutes");

            return estimatedTimeMinutes;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public double getDistanceInKm(String startAddress, String destinationAddress) {
        try {
            double[] startCoordinates = geoLocationService.getCoordinatesFromAddress(startAddress);
            double[] destinationCoordinates = geoLocationService.getCoordinatesFromAddress(destinationAddress);

            double lat1 = startCoordinates[0];
            double lon1 = startCoordinates[1];
            double lat2 = destinationCoordinates[0];
            double lon2 = destinationCoordinates[1];

            return distanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
