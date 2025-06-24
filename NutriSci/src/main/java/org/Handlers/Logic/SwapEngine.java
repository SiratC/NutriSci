package org.Handlers.Logic;

import org.Entity.Meal;
import org.Entity.SwapRequest;

import java.util.List;

/**
 * Handles swaps within a meal.
 */
public class SwapEngine {
    /**
     * Processes the swap of a food item within a meal.
     *
     * @param meals the list of food items
     * @param request the food item to swap
     * @return the changed list of food items including the swap
     */
    public List<Meal> applySwap(List<Meal> meals, SwapRequest request) {
        // Dummy swap logic: return the same meals
        return meals;
    }
}

