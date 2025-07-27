package org.Handlers.Visual;

import org.Entity.NutrientStats;
import org.Entity.TrendResult;
import org.Enums.NutrientType;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.time.LocalDate;
import java.util.Map;

public class TrendChartFactory {

    public static JFreeChart createTrendLineChart(TrendResult trendResult) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<LocalDate, NutrientStats> perDayStats = trendResult.getPerDayStats();

        for (Map.Entry<LocalDate, NutrientStats> entry : perDayStats.entrySet()) {
            LocalDate date = entry.getKey();
            NutrientStats stats = entry.getValue();

            for (Map.Entry<NutrientType, Double> nutrientEntry : stats.getNutrientPercentages().entrySet()) {
                dataset.addValue(nutrientEntry.getValue(), nutrientEntry.getKey().name(), date.toString());
            }
        }

        return ChartFactory.createLineChart(
                "Daily Nutrient Intake Trends",
                "Date",
                "Amount",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    public static JFreeChart createTrendBarChart(TrendResult trendResult) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<LocalDate, NutrientStats> perDayStats = trendResult.getPerDayStats();

        for (Map.Entry<LocalDate, NutrientStats> entry : perDayStats.entrySet()) {
            LocalDate date = entry.getKey();
            NutrientStats stats = entry.getValue();

            for (Map.Entry<NutrientType, Double> nutrientEntry : stats.getNutrientPercentages().entrySet()) {
                dataset.addValue(nutrientEntry.getValue(), nutrientEntry.getKey().name(), date.toString());
            }
        }

        return ChartFactory.createBarChart("Daily Nutrient Intake (Bar Chart)", "Date", "Amount", dataset, PlotOrientation.VERTICAL, true, true, false);
    }
}
