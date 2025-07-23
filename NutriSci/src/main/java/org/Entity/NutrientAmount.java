package org.Entity;

import java.math.BigDecimal;

/**
 * Holds the information of nutrients and the amount.
 */
public class NutrientAmount {
    private final int foodId;
    private final int nutrientNameId;
    private final BigDecimal nutrientValue;

    /**
     * Creates a NutrientAmount with specified IDs and value.
     * @param foodId food ID
     * @param nutrientNameId nutrient ID
     * @param nutrientValue nutrient value
     */
    public NutrientAmount(int foodId,
                          int nutrientNameId,
                          BigDecimal nutrientValue) {
        this.foodId = foodId;
        this.nutrientNameId = nutrientNameId;
        this.nutrientValue = nutrientValue;
    }

    /**
     * Returns food ID.
     * @return food ID
     */
    public int getFoodId() { return foodId; }

    /**
     * Returns nutrient ID.
     * @return nutrient name ID
     */
    public int getNutrientNameId() { return nutrientNameId; }

    /**
     * Returns nutrient value.
     * @return nutrient value
     */
    public BigDecimal getNutrientValue() { return nutrientValue; }
}
