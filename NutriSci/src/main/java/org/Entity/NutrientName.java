package org.Entity;

public class NutrientName {
    private final int nutrientNameId;
    private final String nutrientName;
    private final String unit;

    public NutrientName(int nutrientNameId,
                        String nutrientName,
                        String unit) {
        this.nutrientNameId = nutrientNameId;
        this.nutrientName = nutrientName;
        this.unit = unit;
    }

    public int getNutrientNameId() { return nutrientNameId; }
    public String getUnit() { return unit; }
    public String getNutrientName() { return nutrientName; }
}

