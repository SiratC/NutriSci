package org.Entity;

import org.Enums.NutrientType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Food {

    private int foodID;
    private String name;
    private double quantity; // grams or mL
    private double calories;
    // private Map<NutrientType, Double> nutrients = new HashMap<>();
    private Map<NutrientType, Double> nutrients = new EnumMap<>(NutrientType.class);

    public Food(int foodID, String name, double quantity, double calories) {
        this.foodID = foodID;
        this.name = name;
        this.quantity = quantity;
        this.calories = calories;
    }

    public int getFoodID() {
        return foodID;
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

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setNutrients(Map<NutrientType, Double> nutrients) {
        this.nutrients = nutrients != null ? new EnumMap<>(nutrients) : new EnumMap<>(NutrientType.class);
    }

    public Map<NutrientType, Double> getRawNutrients() {
        return nutrients;
    }

    public Map<NutrientType, Double> getNutrients() {
        return nutrients != null ? nutrients : new EnumMap<>(NutrientType.class);

    }

    public Map<NutrientType, Double> getEstimatedNutrients() {
        Map<NutrientType, Double> dummy = new EnumMap<>(NutrientType.class);
        double cal = calories > 0 ? calories : 100.0;

        dummy.put(NutrientType.Protein, cal * 0.25 / 4);
        dummy.put(NutrientType.Carbohydrate, cal * 0.50 / 4);
        dummy.put(NutrientType.Fat, cal * 0.20 / 9);
        dummy.put(NutrientType.Fiber, cal * 0.05 / 2);
        return dummy;
    }

    @Override
    public String toString() {
        return name + " (" + quantity + "g): " + String.format("%.2f", calories) + " cal";
    }
}
