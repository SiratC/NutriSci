package org.Handlers.Logic;

import org.Enums.CFGVersion;
import org.Enums.NutrientType;

/**
 * Handles nutrient recommendations based off of the CFG and current meal to reach user goals.
 */
public class NutritionGuidelines {
    /**
     * Returns nutrition guidelines based on given CFG version.
     *
     * @param version the version of the Canada's Food Guide
     * @return the NutritionGuidelines based on given CFG
     */
    public static NutritionGuidelines forVersion(CFGVersion version) {

        return new NutritionGuidelines();
    }

    /**
     * Returns the recommended percentage of nutrients for the meal.
     *
     * @param type the type of nutrient recommended
     * @return the percentage of the nutrient
     */
    public double getTargetPercentage(NutrientType type) {

        return 0;
    }
}