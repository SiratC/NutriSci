package org.Handlers.Logic;

import org.Entity.Food;
import org.Entity.Meal;
import org.Entity.NutrientStats;
import org.Enums.NutrientType;
import java.util.*;

public class NutrientAnalyzer implements Analyzer<List<Meal>, NutrientStats> {
    @Override
    public NutrientStats analyze(List<Meal> meals) {
        Map<NutrientType, Double> nutrientSums = new HashMap<>();
        int totalItems = 0;

        for (Meal meal : meals) {
            for (Food food : meal.getItems()) {
                totalItems++;
                Map<NutrientType, Double> foodNutrients = food.getNutrients();

                if (foodNutrients != null) {
                    for (Map.Entry<NutrientType, Double> entry : foodNutrients.entrySet()) {
                        nutrientSums.merge(entry.getKey(), entry.getValue(), Double::sum);
                    }
                }
            }
        }

        NutrientStats stats = new NutrientStats();
        stats.setTotalItems(totalItems);
        stats.setStats(nutrientSums);

        return stats;
    }
}
