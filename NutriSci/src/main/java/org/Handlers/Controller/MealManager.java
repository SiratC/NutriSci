package org.Handlers.Controller;

import org.Entity.*;
import org.Enums.CFGVersion;
import org.Enums.NutrientType;
import org.Handlers.Database.IntakeLog;
import org.Dao.ProfileDAO;
import org.Handlers.Logic.*;
import org.Handlers.Visual.Visualizer;
import org.jfree.chart.JFreeChart;

import java.time.LocalDate;
import java.util.*;

public class MealManager {

    private static MealManager instance;
    private final IntakeLog log = new IntakeLog();
    private final SwapEngine swapEngine = new SwapEngine();  //swap logic

    // Factory Method is used here:
    // all analyzers share the same interface, allowing different interchangeable implementations
    private final Analyzer<List<Meal>, TrendResult> trendAnalyzer = new TrendAnalyzer();
    private final Analyzer<List<Meal>, NutrientStats> nutrientAnalyzer = new NutrientAnalyzer();
    private final Analyzer<List<Meal>, FoodGroupStats> fgAnalyzer = new FoodGroupAnalyzer();

    private final CFGComparer cfgComparer = new CFGComparer();
    private final SwapTracker swapTracker = new SwapTracker();
    private final Visualizer visualizer = new Visualizer();
//    private final ProfileDAO profileDAO = new ProfileDAO(); // added to load user goals -- for now commented out; will work on this after database is fully done.

    /**
     * Singleton Design Pattern:
     * ensures only one instance of MealManager exists throughout the system
     */
    public static MealManager getInstance() {
        if (instance == null) {
            instance = new MealManager();
        }
        return instance;
    }

    // private constructor to ensure Singleton pattern
    private MealManager() {}

    /**
     * logs a meal for the given user.
     */
    public void logMeal(UUID userId, Meal meal) {
        log.add(userId, meal);
    }

    /**
     * returns all meals logged for the specified user.
     */
    public List<Meal> getMeals(UUID userId) {
        return log.getAll(userId);
    }

    /**
     * returns all meals for the user that fall within the specified date range.
     */
    public List<Meal> getMealsInRange(UUID userId, DateRange range) {
        return log.getMealsBetween(userId, range);
    }

    /**
     * applies a swap request to all meals in the specified date range for the user,
     * then returns the updated trend result.
     */
    public TrendResult applySwapToMeals(SwapRequest req) {
        UUID userId = req.getUser().getUserID();
        List<Meal> og = getMealsInRange(userId, req.getRange());
        List<Meal> swapped = swapEngine.applySwap(og, req);
        log.updateMeals(userId, swapped); // update meals in the log
        return trendAnalyzer.analyze(swapped); // return new trend result
    }

    /**
     * creates a detailed nutrient intake report with visuals
     * for a given user and time range.
     */
    // for now commented out until database fully done.
//    public IntakeReport requestNutrientBreakdown(UUID userId, DateRange range, VisualizationOps options) {
//        List<Meal> meals = getMealsInRange(userId, range);
//
//        // computes trend and nutrient analysis
//        TrendResult trend = new TrendAnalyzer().analyze(meals);
//        NutrientStats stats = trend.getCumulativeStats();
//        List<NutrientStats> perMealStats = trend.getPerMealStats();
//
//        // creates chart with analyzed data
//        Map<String, Double> chartData = visualizer.convertToChartData(stats, options);
//        JFreeChart chart = Visualizer.createPieChartPanel(chartData, "Nutrient Breakdown").getChart();
//
//        // for now, sample goal = 100% of every nutrient until we have a concrete implementation of real goals from user profile
//        Map<NutrientType, Double> intakeMap = stats.getNutrientPercentages();
//
//        // fetch actual user goals from profileDAO
//        Map<NutrientType, Double> goalMap = profileDAO.findById(userId).map(Profile::getNutrientGoals).orElse(new HashMap<>());
//
//        ProgressStatus progress = new ProgressStatus(intakeMap, goalMap);
//        return new IntakeReport(stats, perMealStats, progress, chart);
//    }

    /**
     * checks how well the user's meals align with the Canada Food Guide (2007 or 2019).
     */
    public CFGReport requestCFGAlignment(UUID userId, DateRange range, CFGVersion version) {
        List<Meal> meals = getMealsInRange(userId, range);
        FoodGroupStats stats = fgAnalyzer.analyze(meals);
        AlignmentScore score = cfgComparer.analyze(stats, version);
        return new CFGReport(score, stats);
    }

    /**
     * visualizes the effect of swaps on the user's nutrient intake and CFG alignment.
     */
    public SwapEffectReport requestSwapEffectVisualization(UUID userId, VisualizationOps options) {
        List<Meal> allMeals = getMeals(userId);

        // compare CFG alignment before/after swap (for now, use a placeholder for 'before')
        FoodGroupStats before = new FoodGroupStats(); // TODO: store and load real pre-swap data
        FoodGroupStats after = fgAnalyzer.analyze(allMeals);
        CFGDifference difference = swapTracker.analyzeCFG(before, after);

        // generate nutrient change stats and chart
        NutrientChangeStats changeStats = swapTracker.analyze(allMeals);
        Map<String, Map<String, Double>> chartData = visualizer.convertToChartData(changeStats, options);
        JFreeChart chart = Visualizer.createComparisonChart(chartData, "Swap Effect");

        return new SwapEffectReport(chart, difference);
    }
}
