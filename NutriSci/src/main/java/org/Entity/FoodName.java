package org.Entity;

public class FoodName {
    private final int foodId;
    private final String foodDescription;

    public FoodName(int foodId, String foodDescription) {
        this.foodId = foodId;
        this.foodDescription = foodDescription;
    }
    public int getFoodId() { return foodId; }
    public String getFoodDescription() { return foodDescription; }

    @Override
    public String toString() {

        return foodDescription;
    }
}
