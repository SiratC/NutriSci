package org.Entity;

import java.math.BigDecimal;

public class NutrientAmount {
    private final int foodId;
    private final int nutrientNameId;
    private final BigDecimal nutrientValue;

    public NutrientAmount(int foodId,
                          int nutrientNameId,
                          BigDecimal nutrientValue) {
        this.foodId = foodId;
        this.nutrientNameId = nutrientNameId;
        this.nutrientValue = nutrientValue;
    }
    public int getFoodId() { return foodId; }
    public int getNutrientNameId() { return nutrientNameId; }
    public BigDecimal getNutrientValue() { return nutrientValue; }
}
