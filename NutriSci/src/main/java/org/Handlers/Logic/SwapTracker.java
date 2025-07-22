package org.Handlers.Logic;

import org.Entity.*;
import org.Enums.NutrientType;

import java.time.LocalDate;
import java.util.*;

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

    public CFGDifference analyzeCFG(FoodGroupStats before, FoodGroupStats after) {

        return new CFGDifference(before, after);
    }
}
