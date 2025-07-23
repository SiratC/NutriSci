package org.Handlers.Logic;
import org.Entity.*;
import org.Enums.NutrientType;
import java.time.LocalDate;
import java.util.*;

/**
 * Implements {@link Analyzer} and handles the nutrient intake trends over time.
 */
public class TrendAnalyzer implements Analyzer<List<Meal>, TrendResult> {

    private final NutrientCalculator calculator = new NutrientCalculator(new DatabaseNutrientLookup());

    @Override
    public TrendResult analyze(List<Meal> meals) {

        Map<LocalDate, NutrientStats> perDayStats = new TreeMap<>();
        Map<NutrientType, Double> totalMap = new EnumMap<>(NutrientType.class);
        Map<LocalDate, List<Meal>> byDate = new HashMap<>();

        for (Meal meal : meals) {
            byDate.computeIfAbsent(meal.getDate(), d -> new ArrayList<>()).add(meal);
        }

        NutrientStats.NutrientStatsTemplate template = new NutrientStats.Top3Template();

        for (Map.Entry<LocalDate, List<Meal>> entry : byDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Meal> dayMeals = entry.getValue();

            Map<NutrientType, Double> dailyMap = new EnumMap<>(NutrientType.class);

            for (Meal meal : dayMeals) {
                Map<NutrientType, Double> mealMap = calculator.calculate(meal);
                for (Map.Entry<NutrientType, Double> m : mealMap.entrySet()) {
                    dailyMap.merge(m.getKey(), m.getValue(), Double::sum);
                    totalMap.merge(m.getKey(), m.getValue(), Double::sum);
                }
            }

            NutrientStats dailyStats = template.calculateStats(dailyMap);
            perDayStats.put(date, dailyStats);
        }

        TrendResult result = new TrendResult();
        result.setPerDayStats(perDayStats);

        List<NutrientStats> perMealStats = new ArrayList<>(perDayStats.values());
        result.setPerMealStats(perMealStats);

        NutrientStats cumulative = template.calculateStats(totalMap);
        result.setCumulativeStats(cumulative);

        return result;
    }

    /**
     * Updates the trend analyzer due to action.
     *
     * @param action action causing change
     * @param userId user's ID
     * @param meals meal items
     */
    public void update(String action, UUID userId, List<Meal> meals) {

        // stub for observer compatibility
        System.out.println("[TrendAnalyzer] update triggered: " + action + " for user " + userId);
    }
}

