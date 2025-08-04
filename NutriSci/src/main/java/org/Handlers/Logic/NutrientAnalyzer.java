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
        System.out.println("[NutrientAnalyzer] Starting analysis with " + meals.size() + " meals");

        Map<NutrientType, Double> nutrientSums = new HashMap<>();
        int totalItems = 0;

        for (Meal meal : meals) {
            System.out.println("[NutrientAnalyzer] Processing meal with " + meal.getItems().size() + " items");
            for (Food food : meal.getItems()) {
                totalItems++;
                System.out.println("[NutrientAnalyzer] Processing food: " + food.getFoodID() + ", quantity: " + food.getQuantity());

                Map<NutrientType, Double> foodNutrients = lookup.getPerUnit(food.getFoodID());
                if (foodNutrients == null || foodNutrients.isEmpty()) {
                    foodNutrients = food.getNutrients();
                    System.out.println("[NutrientAnalyzer] Using fallback nutrients from food object");
                } else {
                    System.out.println("[NutrientAnalyzer] Using database nutrients: " + foodNutrients.size() + " nutrients");
                }

                for (Map.Entry<NutrientType, Double> entry : foodNutrients.entrySet()) {
                    double adjusted = (entry.getValue() / 100.0) * food.getQuantity();
                    nutrientSums.merge(entry.getKey(), adjusted, Double::sum);
                    System.out.println("[NutrientAnalyzer] Added " + entry.getKey().name() + ": " + adjusted + " (running total: " + nutrientSums.get(entry.getKey()) + ")");
                }
            }
        }

        System.out.println("[NutrientAnalyzer] Final nutrient sums: " + nutrientSums.size() + " different nutrients");
        System.out.println("[NutrientAnalyzer] Total items processed: " + totalItems);

        NutrientStats.NutrientStatsTemplate template = new NutrientStats.Top3Template();
        NutrientStats stats = template.calculateStats(nutrientSums);
        stats.setTotalItems(totalItems);
        
        System.out.println("[NutrientAnalyzer] Stats created: " + (stats != null ? "SUCCESS" : "NULL"));
        if (stats != null) {
            System.out.println("[NutrientAnalyzer] Top nutrients: " + stats.getTopNutrients());
            System.out.println("[NutrientAnalyzer] Nutrient percentages size: " + stats.getNutrientPercentages().size());
        }

        return stats;
    }
}