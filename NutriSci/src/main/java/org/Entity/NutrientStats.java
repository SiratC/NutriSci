package org.Entity;

import org.Enums.NutrientType;
import java.util.*;

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
    public NutrientStats() {}

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
     * Sets the nutrient percentage.
     * @param nutrientPercentages nutrient percentage
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
     * Sets the percentage of nutrients.
     *
     * @param topNutrients the percentage of the nutrients specified
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

    @Override
    public String toString() {
        return "NutrientStats [" +
                "totalItems=" + totalItems +
                ", topNutrients=" + topNutrients +
                ", otherPercentage=" + otherPercentage +
                ", nutrientPercentages=" + nutrientPercentages +
                ']';
    }

    /**
     * Template class for nutrient stats.
     */
    public static abstract class NutrientStatsTemplate {
        /**
         * Calculates the stats based on type and map.
         * @param totalMap the nutrient map
         * @return nutrient stats
         */
        public NutrientStats calculateStats(Map<NutrientType, Double> totalMap) {
            NutrientStats stats = new NutrientStats();
            stats.setTotalItems(totalMap.size());
            stats.setNutrientPercentages(totalMap);

            List<Map.Entry<NutrientType, Double>> sorted = new ArrayList<>(totalMap.entrySet());
            sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

            List<NutrientType> topNutrients = new ArrayList<>();
            double topSum = 0.0;
            double totalSum = 0.0;
            int count = 0;

            for (Map.Entry<NutrientType, Double> entry : sorted) {
                double value = entry.getValue();
                totalSum += value;
                if (count++ < getTopLimit()) {
                    topNutrients.add(entry.getKey());
                    topSum += value;
                }
            }

            stats.setTopNutrients(topNutrients);
            stats.setOtherPercentage(totalSum - topSum);
            return stats;
        }

        protected abstract int getTopLimit();
    }

    /**
     * Gets the top 3 templates.
     */
    public static class Top3Template extends NutrientStatsTemplate {
        @Override
        protected int getTopLimit() {
            return 3;
        }
    }

    /**
     * Gets the top 5 templates.
     */
    public static class Top5Template extends NutrientStatsTemplate {
        @Override
        protected int getTopLimit() {
            return 5;
        }
    }

    /**
     * Gets the top 10 templates.
     */
    public static class Top10Template extends NutrientStatsTemplate {
        @Override
        protected int getTopLimit() {
            return 10;
        }
    }
}



