package org.Entity;

import org.Enums.FoodGroup;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Stores the percentages given by CFG categories of the meal.
 */
public class FoodGroupStats {
    private final Map<FoodGroup, Double> groupPercentages = new EnumMap<>(FoodGroup.class);

    /**
     * Sets the percentages of CFG categories for the meal
     * @param percentages cfg category percentages
     */
    public void setGroupPercentages(Map<FoodGroup, Double> percentages) {
        groupPercentages.clear();
        if (percentages != null) {
            groupPercentages.putAll(percentages);
        }
    }

    /**
     * Returns the CFG percentages for the meal.
     * @return map of cfg percentages
     */
    public Map<FoodGroup, Double> getGroupPercentages() {
        return Collections.unmodifiableMap(groupPercentages);
    }

    /**
     * Returns a specific group's percentage for the meal.
     * @param group the food group specified
     * @return percentage of food group
     */
    public double getPercentage(FoodGroup group) {
        return groupPercentages.getOrDefault(group, 0.0);
    }

    /**
     * Clears the group percentages.
     */
    public void clear() {
        groupPercentages.clear();
    }
    @Override //debug
    public String toString() {

        return "FoodGroupStats [" + "groupPercentages=" + groupPercentages + ']';
    }
}
