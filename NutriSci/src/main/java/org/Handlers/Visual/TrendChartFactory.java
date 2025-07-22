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

    public static JFreeChart createTrendChart(TrendResult trendResult) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<LocalDate, NutrientStats> perDayStats = trendResult.getPerDayStats();

        for (Map.Entry<LocalDate, NutrientStats> entry : perDayStats.entrySet()) {

            LocalDate date = entry.getKey();
            NutrientStats stats = entry.getValue();

            for (Map.Entry<NutrientType, Double> nutrientEntry : stats.getNutrientPercentages().entrySet()) {

                NutrientType type = nutrientEntry.getKey();
                double value = nutrientEntry.getValue();

                if (value > 0.0) {

                    dataset.addValue(value, type.toString(), date.toString());

                    System.out.printf("Plotting %s on %s: %.2f%n", type, date, value);
                }
            }
        }

        return ChartFactory.createBarChart("Daily Nutrient Intake Trends", "Date", "Amount", dataset, PlotOrientation.VERTICAL,
                true, true, false);
    }
}
