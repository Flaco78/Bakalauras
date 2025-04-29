package org.example.backend.service;

import org.example.backend.model.Activity;
import org.example.backend.enums.ActivityCategory;
import org.example.backend.model.ChildProfile;
import org.example.backend.enums.DeliveryMethod;

import java.util.ArrayList;
import java.util.List;

public class VectorBuilder {

    private VectorBuilder() {
        throw new UnsupportedOperationException("Utility class, cannot instantiate.");
    }

    public static List<Integer> buildActivityVector(Activity activity) {
        List<Integer> vector = new ArrayList<>();

        // 1. Kategorijos (one-hot)
        for (ActivityCategory cat : ActivityCategory.values()) {
            vector.add(activity.getCategory() == cat ? 1 : 0);
        }

        // 2. Delivery method
        vector.add(activity.getDeliveryMethod() == DeliveryMethod.ONLINE ? 1 : 0);
        vector.add(activity.getDeliveryMethod() == DeliveryMethod.ONSITE ? 1 : 0);

        // 3. Trukmė
        if (activity.getDurationMinutes() < 30) {
            vector.add(1); vector.add(0); vector.add(0);
        } else if (activity.getDurationMinutes() <= 60) {
            vector.add(0); vector.add(1); vector.add(0);
        } else {
            vector.add(0); vector.add(0); vector.add(1);
        }


        return vector;
    }

    public static List<Integer> buildChildVector(ChildProfile child) {
        List<Integer> vector = new ArrayList<>();

        // 1. Interests
        for (ActivityCategory cat : ActivityCategory.values()) {
            vector.add(child.getInterests().contains(cat) ? 1 : 0);
        }

        // 2. Delivery method preferences
        vector.add(child.getPreferredDeliveryMethod() == DeliveryMethod.ONLINE ? 1 : 0);
        vector.add(child.getPreferredDeliveryMethod() == DeliveryMethod.ONSITE ? 1 : 0);

        // 3. Laiko limitas (vaikas turi tik tas veiklas, kurios trunka ne daugiau nei maxActivityDuration)
        vector.add(child.getMaxActivityDuration() <= 30 ? 1 : 0);  // Trumpa veikla
        vector.add(child.getMaxActivityDuration() <= 60 ? 1 : 0);  // Vidutinė veikla
        vector.add(child.getMaxActivityDuration() > 60 ? 1 : 0);   // Ilga veikla


        return vector;
    }

    // Panašumo skaičiavimas
    public static double cosineSimilarity(List<Integer> vec1, List<Integer> vec2) {
        int dot = 0;
        double normA = 0;
        double normB = 0;

        for (int i = 0; i < vec1.size(); i++) {
            dot += vec1.get(i) * vec2.get(i);
            normA += Math.pow(vec1.get(i), 2);
            normB += Math.pow(vec2.get(i), 2);
        }

        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }


}
