package org.Entity;

import org.Enums.ChartType;
import org.Enums.NutrientType;

import java.util.List;

public class VisualizationOps {

    private DateRange dateRange;
    private List<NutrientType> nutrients;
    private int topCount;
    private boolean includeOther;
    private ChartType chartType;
    private boolean beforeAfter;

    public VisualizationOps(DateRange dateRange, List<NutrientType> nutrients, int topCount, boolean includeOther, ChartType chartType, boolean beforeAfter) {
        this.dateRange = dateRange;
        this.nutrients = nutrients;
        this.topCount = topCount;
        this.includeOther = includeOther;
        this.chartType = chartType;
        this.beforeAfter = beforeAfter;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public List<NutrientType> getNutrients() {
        return nutrients;
    }

    public int getTopCount() {
        return topCount;
    }

    public boolean isIncludeOther() {
        return includeOther;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public boolean isBeforeAfter() {
        return beforeAfter;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public void setNutrients(List<NutrientType> nutrients) {
        this.nutrients = nutrients;
    }

    public void setTopCount(int topCount) {
        this.topCount = topCount;
    }

    public void setIncludeOther(boolean includeOther) {
        this.includeOther = includeOther;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public void setBeforeAfter(boolean beforeAfter) {
        this.beforeAfter = beforeAfter;
    }
}
