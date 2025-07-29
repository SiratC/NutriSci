package org.Handlers.Logic;
import org.Entity.Food;
import org.Entity.Meal;
import org.Entity.NutrientStats;
import org.Enums.NutrientType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NutrientAnalyzer implements Analyzer<List<Meal>, NutrientStats> {

    private final DatabaseNutrientLookup lookup = new DatabaseNutrientLookup();

    @Override
    public NutrientStats analyze(List<Meal> meals) {

        Map<NutrientType, Double> nutrientSums = new HashMap<>();
        int totalItems = 0;

        for (Meal meal : meals) {
            for (Food food : meal.getItems()) {
                totalItems++;

                Map<NutrientType, Double> foodNutrients = lookup.getPerUnit(food.getFoodID());
                if (foodNutrients == null || foodNutrients.isEmpty()) {
                    foodNutrients = food.getNutrients();
                }

                for (Map.Entry<NutrientType, Double> entry : foodNutrients.entrySet()) {
                    nutrientSums.merge(entry.getKey(), (entry.getValue() / 100.0) * food.getQuantity(), Double::sum);
                }


                for (Map.Entry<NutrientType, Double> entry : foodNutrients.entrySet()) {
                    double adjusted = (entry.getValue() / 100.0) * food.getQuantity();
                    nutrientSums.merge(entry.getKey(), adjusted, Double::sum);
                }
            }
        }

        NutrientStats.NutrientStatsTemplate template = new NutrientStats.Top3Template();
        NutrientStats stats = template.calculateStats(nutrientSums);
        stats.setTotalItems(totalItems);

        return stats;
    }
}