package org.Handlers.UI;

import javax.swing.*;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;
import java.util.ArrayList;

import java.awt.event.ActionEvent;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import org.Entity.*;
import org.Enums.Sex;
import org.Enums.CFGVersion;
import org.Enums.NutrientType;
import org.Handlers.Controller.ProfileManager;
import org.Handlers.Database.ExerciseLog;
import org.Handlers.Database.IntakeLog;
import org.Handlers.Logic.NutrientAnalyzer;
import org.Handlers.Logic.SwapEngine;
import org.Handlers.Visual.Visualizer;

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

        /*
        // === ORIGINAL CONSOLE VERSION ===
        ProfileManager pm = new ProfileManager();
        UUID userId = UUID.randomUUID();
        Profile profile = new Profile(userId, Sex.Female, LocalDate.of(1990, 1, 1), 165, 60, "metric");
        pm.saveProfile(profile);
        System.out.println("Saved profile: " + profile.getUserID());

        IntakeLog intakeLog = new IntakeLog();
        Meal breakfast = new Meal(LocalDate.now());
        breakfast.addItem(new Food("Milk", 1));
        breakfast.addItem(new Food("Bread", 2));
        intakeLog.saveMeal(breakfast);
        System.out.println("Logged meal items: " + breakfast.getItems().size());

        ExerciseLog exerciseLog = new ExerciseLog();
        Exercise run = new Exercise(LocalDate.now(), "Running", Duration.ofMinutes(30));
        exerciseLog.saveSession(run);
        System.out.println("Logged exercise: " + run.getType());

        NutrientAnalyzer analyzer = new NutrientAnalyzer();
        int items = analyzer.analyze(intakeLog.fetchMealsByDate(LocalDate.now())).getTotalItems();
        int exercises = exerciseLog.fetchSessionsByDate(LocalDate.now()).size();
        Map<String, Integer> chartData = new HashMap<>();
        chartData.put("Items Eaten", items);
        chartData.put("Exercises", exercises);
        Visualizer.showPie(chartData, "Today's Activity");
        */
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
        currentUser = new Profile(id, Sex.Female, LocalDate.of(1990, 1, 1), 165, 60, "metric");
        profileManager.saveProfile(currentUser);
        JOptionPane.showMessageDialog(parent, "Profile created for user: " + id);
    }

    // === Meal Tab ===
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
        meal.addItem(new Food("Milk", 1));
        meal.addItem(new Food("Bread", 2));
        intakeLog.saveMeal(meal);

        Exercise run = new Exercise(LocalDate.now(), "Running", Duration.ofMinutes(30));
        exerciseLog.saveSession(run);

        JOptionPane.showMessageDialog(parent, " The meal and exercise are logged.");
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
            showError(parent, "First please create a profile .");
            return;
        }

        DateRange range = new DateRange(LocalDate.now().minusDays(1), LocalDate.now());
        List<Meal> meals = intakeLog.getMealsBetween(range);

        if (meals == null || meals.isEmpty()) {
            showError(parent, "No meals found to swap.");
            return;
        }

        SwapRequest request = new SwapRequest(currentUser, range, NutrientType.Fiber, CFGVersion.V2020);
        List<Meal> swapped = swapEngine.applySwap(meals, request);
        intakeLog.updateMeals(swapped);

        JOptionPane.showMessageDialog(parent, "Swaps are applied to " + swapped.size() + " meals.");
    }

    // === Utility ===
    private static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, "⚠ " + msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

}

