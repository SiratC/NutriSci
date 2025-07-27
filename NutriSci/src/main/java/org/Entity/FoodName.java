package org.Entity;

public class FoodName {
    private final int foodId;
    private final String foodDescription;
    private final int caloriesPer100g;

    public FoodName(int foodId, String foodDescription, int caloriesPer100g) {
        this.foodId = foodId;
        this.foodDescription = foodDescription;
        this.caloriesPer100g = caloriesPer100g;
    }

    public int getFoodId() {
        return foodId;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public int getCaloriesPer100g() {
        return caloriesPer100g;
    }

    @Override
    public String toString() {

        return foodDescription;
    }
}
