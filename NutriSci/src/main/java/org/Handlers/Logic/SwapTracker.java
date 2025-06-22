package org.Handlers.Logic;

import org.Entity.CFGDifference;
import org.Entity.FoodGroupStats;
import org.Entity.Meal;
import org.Entity.NutrientChangeStats;

import java.util.List;

/**
 * Implements {@link Analyzer} and handles the nutrient changes due to a swap.
 */
public class SwapTracker implements Analyzer<List<Meal>, NutrientChangeStats> {
    /**
     * Calculates the change in nutrients due to a swap between items.
     *
     * @param meals the list of food items
     * @return the nutrient difference before and after swaps
     */
    @Override
    public NutrientChangeStats analyze(List<Meal> meals) {

        return new NutrientChangeStats();
    }

    /**
     * Calculates the change in CFG adherence before and after the swap.
     *
     * @param before the food group stats of the meal before the swap
     * @param after the food group stats of the meal after the swap
     * @return the difference in CFG score between the meals
     */
    public CFGDifference analyzeCFG(FoodGroupStats before, FoodGroupStats after) {
        return new CFGDifference(before, after);
    }
}

