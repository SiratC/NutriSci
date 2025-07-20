package org.Entity;

import org.Enums.NutrientType;
import java.util.*;

public class NutrientStats {
    private int totalItems;
    private Map<NutrientType, Double> nutrientPercentages;
    private List<NutrientType> topNutrients;
    private double otherPercentage;

    public NutrientStats() {}

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public Map<NutrientType, Double> getNutrientPercentages() {
        return nutrientPercentages;
    }

    public void setNutrientPercentages(Map<NutrientType, Double> nutrientPercentages) {
        this.nutrientPercentages = nutrientPercentages;
    }

    public List<NutrientType> getTopNutrients() {
        return topNutrients;
    }

    public void setTopNutrients(List<NutrientType> topNutrients) {
        this.topNutrients = topNutrients;
    }

    public double getOtherPercentage() {
        return otherPercentage;
    }

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

    public static abstract class NutrientStatsTemplate {
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

    public static class Top3Template extends NutrientStatsTemplate {
        @Override
        protected int getTopLimit() {
            return 3;
        }
    }

    public static class Top5Template extends NutrientStatsTemplate {
        @Override
        protected int getTopLimit() {
            return 5;
        }
    }

    public static class Top10Template extends NutrientStatsTemplate {
        @Override
        protected int getTopLimit() {
            return 10;
        }
    }
}



