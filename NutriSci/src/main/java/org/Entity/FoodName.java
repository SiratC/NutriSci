package org.Entity;

/**
 * Specifies the food items details such as name, ID and description
 */
public class FoodName {
    private final int foodId;
    private final String foodDescription;

    /**
     * Creates a food name with existing ID and description.
     * @param foodId the food ID
     * @param foodDescription the food description
     */
    public FoodName(int foodId, String foodDescription) {
        this.foodId = foodId;
        this.foodDescription = foodDescription;
    }

    /**
     * Returns the food ID.
     * @return food ID
     */
    public int getFoodId() { return foodId; }

    /**
     * Returns the food description.
     * @return food description
     */
    public String getFoodDescription() { return foodDescription; }

    @Override
    public String toString() {

        return foodDescription;
    }
}
