package org.Entity;

import org.Enums.NutrientType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void setStats(Map<NutrientType, Double> totalMap) {
        this.nutrientPercentages = totalMap;
        this.totalItems = totalMap.size();

        List<Map.Entry<NutrientType, Double>> sorted = new ArrayList<>(totalMap.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        this.topNutrients = new ArrayList<>();

        double topSum = 0.0;
        double totalSum = 0.0;
        int count = 0;

        for (Map.Entry<NutrientType, Double> entry : sorted) {
            double value = entry.getValue();
            totalSum += value;
            if (count < 3) {
                topNutrients.add(entry.getKey());
                topSum += value;
                count++;
            }
        }

        this.otherPercentage = totalSum - topSum;
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
}


