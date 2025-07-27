package org.Handlers.Logic;

import org.Entity.Food;
import org.Entity.FoodGroupStats;
import org.Entity.Meal;
import org.Enums.FoodGroup;

import java.util.*;

public class FoodGroupAnalyzer implements Analyzer<List<Meal>, FoodGroupStats> {

    @Override
    public FoodGroupStats analyze(List<Meal> meals) {

        Map<FoodGroup, Double> groupSums = new EnumMap<>(FoodGroup.class);

        double totalCalories = 0.0;

        for (Meal meal : meals) {

            for (Food food : meal.getItems()) {

                FoodGroup group = guessGroup(food);

                double cal = food.getCalories();
                groupSums.merge(group, cal, Double::sum);
                totalCalories += cal;
            }
        }

        Map<FoodGroup, Double> percentages = new EnumMap<>(FoodGroup.class);

        for (var entry : groupSums.entrySet()) {

            double percent = (totalCalories > 0) ? (entry.getValue() / totalCalories) * 100.0 : 0.0;

            percentages.put(entry.getKey(), percent);
        }

        FoodGroupStats stats = new FoodGroupStats();
        stats.setGroupPercentages(percentages);
        return stats;
    }

    private FoodGroup guessGroup(Food food) {

        String name = food.getName().toLowerCase();

        if (name.contains("bread") || name.contains("rice") || name.contains("oat"))
            return FoodGroup.Grains;

        if (name.contains("apple") || name.contains("banana") || name.contains("berry"))
            return FoodGroup.Fruit;

        if (name.contains("chicken") || name.contains("beef") || name.contains("tofu"))
            return FoodGroup.Protein;

        if (name.contains("milk") || name.contains("cheese") || name.contains("yogurt"))
            return FoodGroup.Dairy;

        if (name.contains("carrot") || name.contains("broccoli") || name.contains("spinach"))
            return FoodGroup.Vegetables;

        return FoodGroup.Other;
    }
}
