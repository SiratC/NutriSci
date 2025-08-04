package org.Handlers.Logic;

import org.Dao.MealLogDAO;
import org.Entity.Meal;
import org.Entity.NutrientStats;
import org.Entity.TrendResult;
import org.Enums.NutrientType;
import org.Handlers.Database.DatabaseMealLogDAO;

import java.time.LocalDate;
import java.util.*;

public class TrendAnalyzer implements Analyzer<List<Meal>, TrendResult> {

    private final NutrientCalculator calculator = new NutrientCalculator(new DatabaseNutrientLookup());
    private final MealLogDAO mealLogDAO;

    public TrendAnalyzer() {
        this.mealLogDAO = new DatabaseMealLogDAO();
    }

    @Override
    public TrendResult analyze(List<Meal> meals) {
        Map<LocalDate, List<Meal>> byDate = new HashMap<>();
        Map<LocalDate, NutrientStats> perDayStats = new TreeMap<>();
        Map<NutrientType, Double> totalMap = new EnumMap<>(NutrientType.class);

        NutrientStats.NutrientStatsTemplate template = new NutrientStats.Top3Template();


        for (Meal meal : meals) {
            byDate.computeIfAbsent(meal.getDate(), d -> new ArrayList<>()).add(meal);
        }


        for (Map.Entry<LocalDate, List<Meal>> entry : byDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Meal> dayMeals = entry.getValue();

            Map<NutrientType, Double> dailyMap = new EnumMap<>(NutrientType.class);

            for (Meal meal : dayMeals) {
                Map<NutrientType, Double> mealMap = calculator.calculateWithFallback(meal);

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
        result.setPerMealStats(new ArrayList<>(perDayStats.values()));
        result.setCumulativeStats(template.calculateStats(totalMap));

        return result;
    }


    public TrendResult analyzeForDateRange(UUID userId, LocalDate startDate, LocalDate endDate) {
        try {
            List<Meal> meals = mealLogDAO.getMealsByDateRange(userId, startDate.toString(), endDate.toString());
            return analyze(meals);
        } catch (Exception e) {
            System.err.println("Error in TrendAnalyzer: " + e.getMessage());
            return new TrendResult();
        }
    }


    public void update(String action, UUID userId, List<Meal> meals) {
        System.out.println("[TrendAnalyzer] update triggered: " + action + " for user " + userId);
    }
}
