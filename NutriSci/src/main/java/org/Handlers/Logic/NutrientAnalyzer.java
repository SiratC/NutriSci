package org.Handlers.Logic;


import org.Entity.Meal;
import org.Entity.NutrientStats;

import java.util.List;

/**
 * Implements {@link Analyzer} and handles the nutrient distribution of the meal.
 */
public class NutrientAnalyzer implements Analyzer<List<Meal>, NutrientStats> {
    /**
     * Analyzes the nutrients of the meal and returns the nutrient statistics.
     *
     * @param meals the list of food items
     * @return the nutrient statistics
     */
    @Override
    public NutrientStats analyze(List<Meal> meals) {

        int totalItems = 0;
        for (Meal m : meals) {
            totalItems += m.getItems().size();
        }
        NutrientStats stats = new NutrientStats();
        stats.setTotalItems(totalItems);

        return stats;
    }
}



