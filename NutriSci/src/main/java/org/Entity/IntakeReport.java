package org.Entity;
import org.jfree.chart.JFreeChart;
import java.util.List;

/**
 * A concrete implementation of Report for nutrients.
 * <p>Handles the nutrient statistics of the meal,</p>
 * <p>and creates a chart based on the information alongside the progress made by the user.</p>
 */
public class IntakeReport extends Report {
    private NutrientStats nutrientStats;
    private List<NutrientStats> perMealStats;
    private JFreeChart chart;
    private ProgressStatus progress;

    /**
     * Creates an existing report using given details.
     * @param nutrientStats given nutrient stats
     * @param perMealStats given meal stats
     * @param progress given progress to goal
     * @param chart given chart of nutrients
     */
    public IntakeReport(NutrientStats nutrientStats, List<NutrientStats> perMealStats, ProgressStatus progress, JFreeChart chart) {
        this.nutrientStats = nutrientStats;
        this.perMealStats = perMealStats;
        this.progress = progress;
        this.chart = chart;
    }

    /**
     * Returns the nutrient stats.
     * @return nutrient stats
     */
    public NutrientStats getNutrientStats() {
        return nutrientStats;
    }

    /**
     * Returns the per meal stats.
     * @return list of nutrient stats
     */
    public List<NutrientStats> getPerMealStats() {
        return perMealStats;
    }

    /**
     * Returns the progress to goal
     * @return progress
     */
    public ProgressStatus getProgress() {
        return progress;
    }

    /**
     * Returns the chart of the report.
     * @return chart
     */
    public JFreeChart getChart() {
        return chart;
    }
}
