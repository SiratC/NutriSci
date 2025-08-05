package org.Handlers.Visual;

import org.Entity.*;
import org.Enums.ChartType;
import org.Enums.NutrientType;
import org.Handlers.Logic.DatabaseNutrientLookup;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
public class Visualizer {

    /**
     * generates a ChartPanel of a Pie Chart of the given data.
     *
     * @param data  labels -> values
     * @param title chart title
     */
    public static ChartPanel createPieChartPanel(Map<String, Double> data, String title) {
        System.out.println("[Visualizer] Creating pie chart: " + title);
        System.out.println("[Visualizer] Input data size: " + data.size());
        
        DefaultPieDataset dataset = new DefaultPieDataset();
        int addedValues = 0;

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double val = entry.getValue();
            System.out.println("[Visualizer] Processing: " + entry.getKey() + " = " + val);
            
            // Reduce threshold and add validation
            if (val > 0.001) {  // Changed from 0.01 to 0.001
                dataset.setValue(entry.getKey(), val);
                addedValues++;
                System.out.println("[Visualizer] Added to dataset: " + entry.getKey() + " = " + val);
            } else {
                System.out.println("[Visualizer] Filtered out (too small): " + entry.getKey() + " = " + val);
            }
        }
        
        System.out.println("[Visualizer] Total values added to dataset: " + addedValues);
        
