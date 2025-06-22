package org.Entity;

import org.Enums.NutrientType;

import java.util.Map;

/**
 * Handles the swapping of items within a meal alongside the nutrient change due to the swap.
 */
public class SwapOption {
    private String originalItem;
    private String replacementItem;
    private Map<NutrientType, Double> nutrientDelta;
}
