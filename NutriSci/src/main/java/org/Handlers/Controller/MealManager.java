package org.Handlers.Controller;

import org.Entity.*;
import org.Enums.CFGVersion;
import org.Enums.NutrientType;
import org.Handlers.Database.IntakeLog;
import org.Handlers.Logic.*;
import org.Handlers.Visual.Visualizer;
import org.jfree.chart.JFreeChart;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealManager {

    private static MealManager instance;
    private final IntakeLog log = new IntakeLog();
    private final SwapEngine swapEngine = new SwapEngine();

    // Factory Method is used below: analyzers follow a shared interface; this allows interchangeable implementation
    private final Analyzer<List<Meal>, TrendResult> trendAnalyzer = new TrendAnalyzer();
    private final Analyzer<List<Meal>, NutrientStats> nutrientAnalyzer = new NutrientAnalyzer(); // will implement later
    private final Analyzer<List<Meal>, FoodGroupStats> fgAnalyzer = new FoodGroupAnalyzer();

    private final CFGComparer cfgComparer = new CFGComparer();
    private final SwapTracker swapTracker = new SwapTracker();
    private final Visualizer visualizer = new Visualizer();


    /**
     * ensures only one instance of MealManager exists throughout the system
     * by implementing the Singleton design pattern
     */

    public static MealManager getInstance() {
        if (instance == null) {
            instance = new MealManager();
        }
        return instance;
    }

    private MealManager() {}

    public void logMeal(Meal meal) {
        log.add(meal);
    }

    public List<Meal> getMeals() {
        return Collections.unmodifiableList(log.getAll());
    }

    public List<Meal> getMealsInRange(DateRange range) {
        LocalDate start = range.getStart();
        LocalDate end = range.getEnd();
        return log.getAll().stream()
                .filter(meal -> !meal.getDate().isBefore(start) && !meal.getDate().isAfter(end))
                .toList();
    }

    public TrendResult applySwapToMeals(SwapRequest req) {
        List<Meal> og = getMealsInRange(req.getRange());
        List<Meal> swapped = swapEngine.applySwap(og, req);
        log.updateMeals(swapped);

        return trendAnalyzer.analyze(swapped);
    }

    public IntakeReport requestNutrientBreakdown(DateRange range, VisualizationOps options) {

        List<Meal> meals = getMealsInRange(range);

        // full stats and per-meal stats
        TrendAnalyzer trendAnalyzerImpl = new TrendAnalyzer();
        TrendResult trend = trendAnalyzerImpl.analyze(meals);
        NutrientStats stats = trend.getCumulativeStats();
        List<NutrientStats> perMealStats = trend.getPerMealStats();

        // chart creation
        Map<String, Double> chartData = visualizer.convertToChartData(stats, options);
        JFreeChart chart = Visualizer.createPieChartPanel(chartData, "Nutrient Breakdown").getChart();

        // progress status (sample for now)
        Map<NutrientType, Double> intakeMap = stats.getNutrientPercentages();
        Map<NutrientType, Double> goalMap = new HashMap<>();

        for(NutrientType type: intakeMap.keySet()){
            goalMap.put(type, 100.0); // placeholder for now
        }

        ProgressStatus progress = new ProgressStatus(intakeMap, goalMap);

        return new IntakeReport(stats, perMealStats, progress, chart);
    }


    public CFGReport requestCFGAlignment(DateRange range, CFGVersion version) {

        List<Meal> meals = getMealsInRange(range);
        FoodGroupStats stats = fgAnalyzer.analyze(meals);
        AlignmentScore score = cfgComparer.analyze(stats, version);

        return new CFGReport(score, stats);
    }

    public SwapEffectReport requestSwapEffectVisualization(VisualizationOps options) {

        List<Meal> allMeals = getMeals();

        // cfg difference before/after
        // sample for now
        FoodGroupStats before = new FoodGroupStats();
        FoodGroupStats after = fgAnalyzer.analyze(allMeals);
        CFGDifference difference = swapTracker.analyzeCFG(before, after);

        // chart creation
        NutrientChangeStats changeStats = swapTracker.analyze(allMeals);

        Map<String, Map<String, Double>> chartData = visualizer.convertToChartData(changeStats, options);
        JFreeChart chart = Visualizer.createComparisonChart(chartData, "Swap Effect");

        return new SwapEffectReport(chart, difference);
    }

}

