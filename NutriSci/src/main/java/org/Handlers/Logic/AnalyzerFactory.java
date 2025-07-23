package org.Handlers.Logic;

import java.util.List;
import org.Entity.*;
import org.Handlers.Logic.*;
import org.Enums.CFGVersion;

/**
 * Factory method for analyzer related classes.
 */
public class AnalyzerFactory {
    /**
     * Creates a new Trend Analyzer.
     * @return trend analyzer instance
     */
    public Analyzer<List<Meal>, TrendResult> createTrendAnalyzer() {
        return new TrendAnalyzer();
    }

    /**
     * Creates a new Food Group Analyzer.
     * @return food group analyzer instance
     */
    public Analyzer<List<Meal>, FoodGroupStats> createFoodGroupAnalyzer() {
        return new FoodGroupAnalyzer();
    }

    /**
     * Creates a new Nutrient Analyzer.
     * @return nutrient analyzer instance
     */
    public Analyzer<List<Meal>, NutrientStats> createNutrientAnalyzer() {
        return new NutrientAnalyzer();
    }

    /**
     * Creates a new Swap Tracker.
     * @return swap tracker instance
     */
    public SwapTracker createSwapTracker() {
        return new SwapTracker();
    }

    /**
     * Creates a new CFG Comparer.
     * @return cfg comparer instance
     */
    public CFGComparer createCFGComparer() {
        return new CFGComparer();
    }
}
