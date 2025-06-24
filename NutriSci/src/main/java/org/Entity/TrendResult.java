package org.Entity;

import java.util.List;

/**
 * Manages the information of nutrients per meal and the cumulative nutrients of meals.
 */
public class TrendResult {
    private List<NutrientStats> perMealStats;
    private NutrientStats cumulativeStats;
}
