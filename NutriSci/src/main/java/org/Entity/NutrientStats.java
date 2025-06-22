package org.Entity;

import org.Enums.NutrientType;

import java.util.List;
import java.util.Map;

/**
 * Handles the information of nutrients in a meal, alongside
 * <p>information of the food items providing the nutrients, statistics of the nutrients and nutrient percentages.</p>
 */
public class NutrientStats {
    private int totalItems;
    private Map<NutrientType, Double> nutrientPercentages;
    private List<NutrientType> topNutrients;
    private double otherPercentage;

    /**
     * Creates new instance for nutrient stats.
     */
    public NutrientStats() {

    }

    /**
     * Returns the total count of food items across meals.
     *
     * @return number of food items
     */
    public int getTotalItems() {
        return totalItems;
    }

    /**
     * Sets the total amount of items.
     *
     * @param totalItems the amount of items in the meals
     */
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * Returns the percentage of nutrients.
     *
     * @return percentage of nutrients
     */
    public Map<NutrientType, Double> getNutrientPercentages() {
        return nutrientPercentages;
    }

    /**
     * Sets the percentage of nutrients.
     *
     * @param nutrientPercentages the percentage of the nutrients specificd
     */
    public void setNutrientPercentages(Map<NutrientType, Double> nutrientPercentages) {
        this.nutrientPercentages = nutrientPercentages;
    }

    /**
     * Returns the top nutrients provided by the food items.
     *
     * @return the most prominent nutrients
     */
    public List<NutrientType> getTopNutrients() {
        return topNutrients;
    }

    /**
     * Sets the top nutrients of the food items.
     *
     * @param topNutrients the list of prominent nutrients
     */
    public void setTopNutrients(List<NutrientType> topNutrients) {
        this.topNutrients = topNutrients;
    }

    /**
     * Returns the other nutrients provided by the food items.
     *
     * @return percentage of other nutrients
     */
    public double getOtherPercentage() {
        return otherPercentage;
    }

    /**
     * Sets the percentage of other nutrients.
     *
     * @param otherPercentage the percentage of other nutrients in the meal
     */
    public void setOtherPercentage(double otherPercentage) {
        this.otherPercentage = otherPercentage;
    }
}
