package org.Handlers.Logic;

import org.Entity.FoodGroupStats;
import org.Entity.Meal;

import java.util.List;

/**
 * Implements {@link Analyzer} and handles the percentage of the food groups in the meal.
 */
public class FoodGroupAnalyzer implements Analyzer<List<Meal>, FoodGroupStats> {
    /**
     * Analyzes the meals by the food groups.
     *
     * @param meals the list of food items given
     * @return the percentages of food groups based on the meal
     */
    @Override
    public FoodGroupStats analyze(List<Meal> meals) {

        return new FoodGroupStats();
    }
}
