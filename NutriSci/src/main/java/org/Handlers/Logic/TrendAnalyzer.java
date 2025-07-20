package org.Handlers.Logic;

import org.Entity.*;
import org.Enums.NutrientType;
import java.time.LocalDate;
import java.util.*;

public class TrendAnalyzer implements Analyzer<List<Meal>, TrendResult> {

    private final NutrientCalculator calculator = new NutrientCalculator(new InMemNutrientLookUp());

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
        List<NutrientStats> perMealStats = new ArrayList<>(perDayStats.values());

        NutrientStats cumulative = template.calculateStats(totalMap);
        result.setPerMealStats(perMealStats);
        result.setCumulativeStats(cumulative);

        return result;
    }
}
