package org.Handlers.Logic;

import org.Entity.*;
import org.Enums.NutrientType;

import java.time.LocalDate;
import java.util.*;

/**
 * Implements {@link Analyzer} and handles the nutrient changes due to a swap.
 */
public class SwapTracker implements Analyzer<List<Meal>, NutrientChangeStats> {

    private final NutrientCalculator calculator = new NutrientCalculator(new DatabaseNutrientLookup());

    @Override
    public NutrientChangeStats analyze(List<Meal> meals) {

        NutrientChangeStats changeStats = new NutrientChangeStats();

        // grouping meals by date
        Map<LocalDate, List<Meal>> mealsByDate = new HashMap<>();

        for (Meal meal : meals) {
            mealsByDate.computeIfAbsent(meal.getDate(), d -> new ArrayList<>()).add(meal);
        }

        // calculation of daily total nutrients
        for (Map.Entry<LocalDate, List<Meal>> entry : mealsByDate.entrySet()) {

            LocalDate date = entry.getKey();
            List<Meal> dayMeals = entry.getValue();

            Map<NutrientType, Double> dailyTotal = new EnumMap<>(NutrientType.class);

            for (Meal meal : dayMeals) {
                Map<NutrientType, Double> mealValues = calculator.calculate(meal);
                for (Map.Entry<NutrientType, Double> nutEntry : mealValues.entrySet()) {
                    dailyTotal.merge(nutEntry.getKey(), nutEntry.getValue(), Double::sum);
                }
            }

            for (Map.Entry<NutrientType, Double> nutEntry : dailyTotal.entrySet()) {
                changeStats.addChange(date, nutEntry.getKey(), nutEntry.getValue());
            }
        }

        return changeStats;
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

    /**
     * Updates the tracker based on action.
     *
     * @param action action causing change
     * @param userId user's ID
     * @param meals meal items
     */
    public void update(String action, UUID userId, List<Meal> meals) {
        
        // implement specific logic here later
        System.out.println("[SwapTracker] update triggered: " + action + " for user " + userId);
    }
}


