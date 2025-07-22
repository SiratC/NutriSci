package org.Entity;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TrendResult {

    private List<NutrientStats> perMealStats;
    private NutrientStats cumulativeStats;
    private Map<LocalDate, NutrientStats> perDayStats;

    public List<NutrientStats> getPerMealStats() {

        return perMealStats;
    }

    public void setPerMealStats(List<NutrientStats> perMealStats) {

        this.perMealStats = perMealStats;
    }

    public NutrientStats getCumulativeStats() {

        return cumulativeStats;
    }

    public void setCumulativeStats(NutrientStats cumulativeStats) {

        this.cumulativeStats = cumulativeStats;
    }
    public Map<LocalDate, NutrientStats> getPerDayStats() {
        return perDayStats;
    }

    public void setPerDayStats(Map<LocalDate, NutrientStats> perDayStats) {
        this.perDayStats = perDayStats;
    }

    //debug
    @Override
    public String toString() {

        return "TrendResult [" + "perMealStats=" + perMealStats + ", cumulativeStats=" + cumulativeStats + ", perDayStats=" + perDayStats + ']';
    }

}
