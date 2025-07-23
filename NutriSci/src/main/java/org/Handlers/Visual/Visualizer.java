package org.Handlers.Visual;
import org.Entity.Meal;
import org.Entity.NutrientChangeStats;
import org.Entity.NutrientStats;
import org.Entity.VisualizationOps;
import org.Enums.NutrientType;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;

public class Visualizer {

    /**
     * generates a ChartPanel of a Pie Chart of the given data.
     *
     * @param data  labels -> values
     * @param title chart title
     */
    public static ChartPanel createPieChartPanel(Map<String, Double> data, String title) {

        DefaultPieDataset dataset = new DefaultPieDataset();

        data.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        return new ChartPanel(chart);
    }

    public static ChartPanel createBarChartPanel(Map<String, Map<String, Double>> data, String title) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Map<String, Double>> dateEntry : data.entrySet()) {

            String date = dateEntry.getKey();
            for (Map.Entry<String, Double> nutrientEntry : dateEntry.getValue().entrySet()) {

                dataset.addValue(nutrientEntry.getValue(), nutrientEntry.getKey(), date);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(title, "Date", "Value", dataset, PlotOrientation.VERTICAL, true, true, false);

        return new ChartPanel(chart);
    }

    public static ChartPanel createLineChartPanel(Map<String, Map<String, Double>> chartData, String title) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Map<String, Double>> dateEntry : chartData.entrySet()) {

            String date = dateEntry.getKey();
            for (Map.Entry<String, Double> nutrientEntry : dateEntry.getValue().entrySet()) {

                dataset.addValue(nutrientEntry.getValue(), nutrientEntry.getKey(), date);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                title,
                "Date",
                "Amount",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        return new ChartPanel(chart);
    }


    /**
     * converts NutrientStats into a simple chart format.
     *
     * @param stats   NutrientStats with raw nutrient %
     * @param options Visualization options (filter options)
     * @return map of nutrient name -> value (based on options)
     */
    public Map<String, Double> convertToChartData(NutrientStats stats, VisualizationOps options) {

        Map<String, Double> chartData = new HashMap<>();

        for (NutrientType nutrient : stats.getTopNutrients()) {
            Double value = stats.getNutrientPercentages().get(nutrient);

            if (value != null && value > 0) {

                chartData.put(nutrient.name(), value);

            }
        }

        // include other in chart only if it's over 0
        if (stats.getOtherPercentage() > 0) {
            chartData.put("Other", stats.getOtherPercentage());

        }

        return chartData;
    }

    /**
     * convertion of NutrientChangeStats into nutrient chart data
     * good for showing changes over time
     * @param changeStats nutrient changes over time
     * @param options visualization filters
     * @return a map of each date with key and value is another map of nutrient -> value
     */

    public Map<String, Map<String, Double>> convertToChartData(NutrientChangeStats changeStats, VisualizationOps options) {

        Map<String, Map<String, Double>> chartData = new HashMap<>();

        for (Map.Entry<LocalDate, Map<NutrientType, Double>> dayEntry : changeStats.getChangesOverTime().entrySet()) {

            String dateStr = dayEntry.getKey().toString();

            Map<String, Double> nutrients = new HashMap<>();

            for (Map.Entry<NutrientType, Double> nutrientEntry : dayEntry.getValue().entrySet()) {

                double value = nutrientEntry.getValue();

                if (value > 0) {
                    nutrients.put(nutrientEntry.getKey().name(), value);
                }
            }

            chartData.put(dateStr, nutrients);
        }

        return chartData;
    }


    /**
     * generates a pie chart comparing before and after data.
     * @param data map of nutrient -> value
     * @param title chart title
     * @return a JFreeChart pie chart
     */
    public static JFreeChart createComparisonChart(Map<String, Map<String, Double>> data, String title) {
        // default for now
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (data.containsKey("Before")) {

            data.get("Before").forEach((k, v) -> dataset.setValue("Before - " + k, v));
        }
        if (data.containsKey("After")) {

            data.get("After").forEach((k, v) -> dataset.setValue("After - " + k, v));
        }

        return ChartFactory.createPieChart(title, dataset, true, true, false);
    }

        public void update(String action, UUID userId, List<Meal> meals) {
            
        //  temp stub; actual visualizer update logic later
        System.out.println("[Visualizer] update triggered: " + action + " for user " + userId);
    }



}
