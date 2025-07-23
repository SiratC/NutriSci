package org.Entity;

import org.Enums.NutrientType;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a food entity based on the name and quantity (in grams/servings).
 */
public class Food {

    private int foodID;
    private String name;
    private double quantity; // grams or mL
    private double calories;

    /**
     * Defines a food item with a given name and quantity of food.
     *
     * @param name name of the food item
     * @param calories calories of the food item
     * @param foodID food item's ID
     * @param quantity amount of the food item
     */
    public Food(int foodID, String name, double quantity, double calories) {

        this.foodID = foodID;
        this.name = name;
        this.quantity = quantity;
        this.calories = calories;
    }

    /**
     * Returns the food's name.
     *
     * @return name of food
     */
    public int getFoodID(){

        return foodID;
    }

    /**
     * Returns the amount of food.
     *
     * @return number of food items
     */
    public String getName() {

        return name;
    }

    /**
     * Returns the amount of food.
     *
     * @return number of food items
     */
    public double getQuantity() {

        return quantity;
    }

    /**
     * Returns the calories of the food.
     *
     * @return calories of food item
     */
    public double getCalories() {

        return calories;
    }

    /**
     * Sets the name of the food.
     *
     * @param name the new name for the food item
     */
    public void setName(String name) {

        this.name = name;
    }

    /**
     * Sets the ID of the food.
     *
     * @param foodID the new food ID
     */
    public void setFoodID(int foodID) {

        this.foodID = foodID;

    }

    /**
     * Sets the amount of food in grams or servings.
     *
     * @param quantity the new amount of food
     */
    public void setQuantity(double quantity) {

        this.quantity = quantity;
    }

    /**
     * Sets the amount of calories for the food item.
     * @param calories new calories of food item
     */
    public void setCalories(double calories) {

        this.calories = calories;
    }

    @Override
    public String toString() {
        return name + " (" + quantity + "g): " + calories + " cal";
    }

    /**
     * Returns a map of the nutrients in food item.
     *
     * @return nutrients of food item
     */
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
