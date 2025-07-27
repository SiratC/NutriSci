package org.Handlers.UI;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

import org.Dao.FoodNameDAO;
import org.Entity.*;
import org.Enums.*;
import org.Handlers.Controller.ProfileManager;
import org.Handlers.Database.DataLoader;
import org.Handlers.Database.DatabaseFoodNameDAO;
import org.Handlers.Database.ExerciseLog;
import org.Handlers.Database.IntakeLog;
import org.Handlers.Logic.*;
import org.Handlers.Visual.TrendChartFactory;
import org.Handlers.Visual.Visualizer;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.Enums.ChartType;

public class MainUI {

    private static final ProfileManager profileManager = ProfileManager.getInstance();
    private static final IntakeLog intakeLog = new IntakeLog();
    private static final ExerciseLog exerciseLog = new ExerciseLog();
    private static final SwapEngine swapEngine = new SwapEngine();
    private static final SwapTracker swapTracker = new SwapTracker();
    private static final CFGComparer cfgComparer = new CFGComparer();
    private static final AnalyzerFactory analyzerFactory = new AnalyzerFactory();

    private static Profile currentUser;
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

            if (profileManager.authenticate(username, password)) {
                currentUser = profileManager.loadProfileByName(username);
                JOptionPane.showMessageDialog(panel, "Welcome, " + currentUser.getName());

                for (int i = 2; i < tabs.getTabCount(); i++) {

                    tabs.setEnabledAt(i, true);
                }
                tabs.setSelectedIndex(2);
            } else {

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

            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            Profile profile = new Profile(id, username, password, Sex.Other, LocalDate.of(1990, 1, 1), 170, 70,
                    "metric", now, now);

            try {
                if (profileManager.loadProfileByName(username) != null) {
                    showError(panel, "Username already exists. Please choose another.");
                    return;
                }

                profileManager.saveProfile(profile);
            } catch (Exception ex) {
                showError(panel, "Error saving profile: " + ex.getMessage());
                return;
            }

            JOptionPane.showMessageDialog(panel, "Registered profile for: " + username + "\nYou can now log in.");
            tabs.setSelectedIndex(0);

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

    private static List<JComboBox<FoodName>> allFoodDropdowns = new ArrayList<>();

    private static JPanel buildMealTab() {

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Clear previous dropdowns
        allFoodDropdowns.clear();

        JComboBox<FoodName> foodDropdown = new JComboBox<>();
        allFoodDropdowns.add(foodDropdown);

        JButton logButton = new JButton("Log Meal");
        JButton addMoreFoods = new JButton("Add More Foods");

        try {

            FoodNameDAO foodDao = new DatabaseFoodNameDAO();
            List<FoodName> foods = foodDao.getAllFoodNames();
            if (foods.isEmpty()) {
                System.out.println("No foods found.");
            } else {

                for (FoodName f : foods) {
                    foodDropdown.addItem(f);
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
            showError(contentPanel, "Failed to load food list.");
        }

        logButton.addActionListener(e -> {
            if (currentUser == null) {
                showError(contentPanel, "Please log in first.");
                return;
            }

            // Collect all selected foods
            Meal meal = new Meal.Builder().withDate(LocalDate.now()).build();
            StringBuilder loggedFoods = new StringBuilder();

            for (JComboBox<FoodName> dropdown : allFoodDropdowns) {
                FoodName selected = (FoodName) dropdown.getSelectedItem();
                if (selected != null) {
                    meal.addItem(new Food(selected.getFoodId(), selected.getFoodDescription(), 1, 100));
                    if (loggedFoods.length() > 0) {
                        loggedFoods.append(", ");
                    }
                    loggedFoods.append(selected.getFoodDescription());
                }
            }

            if (loggedFoods.length() == 0) {
                showError(contentPanel, "No foods selected.");
                return;
            }

            intakeLog.saveMeal(currentUser.getUserID(), meal);

            JOptionPane.showMessageDialog(contentPanel, "Meal logged: " + loggedFoods.toString());
        });

        addMoreFoods.addActionListener(e -> {
            try {
                // Create new dropdown
                JComboBox<FoodName> newFoodDropdown = new JComboBox<>();

                // Populate with same foods
                FoodNameDAO foodDao = new DatabaseFoodNameDAO();
                List<FoodName> foods = foodDao.getAllFoodNames();
                for (FoodName f : foods) {
                    newFoodDropdown.addItem(f);
                }

                // Add to our list
                allFoodDropdowns.add(newFoodDropdown);

                // Create new row panel for the food selection
                JPanel foodRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                foodRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                foodRow.add(new JLabel("Select Food:"));
                foodRow.add(newFoodDropdown);

                // Add remove button (since this is not the first row)
                JButton removeButton = new JButton("Remove Food");
                removeButton.addActionListener(removeEvent -> {
                    allFoodDropdowns.remove(newFoodDropdown);
                    contentPanel.remove(foodRow);
                    contentPanel.revalidate();
                    contentPanel.repaint();
                });
                foodRow.add(removeButton);

                // Remove button panel and re-add at the end
                Component buttonPanel = contentPanel.getComponent(contentPanel.getComponentCount() - 1);
                contentPanel.remove(buttonPanel);

                // Add new food row
                contentPanel.add(foodRow);

                // Re-add button panel
                contentPanel.add(buttonPanel);

                contentPanel.revalidate();
                contentPanel.repaint();

            } catch (Exception ex) {
                ex.printStackTrace();
                showError(contentPanel, "Failed to add new food selection.");
            }
        });

        // Create first food selection row (no remove button)
        JPanel firstFoodRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        firstFoodRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        firstFoodRow.add(new JLabel("Select Food:"));
        firstFoodRow.add(foodDropdown);
        contentPanel.add(firstFoodRow);

        // Create button row
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        buttonRow.add(logButton);
        buttonRow.add(addMoreFoods);
        contentPanel.add(buttonRow);

        return panel;
    }

    private static void handleMealLogging(Component parent) {

        if (currentUser == null) {

            showError(parent, "Please log in first.");

            return;
        }

        Meal meal = new Meal.Builder().withDate(LocalDate.now()).build();
        meal.addItem(new Food(114, "Milk, fluid, skim", 1, 34));
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
        JPanel panel = new JPanel(new GridLayout(8, 1));

        JComboBox<ChartType> chartTypeBox = new JComboBox<>(ChartType.values());
        panel.add(new JLabel("Choose Chart Type:"));
        panel.add(chartTypeBox);

        JButton btnTrend = new JButton("Run TrendAnalyzer");
        JButton btnSwapTrack = new JButton("Run SwapTracker");
        JButton btnFG = new JButton("Run FoodGroupAnalyzer");
        JButton btnCFG = new JButton("Run CFGComparer");
        JButton btnNutri = new JButton("Run NutrientAnalyzer");

        // TrendAnalyzer runs
        btnTrend.addActionListener(e -> {
            Analyzer<List<Meal>, TrendResult> trendAnalyzer = analyzerFactory.createTrendAnalyzer();
            List<Meal> meals = intakeLog.getAll(currentUser.getUserID());

            if (meals == null || meals.isEmpty()) {
                showError(panel, "No meals found for analysis.");

                return;
            }

            TrendResult result = trendAnalyzer.analyze(meals);
            System.out.println("[TrendAnalyzer] Meals: " + result);

            VisualizationOps ops = new VisualizationOps(null, null, 3, true, (ChartType) chartTypeBox.getSelectedItem(),
                    false);
            ChartType selectedType = ops.getChartType();
            ChartPanel chartPanel;

            switch (selectedType) {

                case PIE -> {
                    Visualizer visualizer = new Visualizer();
                    Map<String, Double> pieData = visualizer.convertToChartData(result.getCumulativeStats(), ops);
                    chartPanel = Visualizer.createPieChartPanel(pieData, "Cumulative Nutrient Breakdown");
                }
                case BAR -> {
                    JFreeChart chart = TrendChartFactory.createTrendBarChart(result);
                    chartPanel = new ChartPanel(chart);
                }
                case LINE -> {
                    JFreeChart chart = TrendChartFactory.createTrendLineChart(result);
                    chartPanel = new ChartPanel(chart);
                }
                default -> {
                    showError(panel, "Invalid chart type.");

                    return;
                }
            }

            JFrame chartFrame = new JFrame("Trend Analysis Chart");
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.add(chartPanel);
            chartFrame.pack();
            chartFrame.setLocationRelativeTo(null);
            chartFrame.setVisible(true);
        });

        // SwapTracker runs
        btnSwapTrack.addActionListener(e -> {
            List<Meal> meals = intakeLog.getAll(currentUser.getUserID());
            if (meals == null || meals.isEmpty()) {
                showError(panel, "No meals found.");

                return;
            }

            NutrientChangeStats result = swapTracker.analyze(meals);
            System.out.println("[SwapTracker] Results: " + result);

            VisualizationOps ops = new VisualizationOps(null, null, 3, true, (ChartType) chartTypeBox.getSelectedItem(),
                    false);
            Visualizer visualizer = new Visualizer();
            Map<String, Map<String, Double>> chartData = visualizer.convertToChartData(result, ops);

            ChartType selectedType = ops.getChartType();
            ChartPanel chartPanel;

            switch (selectedType) {

                case PIE -> {
                    Map<String, Double> combined = new HashMap<>();
                    chartData.values().forEach(map -> map.forEach((k, v) -> combined.merge(k, v, Double::sum)));
                    chartPanel = Visualizer.createPieChartPanel(combined, "Swap Tracker Pie View");
                }

                case BAR -> chartPanel = Visualizer.createBarChartFromTimeSeries(chartData, "Swap Tracker Bar View");

                case LINE -> chartPanel = Visualizer.createLineChartFromTimeSeries(chartData, "Swap Tracker Line View");

                default -> {
                    showError(panel, "Invalid chart type.");

                    return;
                }
            }

            JFrame chartFrame = new JFrame("Swap Tracker Chart");
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.add(chartPanel);
            chartFrame.pack();
            chartFrame.setLocationRelativeTo(null);
            chartFrame.setVisible(true);
        });

        // FoodGroupAnalyzer runs
        btnFG.addActionListener(e -> {

            Analyzer<List<Meal>, FoodGroupStats> analyzer = analyzerFactory.createFoodGroupAnalyzer();
            FoodGroupStats result = analyzer.analyze(intakeLog.getAll(currentUser.getUserID()));
            System.out.println("[FoodGroupAnalyzer] Results: " + result);

            Map<String, Double> chartData = new HashMap<>();

            result.getGroupPercentages().forEach((k, v) -> chartData.put(k.name(), v));

            ChartType selectedType = (ChartType) chartTypeBox.getSelectedItem();
            ChartPanel chartPanel;

            switch (selectedType) {

                case PIE -> chartPanel = Visualizer.createPieChartPanel(chartData, "Food Group Pie View");
                case BAR -> chartPanel = Visualizer.createBarChartFromSimpleData(chartData, "Food Group Bar View");
                case LINE -> chartPanel = Visualizer.createLineChartFromSimpleData(chartData, "Food Group Line View");
                default -> {
                    showError(panel, "Invalid chart type.");

                    return;
                }
            }

            JFrame chartFrame = new JFrame("Food Group Chart");
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.add(chartPanel);
            chartFrame.pack();
            chartFrame.setLocationRelativeTo(null);
            chartFrame.setVisible(true);

        });

        // CFGComparer runs
        btnCFG.addActionListener(e -> {

            Analyzer<List<Meal>, FoodGroupStats> analyzer = analyzerFactory.createFoodGroupAnalyzer();
            FoodGroupStats stats = analyzer.analyze(intakeLog.getAll(currentUser.getUserID()));
            AlignmentScore result = cfgComparer.analyze(stats, CFGVersion.V2019);
            System.out.println("[CFGComparer] Score: " + result);

            Map<String, Double> chartData = Map.of("Alignment Score", result.getScore());
            ChartType selectedType = (ChartType) chartTypeBox.getSelectedItem();
            ChartPanel chartPanel;

            switch (selectedType) {

                case PIE -> chartPanel = Visualizer.createPieChartPanel(chartData, "CFGComparer Pie View");
                case BAR -> chartPanel = Visualizer.createBarChartFromSimpleData(chartData, "CFGComparer Bar View");
                case LINE -> chartPanel = Visualizer.createLineChartFromSimpleData(chartData, "CFGComparer Line View");
                default -> {
                    showError(panel, "Invalid chart type.");

                    return;
                }
            }

            JFrame chartFrame = new JFrame("CFGComparer Chart");
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.add(chartPanel);
            chartFrame.pack();
            chartFrame.setLocationRelativeTo(null);
            chartFrame.setVisible(true);
        });

        // NutrientAnalyzer runs
        btnNutri.addActionListener(e -> {

            Analyzer<List<Meal>, NutrientStats> analyzer = analyzerFactory.createNutrientAnalyzer();
            NutrientStats result = analyzer.analyze(intakeLog.getAll(currentUser.getUserID()));
            System.out.println("[NutrientAnalyzer] Top 3 Nutrients: " + result.getTopNutrients());

            VisualizationOps ops = new VisualizationOps(null, null, 3, true, (ChartType) chartTypeBox.getSelectedItem(),
                    false);
            Visualizer visualizer = new Visualizer();
            Map<String, Double> chartData = visualizer.convertToChartData(result, ops);

            ChartType selectedType = ops.getChartType();
            ChartPanel chartPanel;

            switch (selectedType) {

                case PIE -> chartPanel = Visualizer.createPieChartPanel(chartData, "Nutrient Analyzer Pie View");
                case BAR ->
                    chartPanel = Visualizer.createBarChartFromSimpleData(chartData, "Nutrient Analyzer Bar View");
                case LINE ->
                    chartPanel = Visualizer.createLineChartFromSimpleData(chartData, "Nutrient Analyzer Line View");
                default -> {
                    showError(panel, "Invalid chart type.");

                    return;
                }
            }

            JFrame chartFrame = new JFrame("Nutrient Analyzer Chart");
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.add(chartPanel);
            chartFrame.pack();
            chartFrame.setLocationRelativeTo(null);
            chartFrame.setVisible(true);

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
