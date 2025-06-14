package org.Handlers.Logic;


import org.Entity.Meal;
import org.Entity.NutrientStats;

import java.util.List;

public class NutrientAnalyzer implements Analyzer<List<Meal>, NutrientStats> {
    @Override
    public NutrientStats analyze(List<Meal> meals) {
        // Example: count total items as a placeholder metric
        int totalItems = 0;
        for (Meal m : meals) {
            totalItems += m.getItems().size();
        }
        NutrientStats stats = new NutrientStats();
        stats.setTotalItems(totalItems);

        return stats;
    }
}

