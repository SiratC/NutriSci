package org.Handlers.Logic;

import java.util.List;
import org.Entity.*;
import org.Handlers.Logic.*;
import org.Enums.CFGVersion;

public class AnalyzerFactory {

    public Analyzer<List<Meal>, TrendResult> createTrendAnalyzer() {
        return new TrendAnalyzer();
    }

    public Analyzer<List<Meal>, FoodGroupStats> createFoodGroupAnalyzer() {
        return new FoodGroupAnalyzer();
    }

    public Analyzer<List<Meal>, NutrientStats> createNutrientAnalyzer() {
        return new NutrientAnalyzer();
    }

    public SwapTracker createSwapTracker() {
        return new SwapTracker();
    }

    public CFGComparer createCFGComparer() {
        return new CFGComparer();
    }
}
