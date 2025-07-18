package org.Entity;

import org.Enums.NutrientType;

import java.util.HashMap;
import java.util.Map;

public class Food {
    private String name;
    private double quantity; // grams or mL
    private double calories;

    public Food(String name, double quantity, double calories) {
        this.name = name;
        this.quantity = quantity;
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getCalories() {
        return calories;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    @Override
    public String toString() {
        return name + " (" + quantity + "g): " + calories + " cal";
    }

    // added getNutrients
    public Map<NutrientType, Double> getNutrients() {
        Map<NutrientType, Double> nutrients = new HashMap<>();

        // dummy sample nutrient data per food item for now
        nutrients.put(NutrientType.Protein, calories * 0.25);
        nutrients.put(NutrientType.Carbohydrate, calories * 0.50);
        nutrients.put(NutrientType.Fat, calories * 0.20);
        nutrients.put(NutrientType.Fiber, calories * 0.05);

        return nutrients;
    }
}
