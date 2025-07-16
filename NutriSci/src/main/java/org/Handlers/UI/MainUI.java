package org.Handlers.UI;
import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import org.Entity.*;
import org.Enums.*;
import org.Handlers.Controller.ProfileManager;
import org.Handlers.Database.ExerciseLog;
import org.Handlers.Database.IntakeLog;
import org.Handlers.Logic.*;
import org.Handlers.Visual.Visualizer;
import org.jfree.chart.ChartPanel;

// Just for testing if things are working as it should (with dummy samples for now).
public class MainUI {

    private static final ProfileManager profileManager = ProfileManager.getInstance();
    private static final IntakeLog intakeLog = new IntakeLog();
    private static final ExerciseLog exerciseLog = new ExerciseLog();
    private static final SwapEngine swapEngine = new SwapEngine();
    private static final FoodGroupAnalyzer fgAnalyzer = new FoodGroupAnalyzer();
    private static final CFGComparer cfgComparer = new CFGComparer();
    private static final NutrientAnalyzer nutrientAnalyzer = new NutrientAnalyzer();
    private static final TrendAnalyzer trendAnalyzer = new TrendAnalyzer();
    private static final SwapTracker swapTracker = new SwapTracker();
    private static Profile currentUser;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(MainUI::setupGUI);
    }

    private static void setupGUI() {

        JFrame frame = new JFrame("NutriSci – D1 Swing UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 600);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Profile", buildProfileTab());
        tabs.addTab("Meals", buildMealTab());
        tabs.addTab("Swaps", buildSwapTab());
        tabs.addTab("Analysis", buildAnalysisTab());

        frame.add(tabs);
        frame.setVisible(true);
    }

    private static JPanel buildProfileTab() {

        JPanel panel = new JPanel(new FlowLayout());
        JButton createBtn = new JButton("Create Profile");
        createBtn.addActionListener(e -> handleProfileCreation(panel));
        panel.add(createBtn);
        return panel;
    }

    private static void handleProfileCreation(Component parent) {

        UUID id = UUID.randomUUID();
        String name = JOptionPane.showInputDialog(parent, "Enter your name: ");
        currentUser = new Profile(id, name, Sex.Female, LocalDate.of(1990, 1, 1), 165, 60, "metric", LocalDateTime.now(), LocalDateTime.now());
        profileManager.saveProfile(currentUser);
        JOptionPane.showMessageDialog(parent, "Profile created for user: " + id);
    }

    private static JPanel buildMealTab() {

        JPanel panel = new JPanel(new FlowLayout());
        JButton mealBtn = new JButton("Log Sample Meal");
        mealBtn.addActionListener(e -> handleMealLogging(panel));
        panel.add(mealBtn);
        return panel;
    }

    private static void handleMealLogging(Component parent) {

        if (currentUser == null) {
            showError(parent, "Please create a profile first.");
            return;
        }

        Meal meal = new Meal(LocalDate.now());
        meal.addItem(new Food("Test", 1, 100));
        intakeLog.saveMeal(meal);

        Exercise run = new Exercise(LocalDate.now(), "Running", Duration.ofMinutes(30));
        exerciseLog.saveSession(run);

        NutrientCalculator calc = new NutrientCalculator(new InMemNutrientLookUp());

        Map<NutrientType, Double> nutMap = calc.calculate(meal);

        Map<String, Double> stringMap = new HashMap<>();

        for (Map.Entry<NutrientType, Double> entry : nutMap.entrySet()) {
            
            if (entry.getValue() > 0) {
                stringMap.put(entry.getKey().name(), entry.getValue());
            }
        }

        ChartPanel chartPanel = Visualizer.createPieChartPanel(stringMap, "Meal Nutrient Breakdown");
        JFrame chartFrame = new JFrame("Nutrient Pie Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.add(chartPanel);
        chartFrame.pack();
        chartFrame.setLocationRelativeTo(parent);
        chartFrame.setVisible(true);

        JOptionPane.showMessageDialog(parent, "The meal and exercise are logged.");
    }

    private static JPanel buildSwapTab() {

        JPanel panel = new JPanel(new FlowLayout());
        JButton swapBtn = new JButton("Apply Swap (Fiber / CFG 2020)");
        swapBtn.addActionListener(e -> handleSwapLogic(panel));
        panel.add(swapBtn);
        return panel;
    }

    private static void handleSwapLogic(Component parent) {

        if (currentUser == null) {

            showError(parent, "Please create a profile first.");
            return;
        }

        DateRange range = new DateRange(LocalDate.now().minusDays(1), LocalDate.now());
        List<Meal> meals = intakeLog.getMealsBetween(range);

        if (meals == null || meals.isEmpty()) {

            showError(parent, "No meals found to swap.");
            return;
        }

        SwapRequest request = new SwapRequest(currentUser, range, NutrientType.Fiber, CFGVersion.V2019);
        List<Meal> swapped = swapEngine.applySwap(meals, request);
        intakeLog.updateMeals(swapped);

        JOptionPane.showMessageDialog(parent, "Swaps applied to " + swapped.size() + " meals.");
    }

    private static JPanel buildAnalysisTab() {

        JPanel panel = new JPanel(new GridLayout(6, 1));

        JButton btnTrend = new JButton("Run TrendAnalyzer");
        JButton btnSwapTrack = new JButton("Run SwapTracker");
        JButton btnFG = new JButton("Run FoodGroupAnalyzer");
        JButton btnCFG = new JButton("Run CFGComparer");
        JButton btnNutri = new JButton("Run NutrientAnalyzer");

        btnTrend.addActionListener(e -> {
            TrendResult result = trendAnalyzer.analyze(intakeLog.getAll());
            System.out.println("[TrendAnalyzer] Total Meals: " + intakeLog.getAll().size());
            System.out.println(result);
        });

        btnSwapTrack.addActionListener(e -> {
            NutrientChangeStats result = swapTracker.analyze(intakeLog.getAll());
            System.out.println("[SwapTracker] Results: " + result);
        });

        btnFG.addActionListener(e -> {
            FoodGroupStats result = fgAnalyzer.analyze(intakeLog.getAll());
            System.out.println("[FoodGroupAnalyzer] Results: " + result);
        });

        btnCFG.addActionListener(e -> {
            FoodGroupStats stats = fgAnalyzer.analyze(intakeLog.getAll());
            AlignmentScore result = cfgComparer.analyze(stats, CFGVersion.V2019);
            System.out.println("[CFGComparer] Score: " + result);
        });

        btnNutri.addActionListener(e -> {
            NutrientStats result = nutrientAnalyzer.analyze(intakeLog.getAll());
            System.out.println("[NutrientAnalyzer] Top 3 Nutrients: " + result.getTopNutrients());
        });

        panel.add(btnTrend);
        panel.add(btnSwapTrack);
        panel.add(btnFG);
        panel.add(btnCFG);
        panel.add(btnNutri);
        return panel;
    }

    private static void showError(Component parent, String msg)
    {
        JOptionPane.showMessageDialog(parent, "⚠ " + msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
