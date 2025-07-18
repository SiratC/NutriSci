package org.Entity;
import java.util.List;

public class TrendResult {

    private List<NutrientStats> perMealStats;
    private NutrientStats cumulativeStats;

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

    //debug
    @Override
    public String toString() {

        return "TrendResult [" + "perMealStats=" + perMealStats + ", cumulativeStats=" + cumulativeStats + ']';
    }

}
