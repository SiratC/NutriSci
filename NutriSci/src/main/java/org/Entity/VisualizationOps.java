package org.Entity;

import org.Enums.ChartType;
import org.Enums.NutrientType;

import java.util.List;


/**
 * Defines visualization of charts (Pie, Bar, Line, Plate) regarding nutrients.
 */
public class VisualizationOps {

    private DateRange dateRange;
    private List<NutrientType> nutrients;
    private int topCount;
    private boolean includeOther;
    private ChartType chartType;
    private boolean beforeAfter;

    /**
     * Creates an instance of visualization with given information.
     * @param dateRange range of time
     * @param nutrients nutrients of chart
     * @param topCount highest nutrients
     * @param includeOther other nutrients
     * @param chartType the type of chart
     * @param beforeAfter the swap differences
     */
    public VisualizationOps(DateRange dateRange, List<NutrientType> nutrients, int topCount, boolean includeOther, ChartType chartType, boolean beforeAfter) {
        this.dateRange = dateRange;
        this.nutrients = nutrients;
        this.topCount = topCount;
        this.includeOther = includeOther;
        this.chartType = chartType;
        this.beforeAfter = beforeAfter;
    }

    /**
     * Returns range of time.
     * @return date range
     */
    public DateRange getDateRange() {
        return dateRange;
    }

    /**
     * Returns list of nutrients in visualization.
     * @return nutrient list
     */
    public List<NutrientType> getNutrients() {
        return nutrients;
    }

    /**
     * Returns top nutrients in chart.
     * @return top nutrients
     */
    public int getTopCount() {
        return topCount;
    }

    /**
     * Returns other nutrients in chart.
     * @return other nutrients
     */
    public boolean isIncludeOther() {
        return includeOther;
    }

    /**
     * Returns chart type.
     * @return chart type
     */
    public ChartType getChartType() {
        return chartType;
    }

    /**
     * Returns the visual difference of the swap item.
     * @return swap change
     */
    public boolean isBeforeAfter() {
        return beforeAfter;
    }

    /**
     * Sets the range of time.
     * @param dateRange time range
     */
    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    /**
     * Sets the nutrients in the visualization.
     * @param nutrients nutrients shown
     */
    public void setNutrients(List<NutrientType> nutrients) {
        this.nutrients = nutrients;
    }

    /**
     * Sets the top nutrients.
     * @param topCount top nutrients
     */
    public void setTopCount(int topCount) {
        this.topCount = topCount;
    }

    /**
     * Sets the other nutrients.
     * @param includeOther other nutrients
     */
    public void setIncludeOther(boolean includeOther) {
        this.includeOther = includeOther;
    }

    /**
     * Sets the chart type.
     * @param chartType chart type
     */
    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    /**
     * Sets the swap information.
     * @param beforeAfter swap change
     */
    public void setBeforeAfter(boolean beforeAfter) {
        this.beforeAfter = beforeAfter;
    }
}
