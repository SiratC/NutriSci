package org.Handlers.Logic;

import org.Entity.Food;
import org.Entity.FoodGroupStats;
import org.Entity.Meal;
import org.Enums.FoodGroup;
import org.Enums.NutrientType;

import java.util.*;

public class FoodGroupAnalyzer implements Analyzer<List<Meal>, FoodGroupStats> {

    private final DatabaseNutrientLookup lookup = new DatabaseNutrientLookup();

    @Override
    public FoodGroupStats analyze(List<Meal> meals) {

        Map<FoodGroup, Double> groupSums = new EnumMap<>(FoodGroup.class);
        double totalCalories = 0.0;

        for (Meal meal : meals) {
            for (Food food : meal.getItems()) {
                FoodGroup group = guessGroup(food);
                double cal = estimateCalories(food);

                System.out.println("→ " + food.getName() + " nutrients: " + food.getNutrients());
                System.out.println("→ Estimated cals: " + cal);

                groupSums.merge(group, cal, Double::sum);
                totalCalories += cal;
            }
        }

        Map<FoodGroup, Double> percentages = new EnumMap<>(FoodGroup.class);
        for (var entry : groupSums.entrySet()) {
            double percent = (totalCalories > 0) ? (entry.getValue() / totalCalories) * 100.0 : 0.0;
            percentages.put(entry.getKey(), percent);
        }

        System.out.println("[FoodGroupAnalyzer] Final percentages: " + percentages);

        FoodGroupStats stats = new FoodGroupStats();
        stats.setGroupPercentages(percentages);
        return stats;
    }


    private double estimateCalories(Food food) {
        Map<NutrientType, Double> nutrients = food.getRawNutrients(); // NEW: avoid getNutrients()

        if (nutrients == null || nutrients.isEmpty()) {
            System.out.println("→ Empty in-memory nutrients for: " + food.getName());

            nutrients = lookup.getPerUnit(food.getFoodID()); // DB fetch
            if (nutrients != null && !nutrients.isEmpty()) {
                System.out.println("→ DB nutrients used for " + food.getName() + ": " + nutrients);
                food.setNutrients(nutrients); // cache
            } else {
                nutrients = food.getEstimatedNutrients(); // fallback from calorie
                System.out.println("→ Estimated fallback nutrients: " + nutrients);
            }
        }

        // Check if there's enough to estimate calories
        double protein = nutrients.getOrDefault(NutrientType.Protein, 0.0);
        double carbs   = nutrients.getOrDefault(NutrientType.Carbohydrate, 0.0);
        double fat     = nutrients.getOrDefault(NutrientType.Fat, 0.0);

        if (protein == 0.0 && carbs == 0.0 && fat == 0.0) {
            System.out.println("→ No macro data, fallback to stored calories: " + food.getCalories());
            return food.getCalories();
        }

        double estimated = (4 * protein + 4 * carbs + 9 * fat) * (food.getQuantity() / 100.0);
        System.out.println("→ Estimated cals for " + food.getName() + ": " + estimated);

        return estimated;
    }





    private FoodGroup guessGroup(Food food) {
        String name = food.getName().toLowerCase();

        if (name.contains("bread") || name.contains("rice") || name.contains("quinoa") || name.contains("grain") || name.contains("wheat"))
            return FoodGroup.Grains;

        if (name.contains("apple") || name.contains("banana") || name.contains("fruit"))
            return FoodGroup.Fruit;

        if (name.contains("chicken") || name.contains("beef") || name.contains("tofu")
            || name.contains("fish") || name.contains("salmon")
            || name.contains("nuts") || name.contains("almond"))
            return FoodGroup.Protein;

        if (name.contains("milk") || name.contains("cheese") || name.contains("yogurt"))
            return FoodGroup.Dairy;

        if (name.contains("spinach") || name.contains("broccoli") || name.contains("carrot") || name.contains("vegetable"))
            return FoodGroup.Vegetables;

        return FoodGroup.Other;
    }
}


