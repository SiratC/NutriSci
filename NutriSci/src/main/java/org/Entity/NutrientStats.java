package org.Entity;

import org.Enums.NutrientType;

import java.util.List;
import java.util.Map;

public class NutrientStats {
    private int totalItems;
    private Map<NutrientType, Double> nutrientPercentages;
    private List<NutrientType> topNutrients;
    private double otherPercentage;

    public NutrientStats() {

    }

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
}
