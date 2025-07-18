package org.Entity;
import org.jfree.chart.JFreeChart;
import java.util.List;

public class IntakeReport extends Report {
    private NutrientStats nutrientStats;
    private List<NutrientStats> perMealStats;
    private JFreeChart chart;
    private ProgressStatus progress;

    public IntakeReport(NutrientStats nutrientStats, List<NutrientStats> perMealStats, ProgressStatus progress, JFreeChart chart) {
        this.nutrientStats = nutrientStats;
        this.perMealStats = perMealStats;
        this.progress = progress;
        this.chart = chart;
    }

    public NutrientStats getNutrientStats() {
        return nutrientStats;
    }

    public List<NutrientStats> getPerMealStats() {
        return perMealStats;
    }

    public ProgressStatus getProgress() {
        return progress;
    }

    public JFreeChart getChart() {
        return chart;
    }
}
