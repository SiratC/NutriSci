package org.Handlers.Logic;

import org.Enums.NutrientType;

public class NutrientTypeMapper {
    public int mapNutrientTypeToId(NutrientType type) {
        return switch (type) {
            case Protein -> 203;
            case Carbohydrate -> 205;
            case Fat -> 204;
            case Fiber -> 291;
            case Calories -> 208;
            default -> 9999;
        };
    }

    public NutrientType mapIdToNutrientType(int id) {
        return switch (id) {
            case 203 -> NutrientType.Protein;
            case 205 -> NutrientType.Carbohydrate;
            case 204 -> NutrientType.Fat;
            case 291 -> NutrientType.Fiber;
            case 208 -> NutrientType.Calories; // Technically not a nutrient but considered as such for swaps
            default -> null;
        };
    }
}
