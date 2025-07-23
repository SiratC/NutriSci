package org.Entity;

/**
 * Holds information of nutrient name alongside other food item information.
 */
public class NutrientName {
    private final int nutrientNameId;
    private final String nutrientName;
    private final String unit;

    /**
     * Creates a NutrientName with specified information.
     * @param nutrientNameId nutrient ID
     * @param nutrientName nutrient name
     * @param unit unit of measurement
     */
    public NutrientName(int nutrientNameId,
                        String nutrientName,
                        String unit) {
        this.nutrientNameId = nutrientNameId;
        this.nutrientName = nutrientName;
        this.unit = unit;
    }

    /**
     * Returns nutrient ID.
     * @return nutrient ID
     */
    public int getNutrientNameId() { return nutrientNameId; }

    /**
     * Returns unit of measurement
     * @return unit
     */
    public String getUnit() { return unit; }

    /**
     * Returns name of nutrient.
     * @return nutrient name
     */
    public String getNutrientName() { return nutrientName; }
}

