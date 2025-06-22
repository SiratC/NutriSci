package org.Handlers.Logic;

import org.Entity.Meal;
import org.Entity.TrendResult;

import java.util.List;

/**
 * Implements {@link Analyzer} and handles the nutrient intake trends over time.
 */
public class TrendAnalyzer implements Analyzer<List<Meal>, TrendResult> {
    /**
     * Calculates the nutrients intake over time.
     *
     * @param meals the list of meals
     * @return the nutrient trends
     */
    @Override
    public TrendResult analyze(List<Meal> meals) {

        return new TrendResult();
    }
}
