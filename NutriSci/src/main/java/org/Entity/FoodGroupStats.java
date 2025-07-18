package org.Entity;

import org.Enums.FoodGroup;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class FoodGroupStats {
    private final Map<FoodGroup, Double> groupPercentages = new EnumMap<>(FoodGroup.class);

    public void setGroupPercentages(Map<FoodGroup, Double> percentages) {
        groupPercentages.clear();
        if (percentages != null) {
            groupPercentages.putAll(percentages);
        }
    }

    public Map<FoodGroup, Double> getGroupPercentages() {
        return Collections.unmodifiableMap(groupPercentages);
    }

    public double getPercentage(FoodGroup group) {
        return groupPercentages.getOrDefault(group, 0.0);
    }

    public void clear() {
        groupPercentages.clear();
    }
    @Override //debug
    public String toString() {

        return "FoodGroupStats [" + "groupPercentages=" + groupPercentages + ']';
    }
}
