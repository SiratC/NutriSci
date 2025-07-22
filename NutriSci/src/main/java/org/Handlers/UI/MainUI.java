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
import org.Handlers.Database.DataLoader;
import org.Handlers.Database.ExerciseLog;
import org.Handlers.Database.IntakeLog;
import org.Handlers.Logic.*;
import org.Handlers.Visual.Visualizer;
import org.jfree.chart.ChartPanel;

public class MainUI {

    private static final ProfileManager profileManager = ProfileManager.getInstance();
    private static final IntakeLog intakeLog = new IntakeLog();
    private static final ExerciseLog exerciseLog = new ExerciseLog();
    private static final SwapEngine swapEngine = new SwapEngine();
    private static final SwapTracker swapTracker = new SwapTracker();
    private static final CFGComparer cfgComparer = new CFGComparer();
    private static final AnalyzerFactory analyzerFactory = new AnalyzerFactory();

    private static Profile currentUser;
    private static final Map<String, Profile> mockUserDB = new HashMap<>();
    private static JFrame mainFrame;
    private static JTabbedPane tabs;

    public static void main(String[] args) {
        loadFoodData();
        SwingUtilities.invokeLater(MainUI::setupGUI);
    }

    private static void loadFoodData() {
        try {
            new DataLoader().loadAll();
            System.out.println("Loading of food items and nutrient amounts complete!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setupGUI() {

        mainFrame = new JFrame("NutriSci");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(850, 600);
        mainFrame.setLocationRelativeTo(null);

        tabs = new JTabbedPane();

        tabs.addTab("Login", buildLoginTab());
        tabs.addTab("Register", buildRegisterTab());
        tabs.addTab("Profile", buildProfileTab());
        tabs.addTab("Meals", buildMealTab());
        tabs.addTab("Swaps", buildSwapTab());
        tabs.addTab("Analysis", buildAnalysisTab());

        for (int i = 2; i < tabs.getTabCount(); i++) {

            tabs.setEnabledAt(i, false);
        }
        mainFrame.add(tabs);

        mainFrame.setVisible(true);
    }

    private static JPanel buildLoginTab() {

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField(12);
        JPasswordField passwordField = new JPasswordField(12);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);
        gbc.gridx = 1;
        panel.add(registerButton, gbc);

        loginButton.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {

                showError(panel, "Username and password cannot be empty.");

                return;
            }

            Profile existing = mockUserDB.get(username);

            if (existing != null) {

                if (!existing.getPassword().equals(password)) {

                    showError(panel, "Incorrect password.");

                    return;
                }
                currentUser = existing;
                JOptionPane.showMessageDialog(panel, "Welcome, " + currentUser.getName());

                for (int i = 2; i < tabs.getTabCount(); i++) {

                    tabs.setEnabledAt(i, true);
                }
                tabs.setSelectedIndex(2);
            }

            else {

                showError(panel, "User not registered. Please register first.");
            }
        });

        registerButton.addActionListener(e -> tabs.setSelectedIndex(1));

        return panel;
    }

    private static JPanel buildRegisterTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField(12);
        JPasswordField passwordField = new JPasswordField(12);
        JButton registerButton = new JButton("Register");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, gbc);

        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                showError(panel, "Username and password must not be empty.");
                return;
            }

            if (mockUserDB.containsKey(username)) {
                showError(panel, "User already exists.");
                return;
            }

            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            Profile profile = new Profile(id, username, Sex.Other, LocalDate.of(1990, 1, 1), 170, 70, "metric", now, now);
            profile.setPassword(password);

            mockUserDB.put(username, profile);
            profileManager.saveProfile(profile);

            JOptionPane.showMessageDialog(panel, "Registered profile for: " + username + "\nYou can now log in.");
        });

        return panel;
    }

    private static JPanel buildProfileTab() {
        JPanel panel = new JPanel(new FlowLayout());
        JButton infoBtn = new JButton("View Profile Info");
        infoBtn.addActionListener(e -> JOptionPane.showMessageDialog(panel, currentUser.toString()));
        panel.add(infoBtn);
        return panel;
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

            showError(parent, "Please log in first.");
            return;
        }

        Meal meal = new Meal(LocalDate.now());
        meal.addItem(new Food(1001, "Oatmeal", 1, 100));
        intakeLog.saveMeal(currentUser.getUserID(), meal);

        Exercise run = new Exercise(LocalDate.now(), "Running", Duration.ofMinutes(30));
        exerciseLog.saveSession(run);

        NutrientCalculator calc = new NutrientCalculator(new DatabaseNutrientLookup());
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

            showError(parent, "Please log in first.");

            return;
        }

        DateRange range = new DateRange(LocalDate.now().minusDays(1), LocalDate.now());

        List<Meal> meals = intakeLog.getMealsBetween(currentUser.getUserID(), range);

        if (meals == null || meals.isEmpty()) {

            showError(parent, "No meals found to swap.");

            return;
        }

        SwapRequest request = new SwapRequest(currentUser, range, NutrientType.Fiber, CFGVersion.V2019);
        List<Meal> swapped = swapEngine.applySwap(meals, request);
        intakeLog.updateMeals(currentUser.getUserID(), swapped);

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

            Analyzer<List<Meal>, TrendResult> trend = analyzerFactory.createTrendAnalyzer();
            TrendResult result = trend.analyze(intakeLog.getAll(currentUser.getUserID()));

            System.out.println("[TrendAnalyzer] Meals: " + result);
        });

        btnSwapTrack.addActionListener(e -> {

            NutrientChangeStats result = swapTracker.analyze(intakeLog.getAll(currentUser.getUserID()));

            System.out.println("[SwapTracker] Results: " + result);
        });

        btnFG.addActionListener(e -> {

            Analyzer<List<Meal>, FoodGroupStats> analyzer = analyzerFactory.createFoodGroupAnalyzer();
            FoodGroupStats result = analyzer.analyze(intakeLog.getAll(currentUser.getUserID()));

            System.out.println("[FoodGroupAnalyzer] Results: " + result);
        });

        btnCFG.addActionListener(e -> {

            Analyzer<List<Meal>, FoodGroupStats> analyzer = analyzerFactory.createFoodGroupAnalyzer();
            FoodGroupStats stats = analyzer.analyze(intakeLog.getAll(currentUser.getUserID()));
            AlignmentScore result = cfgComparer.analyze(stats, CFGVersion.V2019);

            System.out.println("[CFGComparer] Score: " + result);
        });

        btnNutri.addActionListener(e -> {

            Analyzer<List<Meal>, NutrientStats> analyzer = analyzerFactory.createNutrientAnalyzer();
            NutrientStats result = analyzer.analyze(intakeLog.getAll(currentUser.getUserID()));

            System.out.println("[NutrientAnalyzer] Top 3 Nutrients: " + result.getTopNutrients());
        });

        panel.add(btnTrend);
        panel.add(btnSwapTrack);
        panel.add(btnFG);
        panel.add(btnCFG);
        panel.add(btnNutri);

        return panel;
    }

    private static void showError(Component parent, String msg) {

        JOptionPane.showMessageDialog(parent, "Error:  " + msg, "", JOptionPane.ERROR_MESSAGE);
    }
}