        if (addedValues == 0) {
            System.out.println("[Visualizer] WARNING: No data added to pie chart dataset!");
            // Add a placeholder to prevent empty chart
            dataset.setValue("No Data", 1.0);
        }

        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        return new ChartPanel(chart);
    }

    /**
     * creates a bar chart from a simple key-value map (nutrient -> amount).
     * used for NutrientAnalyzer, FoodGroupAnalyzer, and CFGComparer results.
     *
     * @param data
     * @param title
     * @return
     */

    public static ChartPanel createBarChartFromSimpleData(Map<String, Double> data, String title) {
        System.out.println("[Visualizer] Creating bar chart: " + title);
        System.out.println("[Visualizer] Input data size: " + data.size());

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int addedValues = 0;

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double val = entry.getValue();
            System.out.println("[Visualizer] Adding to bar chart: " + entry.getKey() + " = " + val);
            dataset.addValue(val, "Value", entry.getKey());
            addedValues++;
        }
        
        System.out.println("[Visualizer] Total values added to bar chart: " + addedValues);
        
        if (addedValues == 0) {
            System.out.println("[Visualizer] WARNING: No data added to bar chart dataset!");
            dataset.addValue(0, "No Data", "No Data");
        }
        
        JFreeChart chart = ChartFactory.createBarChart(title, "Nutrient", "Amount", dataset);
        return new ChartPanel(chart);
    }


    /**
     * creates a line chart from a simple key-value map.
     * no time axis used here.
     * @param data
     * @param title
     * @return
     */

    public static ChartPanel createLineChartFromSimpleData(Map<String, Double> data, String title) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Double> entry : data.entrySet()) {

            dataset.addValue(entry.getValue(), "Value", entry.getKey());
        }
        JFreeChart chart = ChartFactory.createLineChart(title, "Nutrient", "Amount", dataset);

        return new ChartPanel(chart);
    }


    /**
     * creates a bar chart from a time series of nutrient values.
     * each inner map represents nutrients for a specific date.
     * used for SwapTracker
     * @param data
     * @param title
     * @return
     */
    public static ChartPanel createBarChartFromTimeSeries(Map<String, Map<String, Double>> data, String title) {

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


    /**
     * creates a line chart from a time series of nutrient values.
     * each inner map represents nutrients for a specific date.
     * used for SwapTracker
     * @param chartData
     * @param title
     * @return
     */
    public static ChartPanel createLineChartFromTimeSeries(Map<String, Map<String, Double>> chartData, String title) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Map<String, Double>> dateEntry : chartData.entrySet()) {
            String date = dateEntry.getKey();

            for (Map.Entry<String, Double> nutrientEntry : dateEntry.getValue().entrySet()) {
                dataset.addValue(nutrientEntry.getValue(), nutrientEntry.getKey(), date);
            }
        }
        JFreeChart chart = ChartFactory.createLineChart(
                title, "Date", "Amount", dataset, PlotOrientation.VERTICAL, true, true, false
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
        System.out.println("[Visualizer] Converting NutrientStats to chart data");
        System.out.println("[Visualizer] Stats null check: " + (stats != null ? "NOT NULL" : "NULL"));
        
        if (stats == null) {
            return new HashMap<>();
        }

        Map<String, Double> chartData = new HashMap<>();
        
        System.out.println("[Visualizer] Top nutrients count: " + (stats.getTopNutrients() != null ? stats.getTopNutrients().size() : "NULL"));
        System.out.println("[Visualizer] Nutrient percentages: " + (stats.getNutrientPercentages() != null ? stats.getNutrientPercentages().size() : "NULL"));

        for (NutrientType nutrient : stats.getTopNutrients()) {
            Double value = stats.getNutrientPercentages().get(nutrient);
            System.out.println("[Visualizer] Processing nutrient: " + nutrient.name() + " = " + value);

            if (value != null && value > 0) {
                chartData.put(nutrient.name(), value);
                System.out.println("[Visualizer] Added to chart: " + nutrient.name() + " = " + value);
            }
        }

        // include other in chart only if it's over 0
        if (stats.getOtherPercentage() > 0) {
            chartData.put("Other", stats.getOtherPercentage());
            System.out.println("[Visualizer] Added Other: " + stats.getOtherPercentage());
        }
        
        System.out.println("[Visualizer] Final chart data size: " + chartData.size());
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
        System.out.println("[Visualizer] Converting NutrientChangeStats to chart data");
        System.out.println("[Visualizer] ChangeStats null check: " + (changeStats != null ? "NOT NULL" : "NULL"));
        
        if (changeStats == null) {
            return new HashMap<>();
        }

        Map<String, Map<String, Double>> chartData = new HashMap<>();
        
        System.out.println("[Visualizer] Changes over time entries: " + changeStats.getChangesOverTime().size());

        for (Map.Entry<LocalDate, Map<NutrientType, Double>> dayEntry : changeStats.getChangesOverTime().entrySet()) {

            String dateStr = dayEntry.getKey().toString();
            System.out.println("[Visualizer] Processing date: " + dateStr);

            Map<String, Double> nutrients = new HashMap<>();

            for (Map.Entry<NutrientType, Double> nutrientEntry : dayEntry.getValue().entrySet()) {

                double value = nutrientEntry.getValue();
                System.out.println("[Visualizer] Processing nutrient change: " + nutrientEntry.getKey().name() + " = " + value);

                if (value > 0) {
                    nutrients.put(nutrientEntry.getKey().name(), value);
                    System.out.println("[Visualizer] Added nutrient change: " + nutrientEntry.getKey().name() + " = " + value);
                }
            }

            chartData.put(dateStr, nutrients);
            System.out.println("[Visualizer] Added date entry with " + nutrients.size() + " nutrients");
        }
        
        System.out.println("[Visualizer] Final change chart data size: " + chartData.size());
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

    /**
     * Creates a single chart from a single dataset (for individual before/after display)
     * @param data map of nutrient -> value for single dataset
     * @param title chart title
     * @param chartType type of chart to create (PIE, BAR, LINE)
     * @return ChartPanel with the individual chart, or null if no data
     */
    public static ChartPanel createSingleChart(Map<String, Double> data, String title, ChartType chartType) {
        System.out.println("[Visualizer] Creating single chart: " + title + " (" + chartType + ")");
        System.out.println("[Visualizer] Input data size: " + (data != null ? data.size() : "NULL"));
        
        if (data == null || data.isEmpty()) {
            System.out.println("[Visualizer] No data provided, returning null");
            return null;
        }
        
        switch (chartType) {
            case PIE -> {
                System.out.println("[Visualizer] Creating individual PIE chart");
                return createPieChartPanel(data, title);
            }
            case BAR -> {
                System.out.println("[Visualizer] Creating individual BAR chart");
                return createBarChartFromSimpleData(data, title);
            }
            case LINE -> {
                System.out.println("[Visualizer] Creating individual LINE chart");
                return createLineChartFromSimpleData(data, title);
            }
            default -> {
                System.out.println("[Visualizer] Unknown chart type: " + chartType);
                return null;
            }
        }
    }

    /**
     * creates a side-by-side bar chart comparing before and after data.
     * @param data map containing "Before" and "After" keys with nutrient data
     * @param title chart title
     * @return a ChartPanel with comparison bar chart 
     */
    public static ChartPanel createComparisonBarChart(Map<String, Map<String, Double>> data, String title) {
        System.out.println("[Visualizer] Creating comparison bar chart: " + title);
        System.out.println("[Visualizer] Input comparison data keys: " + data.keySet());
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int beforeCount = 0, afterCount = 0;
        
        if (data.containsKey("Before")) {
            System.out.println("[Visualizer] Processing Before data: " + data.get("Before").size() + " nutrients");
            data.get("Before").forEach((nutrient, value) -> {
                System.out.println("[Visualizer] Before - " + nutrient + ": " + value);
                dataset.addValue(value, "Before Swaps", nutrient);
            });
            beforeCount = data.get("Before").size();
        } else {
            System.out.println("[Visualizer] WARNING: No 'Before' data found in comparison data");
        }
        
        if (data.containsKey("After")) {
            System.out.println("[Visualizer] Processing After data: " + data.get("After").size() + " nutrients");
            data.get("After").forEach((nutrient, value) -> {
                System.out.println("[Visualizer] After - " + nutrient + ": " + value);
                dataset.addValue(value, "After Swaps", nutrient);
            });
            afterCount = data.get("After").size();
        } else {
            System.out.println("[Visualizer] WARNING: No 'After' data found in comparison data");
        }
        
        System.out.println("[Visualizer] Added to comparison bar chart - Before: " + beforeCount + ", After: " + afterCount);
        
        JFreeChart chart = ChartFactory.createBarChart(
            title, "Nutrients", "Amount", dataset, 
            PlotOrientation.VERTICAL, true, true, false);
            
        return new ChartPanel(chart);
    }

    /**
     * creates a line chart comparing before and after data with overlaid lines.
     * @param data map containing "Before" and "After" keys with nutrient data
     * @param title chart title
     * @return a ChartPanel with comparison line chart
     */
    public static ChartPanel createComparisonLineChart(Map<String, Map<String, Double>> data, String title) {
        System.out.println("[Visualizer] Creating comparison line chart: " + title);
        System.out.println("[Visualizer] Input comparison data keys: " + data.keySet());
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int beforeCount = 0, afterCount = 0;
        
        if (data.containsKey("Before")) {
            System.out.println("[Visualizer] Processing Before data: " + data.get("Before").size() + " nutrients");
            data.get("Before").forEach((nutrient, value) -> {
                System.out.println("[Visualizer] Before - " + nutrient + ": " + value);
                dataset.addValue(value, "Before Swaps", nutrient);
            });
            beforeCount = data.get("Before").size();
        } else {
            System.out.println("[Visualizer] WARNING: No 'Before' data found in comparison data");
        }
        
        if (data.containsKey("After")) {
            System.out.println("[Visualizer] Processing After data: " + data.get("After").size() + " nutrients");
            data.get("After").forEach((nutrient, value) -> {
                System.out.println("[Visualizer] After - " + nutrient + ": " + value);
                dataset.addValue(value, "After Swaps", nutrient);
            });
            afterCount = data.get("After").size();
        } else {
            System.out.println("[Visualizer] WARNING: No 'After' data found in comparison data");
        }
        
        System.out.println("[Visualizer] Added to comparison line chart - Before: " + beforeCount + ", After: " + afterCount);
        
        JFreeChart chart = ChartFactory.createLineChart(
            title, "Nutrients", "Amount", dataset,
            PlotOrientation.VERTICAL, true, true, false);
            
        return new ChartPanel(chart);
    }

    /**
     * Helper method to prepare comparison data from original and current meals
     * @param originalMeals meals before swaps
     * @param currentMeals meals after swaps  
     * @param selectedNutrients nutrients to include in comparison
     * @return map with "Before" and "After" keys containing nutrient data
     */
    public static Map<String, Map<String, Double>> prepareComparisonData(
        List<Meal> originalMeals, List<Meal> currentMeals, List<NutrientType> selectedNutrients) {

        System.out.println("[Visualizer] Preparing comparison data");

        // checking input
        if (selectedNutrients == null || selectedNutrients.isEmpty()) {
            System.out.println("[Visualizer] No nutrients provided, using fallback");
            selectedNutrients = List.of(NutrientType.Protein, NutrientType.Carbohydrate, NutrientType.Fat, NutrientType.Calories);
        }

        // preparation of comparison maps
        Map<String, Map<String, Double>> comparisonData = new HashMap<>();
        comparisonData.put("Before", calculateNutrientTotals(originalMeals, selectedNutrients, "Before"));
        comparisonData.put("After", calculateNutrientTotals(currentMeals, selectedNutrients, "After"));

        System.out.println("[Visualizer] Comparison data prepared successfully");
        return comparisonData;
    }

    private static Map<String, Double> calculateNutrientTotals(
        List<Meal> meals, List<NutrientType> nutrients, String label) {

        System.out.println("[Visualizer] Calculating " + label + " data...");
        Map<String, Double> result = new HashMap<>();

        if (meals == null || meals.isEmpty()) {
            System.out.println("[Visualizer] WARNING: " + label + " meals are null or empty");
            return result;
        }

        for (NutrientType nutrient : nutrients) {
            double total = calculateTotalNutrient(meals, nutrient);
            result.put(nutrient.name(), total);
            System.out.println("[Visualizer] " + label + " - " + nutrient.name() + ": " + total);
        }

        return result;
    }


    /**
     * Helper method to calculate total nutrient value across meals
     * Uses consistent calculation logic with NutrientAnalyzer (database lookup + quantity scaling)
     */
    private static double calculateTotalNutrient(List<Meal> meals, NutrientType nutrient) {
        System.out.println("[Visualizer] Calculating total " + nutrient.name() + " for " + (meals != null ? meals.size() : "NULL") + " meals");
        
        if (meals == null || meals.isEmpty()) {
            System.out.println("[Visualizer] WARNING: No meals provided, returning 0");
            return 0.0;
        }
        
        double total = 0.0;
        DatabaseNutrientLookup lookup = new DatabaseNutrientLookup();
        
        for (Meal meal : meals) {
            System.out.println("[Visualizer] Processing meal with " + meal.getItems().size() + " items");
            for (Food food : meal.getItems()) {
                System.out.println("[Visualizer] Processing food: " + food.getFoodID() + ", quantity: " + food.getQuantity());
                
                // Use same logic as NutrientAnalyzer: try database first, fallback to food object
                Map<NutrientType, Double> foodNutrients = lookup.getPerUnit(food.getFoodID());
                if (foodNutrients == null || foodNutrients.isEmpty()) {
                    foodNutrients = food.getNutrients();
                    System.out.println("[Visualizer] Using fallback nutrients from food object");
                } else {
                    System.out.println("[Visualizer] Using database nutrients: " + foodNutrients.size() + " nutrients");
                }
                
                // Apply quantity scaling like NutrientAnalyzer does
                Double nutrientValue = foodNutrients.get(nutrient);
                if (nutrientValue != null) {
                    double scaledValue = (nutrientValue / 100.0) * food.getQuantity();
                    total += scaledValue;
                    System.out.println("[Visualizer] Added " + nutrient.name() + ": " + scaledValue + " (raw: " + nutrientValue + ", quantity: " + food.getQuantity() + ")");
                } else {
                    System.out.println("[Visualizer] No " + nutrient.name() + " data for food " + food.getFoodID());
                }
            }
        }
        
        System.out.println("[Visualizer] Total " + nutrient.name() + ": " + total);
        return total;
    }


    public JFreeChart createChartFromTrend(TrendResult result, ChartType chartType) {
        Map<LocalDate, NutrientStats> perDayStats = result.getPerDayStats();
        if (perDayStats == null || perDayStats.isEmpty()) {
            System.out.println("[Visualizer] No trend data available");
            return null;
        }

        switch (chartType) {
            case LINE:
                return createLineChart(perDayStats);
            case BAR:
                return createBarChart(perDayStats);
            case PIE:
                return createPieChart(result.getCumulativeStats());
            default:
                throw new IllegalArgumentException("Unsupported chart type: " + chartType);
        }
    }

    // line chart
    private JFreeChart createLineChart(Map<LocalDate, NutrientStats> perDayStats) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<LocalDate, NutrientStats> entry : perDayStats.entrySet()) {
            String date = entry.getKey().toString();
            NutrientStats stats = entry.getValue();

            for (Map.Entry<NutrientType, Double> nutrientEntry : stats.getNutrientPercentages().entrySet()) {
                dataset.addValue(nutrientEntry.getValue(), nutrientEntry.getKey().name(), date);
            }
        }

        return ChartFactory.createLineChart(
            "Daily Nutrient Breakdown (Line Chart)",
            "Date",
            "Percentage",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );
    }

    // bar chart
    private JFreeChart createBarChart(Map<LocalDate, NutrientStats> perDayStats) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<LocalDate, NutrientStats> entry : perDayStats.entrySet()) {
            String date = entry.getKey().toString();
            NutrientStats stats = entry.getValue();

            for (Map.Entry<NutrientType, Double> nutrientEntry : stats.getNutrientPercentages().entrySet()) {
                dataset.addValue(nutrientEntry.getValue(), nutrientEntry.getKey().name(), date);
            }
        }

        return ChartFactory.createBarChart(
            "Daily Nutrient Breakdown (Bar Chart)",
            "Date",
            "Percentage",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );
    }

    // pie chart
    private JFreeChart createPieChart(NutrientStats cumulativeStats) {
        System.out.println("[Visualizer] Creating PIE chart from TrendResult cumulative stats");

        VisualizationOps ops = new VisualizationOps();
        Map<String, Double> data = convertToChartData(cumulativeStats, ops);

        if (data.isEmpty()) {
            System.out.println("[Visualizer] No data to display in PIE chart, adding dummy");
            data.put("No Data", 1.0);
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        return ChartFactory.createPieChart("Cumulative Nutrient Breakdown (Pie Chart)", dataset, true, true, false);
    }

        public void update(String action, UUID userId, List<Meal> meals) {
            
        //  temp stub; actual visualizer update logic later
        System.out.println("[Visualizer] update triggered: " + action + " for user " + userId);
    }



}
