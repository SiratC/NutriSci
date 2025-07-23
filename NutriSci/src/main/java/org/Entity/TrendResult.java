package org.Entity;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Manages the information of nutrients per meal and the cumulative nutrients of meals.
 */
public class TrendResult {

    private List<NutrientStats> perMealStats;
    private NutrientStats cumulativeStats;
    private Map<LocalDate, NutrientStats> perDayStats;

    /**
     * Returns the per meal stats.
     * @return stats per meal
     */
    public List<NutrientStats> getPerMealStats() {

        return perMealStats;
    }

    /**
     * Sets the per meal stats.
     * @param perMealStats stats per meal
     */
    public void setPerMealStats(List<NutrientStats> perMealStats) {

        this.perMealStats = perMealStats;
    }

    /**
     * Returns the stats of nutrients overall.
     * @return nutrient stats
     */
    public NutrientStats getCumulativeStats() {

        return cumulativeStats;
    }

    /**
     * Sets the overall nutrients
     * @param cumulativeStats nutrient stats
     */
    public void setCumulativeStats(NutrientStats cumulativeStats) {

        this.cumulativeStats = cumulativeStats;
    }

    /**
     * Returns the per day stats.
     * @return meal per day stats
     */
    public Map<LocalDate, NutrientStats> getPerDayStats() {
        return perDayStats;
    }

    /**
     * Sets the per day stats.
     * @param perDayStats meal per day stats
     */
    public void setPerDayStats(Map<LocalDate, NutrientStats> perDayStats) {
        this.perDayStats = perDayStats;
    }

    //debug
    @Override
    public String toString() {

        return "TrendResult [" + "perMealStats=" + perMealStats + ", cumulativeStats=" + cumulativeStats + ", perDayStats=" + perDayStats + ']';
    }

}
