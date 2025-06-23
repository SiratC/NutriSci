package org.Handlers.UI;

import javax.swing.*;
import java.awt.Component;
import java.awt.FlowLayout;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.Entity.*;
import org.Enums.Sex;
import org.Enums.CFGVersion;
import org.Enums.NutrientType;
import org.Handlers.Controller.ProfileManager;
import org.Handlers.Database.ExerciseLog;
import org.Handlers.Database.IntakeLog;
import org.Handlers.Logic.InMemNutrientLookUp;
import org.Handlers.Logic.NutrientCalculator;
import org.Handlers.Logic.SwapEngine;
import org.Handlers.Visual.Visualizer;
import org.jfree.chart.ChartPanel;

/** Demonstrates Deliverable 1 features */
public class MainUI {

    // --- Shared state for GUI ---
    private static final ProfileManager profileManager = new ProfileManager();
    private static final IntakeLog intakeLog = new IntakeLog();
    private static final ExerciseLog exerciseLog = new ExerciseLog();
    private static final SwapEngine swapEngine = new SwapEngine();
    private static Profile currentUser;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUI::setupGUI);
    }

    // === GUI Setup ===
    private static void setupGUI() {
        JFrame frame = new JFrame("NutriSci – D1 Swing UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 500);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Profile", buildProfileTab());
        tabs.addTab("Meals", buildMealTab());
        tabs.addTab("Swaps", buildSwapTab());

        frame.add(tabs);
        frame.setVisible(true);
    }

    // === Profile Tab ===
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
        LocalDate dob = LocalDate.of(1990, 1, 1);
        double height = 165;
        double weight = 60;
        String units = "metric";
        LocalDateTime now = LocalDateTime.now();

        currentUser = new Profile(id, name, Sex.Female, dob, height, weight, units, now, now);

        profileManager.saveProfile(currentUser);

        JOptionPane.showMessageDialog(parent, "Profile created for user: " + id);
    }

    // === Meal Tab with Visualizer ===
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
        meal.addItem(new Food("Test", 1));
        intakeLog.saveMeal(meal);

        Exercise run = new Exercise(LocalDate.now(), "Running", Duration.ofMinutes(30));
        exerciseLog.saveSession(run);


        NutrientCalculator calc = new NutrientCalculator(new InMemNutrientLookUp());
        Map<NutrientType, Double> nutMap = calc.calculate(meal);


        Map<String, Double> stringMap = new HashMap<>();
        for (var entry : nutMap.entrySet()) {
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

    // === Swap Tab ===
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

    // === Utility ===
    private static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, "⚠ " + msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
