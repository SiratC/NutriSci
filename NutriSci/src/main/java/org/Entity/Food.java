package org.Entity;

/**
 * Represents a food entity based on the name and quantity (in grams/servings).
 */
public class Food {
    private String name;
    private double quantity;

    /**
     * Defines a food item with a given name and quantity of food.
     *
     * @param name the name of the food item.
     * @param quantity the amount of the food item.
     */
    public Food(String name, double quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Returns the food's name.
     *
     * @return name of food
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
     * Sets the name of the food.
     *
     * @param name the new name for the food item
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the amount of food in grams or servings.
     *
     * @param quantity the new amount of food
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}