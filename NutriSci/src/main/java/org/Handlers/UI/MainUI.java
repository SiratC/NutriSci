package org.Handlers.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.Dao.FoodNameDAO;
import org.Dao.NutrientAmountDAO;
import org.Entity.AlignmentScore;
import org.Entity.DateRange;
import org.Entity.Food;
import org.Entity.FoodGroupStats;
import org.Entity.FoodName;
import org.Entity.Meal;
import org.Entity.NutrientAmount;
import org.Entity.NutrientChangeStats;
import org.Entity.NutrientStats;
import org.Entity.Profile;
import org.Entity.ProgressStatus;
import org.Entity.SwapRequest;
import org.Entity.TrendResult;
import org.Entity.VisualizationOps;
import org.Enums.CFGVersion;
import org.Enums.ChartType;
import org.Enums.NutrientType;
import org.Enums.Sex;
import org.Handlers.Controller.MealManager;
import org.Handlers.Controller.ProfileManager;
import org.Handlers.Database.DataLoader;
import org.Handlers.Database.DatabaseFoodNameDAO;
import org.Handlers.Database.DatabaseNutrientAmountDAO;
import org.Handlers.Database.IntakeLog;
import org.Handlers.Logic.Analyzer;
import org.Handlers.Logic.AnalyzerFactory;
import org.Handlers.Logic.CFGComparer;
import org.Handlers.Logic.DatabaseNutrientLookup;
import org.Handlers.Logic.NutrientAnalyzer;
import org.Handlers.Logic.NutrientCalculator;
import org.Handlers.Logic.SwapEngine;
import org.Handlers.Logic.SwapTracker;
import org.Handlers.Visual.CFGChartFactory;
import org.Handlers.Visual.TrendChartFactory;
import org.Handlers.Visual.Visualizer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class MainUI {

    private static final ProfileManager profileManager = ProfileManager.getInstance();
    private static final MealManager mealManager = MealManager.getInstance();
    private static final IntakeLog intakeLog = new IntakeLog();
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
        tabs.addTab("Swap Compare", buildSwapCompareTab());
        tabs.addTab("Journal", buildJournalTab());

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
                tabs.setSelectedIndex(2); // switch to Profile tab
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
        JComboBox<Sex> sexBox = new JComboBox<>(Sex.values());
        sexBox.setSelectedIndex(-1); // force user to pick sex
        JButton registerButton = new JButton("Register");

        int row = 0;

        // Username
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        row++;

        // Password
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        row++;

        // Sex
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Sex (select one):"), gbc);
        gbc.gridx = 1;
        panel.add(sexBox, gbc);
        row++;

        // Register button
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, gbc);

        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                showError(panel, "Username and password must not be empty.");
                return;
            }

            Sex selectedSex = (Sex) sexBox.getSelectedItem();
            if (selectedSex == null) {
                showError(panel, "Please select your sex.");
                return;
            }

            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            Profile profile = new Profile(
                    id,
                    username,
                    password,
                    selectedSex,
                    LocalDate.of(1990, 1, 1), // placeholder DOB
                    170,
                    70,
                    "metric",
                    now,
                    now);

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

        JButton viewProfileBtn = new JButton("View Profile Info");
        viewProfileBtn.addActionListener(e -> {
            if (currentUser == null) {
                showError(panel, "Please log in first.");
                return;
            }
            String profileDetails = currentUser.toString();
            JOptionPane.showMessageDialog(panel, profileDetails, "Profile Info", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton nutrientBreakdownBtn = new JButton("Show Nutrient Breakdown (Today)");
        nutrientBreakdownBtn.addActionListener(e -> showNutrientBreakdown(panel));

        JButton editProfileBtn = new JButton("Edit Profile Info");
        editProfileBtn.addActionListener(e -> showEditProfileDialog(panel));

        panel.add(viewProfileBtn);
        panel.add(nutrientBreakdownBtn);
        panel.add(editProfileBtn);

        return panel;
    }

    private static void openGoalDialog(Component parent) {
        JDialog dialog = new JDialog(mainFrame, "Set Nutrient Goals", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<NutrientType> nutrientBox1 = new JComboBox<>(NutrientType.values());
        JTextField valueField1 = new JTextField();

        JComboBox<NutrientType> nutrientBox2 = new JComboBox<>(NutrientType.values());
        JTextField valueField2 = new JTextField();

        JButton saveBtn = new JButton("Save Goals");

        // Nutrient #1
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Nutrient #1:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        dialog.add(nutrientBox1, gbc);

        // Goal #1
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Goal Amount (g or mg):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        dialog.add(valueField1, gbc);

        // Nutrient #2
        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Nutrient #2 (optional):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        dialog.add(nutrientBox2, gbc);

        // Goal #2
        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("Goal Amount (optional):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        dialog.add(valueField2, gbc);

        // Save button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        dialog.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            Map<NutrientType, Double> goals = new HashMap<>();

            try {
                NutrientType n1 = (NutrientType) nutrientBox1.getSelectedItem();
                double v1 = Double.parseDouble(valueField1.getText());
                goals.put(n1, v1);
            } catch (Exception ex) {
                showError(parent, "Invalid input for nutrient #1.");
                return;
            }

            try {
                String val2 = valueField2.getText().trim();
                if (!val2.isEmpty()) {
                    NutrientType n2 = (NutrientType) nutrientBox2.getSelectedItem();
                    double v2 = Double.parseDouble(val2);
                    goals.put(n2, v2);
                }
            } catch (Exception ex) {
                showError(parent, "Invalid input for nutrient #2.");
                return;
            }

            currentUser.setNutrientGoals(goals); // assuming this method exists
            JOptionPane.showMessageDialog(dialog, "Goals saved!");
            dialog.dispose();
        });

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static void showProgressDialog() {
        List<Meal> meals = mealManager.getMeals(currentUser.getUserID());
        if (meals == null || meals.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "No meals found. Log some meals first.");
            return;
        }

        NutrientAnalyzer analyzer = new NutrientAnalyzer();
        NutrientStats stats = analyzer.analyze(meals);
        Map<NutrientType, Double> intake = stats.getAllStats();
        Map<NutrientType, Double> goals = currentUser.getNutrientGoals();
        ProgressStatus progress = new ProgressStatus(intake, goals);

        JPanel progressPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        for (Map.Entry<NutrientType, Double> entry : goals.entrySet()) {
            NutrientType type = entry.getKey();
            double percentage = progress.progressOf(type);

            JPanel container = new JPanel(new BorderLayout());
            JLabel label = new JLabel(type + ": " + String.format("%.1f", percentage) + "%");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));

            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue((int) percentage);
            bar.setForeground(getProgressColor(percentage));
            bar.setStringPainted(false);

            container.add(label, BorderLayout.NORTH);
            container.add(bar, BorderLayout.CENTER);
            progressPanel.add(container);
        }

        JScrollPane scroll = new JScrollPane(progressPanel);
        scroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(mainFrame, scroll, "Nutrient Goal Progress", JOptionPane.INFORMATION_MESSAGE);
    }

    private static Color getProgressColor(double percentage) {
        if (percentage < 50)
            return Color.RED;
        else if (percentage < 90)
            return Color.ORANGE;
        else
            return new Color(0, 153, 0); // Green
    }

    private static void showEditProfileDialog(Component parent) {
        if (currentUser == null) {
            showError(parent, "Please log in first.");
            return;
        }

        JTextField ageInput = new JTextField(String.valueOf(currentUser.getAge()));
        JTextField weightInput = new JTextField(String.valueOf(currentUser.getWeight()));
        JTextField heightInput = new JTextField(String.valueOf(currentUser.getHeight()));

        JPanel formPanel = new JPanel(new GridLayout(0, 2));
        formPanel.add(new JLabel("Age:"));
        formPanel.add(ageInput);
        formPanel.add(new JLabel("Weight (kg):"));
        formPanel.add(weightInput);
        formPanel.add(new JLabel("Height (cm):"));
        formPanel.add(heightInput);

        int choice = JOptionPane.showConfirmDialog(parent, formPanel, "Edit Profile", JOptionPane.OK_CANCEL_OPTION);

        if (choice == JOptionPane.OK_OPTION) {
            try {
                int age = Integer.parseInt(ageInput.getText());
                double weight = Double.parseDouble(weightInput.getText());
                double height = Double.parseDouble(heightInput.getText());

                LocalDate dob = LocalDate.now().minusYears(age);
                currentUser.setDob(dob);

                currentUser.setWeight(weight);
                currentUser.setHeight(height);

                double bmi = currentUser.calculateBMI();

                JOptionPane.showMessageDialog(parent,
                        "Profile updated successfully!\nNew BMI: " + String.format("%.2f", bmi),
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                showError(parent, "Invalid input. Please enter numeric values.");
            }
        }
    }

    private static JPanel buildMealTab() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // top panel for date and meal type
        JPanel topPanel = new JPanel(new FlowLayout());
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        JComboBox<String> mealTypeDropdown = new JComboBox<>(new String[] { "Breakfast", "Lunch", "Dinner", "Snack" });

        topPanel.add(new JLabel("Select Date:"));
        topPanel.add(dateSpinner);
        topPanel.add(new JLabel("Meal Type:"));
        topPanel.add(mealTypeDropdown);

        // center panel for food selections
        JPanel foodSelectionPanel = new JPanel();
        foodSelectionPanel.setLayout(new BoxLayout(foodSelectionPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(foodSelectionPanel);
        scrollPane.setPreferredSize(new Dimension(800, 300));

        // list to keep track of food selection rows
        List<FoodSelectionRow> foodRows = new ArrayList<>();

        // load from db
        final List<FoodName> foods = loadFoodsFromDatabase(mainPanel);

        // first food selection row without "Remove" button
        FoodSelectionRow firstRow = new FoodSelectionRow(foods, foodRows, foodSelectionPanel, true);
        foodRows.add(firstRow);
        foodSelectionPanel.add(firstRow.getPanel());

        // for the main buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());

        JButton addMoreButton = new JButton("Add More Foods");
        addMoreButton.addActionListener(e -> {
            FoodSelectionRow newRow = new FoodSelectionRow(foods, foodRows, foodSelectionPanel, false);
            foodRows.add(newRow);
            foodSelectionPanel.add(newRow.getPanel());
            foodSelectionPanel.revalidate();
            foodSelectionPanel.repaint();
        });

        JButton logMealButton = new JButton("Log Meal");
        logMealButton.addActionListener(e -> {
            if (currentUser == null) {
                showError(mainPanel, "Please log in first.");
                return;
            }

            String mealType = (String) mealTypeDropdown.getSelectedItem();
            LocalDate date = ((Date) dateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // collects all valid food selections
            List<Food> selectedFoods = new ArrayList<>();
            for (FoodSelectionRow row : foodRows) {
                Food food = row.getSelectedFood();
                if (food.getQuantity() <= 0) {
                    showError(mainPanel, "Please enter a valid quantity for all selected foods.");
                    return;
                }

                selectedFoods.add(food);
            }

            if (selectedFoods.isEmpty()) {
                showError(mainPanel, "Please select at least one food item.");
                return;
            }

            // check if meal already logged (except for snacks)
            List<Meal> mealsOnDate = intakeLog.getMealsBetween(currentUser.getUserID(), new DateRange(date, date));
            boolean alreadyLogged = mealsOnDate.stream()
                    .anyMatch(m -> m.getType().equalsIgnoreCase(mealType) && !mealType.equalsIgnoreCase("Snack"));

            if (alreadyLogged) {
                showError(mainPanel, "You already logged " + mealType + " for this date.");
                return;
            }

            Meal meal = new Meal.Builder().withDate(date).withType(mealType).build();
            for (Food food : selectedFoods) {
                meal.addItem(food);
            }

            intakeLog.saveMeal(currentUser.getUserID(), meal);

            JOptionPane.showMessageDialog(mainPanel,
                    "Meal logged successfully with " + selectedFoods.size() + " food item(s).");

            if (!foodRows.isEmpty()) {
                // only clear the first row's selection, not remove it
                foodRows.get(0).clearSelection();

                // remove all additional rows
                for (int i = foodRows.size() - 1; i > 0; i--) {
                    FoodSelectionRow rowToRemove = foodRows.get(i);
                    foodSelectionPanel.remove(rowToRemove.getPanel());
                    foodRows.remove(i);
                }

                foodSelectionPanel.revalidate();
                foodSelectionPanel.repaint();
            }
        });

        bottomPanel.add(addMoreButton);
        bottomPanel.add(logMealButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    // helper class to manage individual food selection rows
    private static class FoodSelectionRow {
        private JPanel panel;
        private JComboBox<FoodName> foodDropdown;
        private JTextField quantityField;
        private JButton removeButton;
        private List<FoodSelectionRow> allRows;
        private JPanel parentPanel;
        private boolean isFirstRow;

        public FoodSelectionRow(List<FoodName> foods, List<FoodSelectionRow> allRows,
                JPanel parentPanel, boolean isFirstRow) {
            this.allRows = allRows;
            this.parentPanel = parentPanel;
            this.isFirstRow = isFirstRow;

            createPanel(foods);
        }

        private void createPanel(List<FoodName> foods) {
            panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBorder(BorderFactory.createEtchedBorder());
            panel.setPreferredSize(new Dimension(750, 50));
            panel.setMinimumSize(new Dimension(750, 50));
            panel.setMaximumSize(new Dimension(750, 50));

            foodDropdown = new JComboBox<>();
            for (FoodName food : foods) {
                foodDropdown.addItem(food);
            }
            foodDropdown.setSelectedIndex(0);
            foodDropdown.setPreferredSize(new Dimension(300, 25));

            quantityField = new JTextField(8);
            quantityField.setPreferredSize(new Dimension(80, 25));

            // remove button (only for non-first rows)
            if (!isFirstRow) {
                removeButton = new JButton("Remove");
                removeButton.setPreferredSize(new Dimension(80, 25));
                removeButton.addActionListener(e -> removeRow());
            }

            panel.add(new JLabel("Select Food:"));
            panel.add(foodDropdown);
            panel.add(new JLabel("Quantity (g):"));
            panel.add(quantityField);

            if (removeButton != null) {
                panel.add(removeButton);
            }
        }

        private void removeRow() {
            allRows.remove(this);
            parentPanel.remove(panel);
            parentPanel.revalidate();
            parentPanel.repaint();
        }

        public Food getSelectedFood() {
            FoodName selected = (FoodName) foodDropdown.getSelectedItem();
            if (selected == null) {
                return null;
            }

            String quantityText = quantityField.getText().trim();

            try {
                double quantity = Double.parseDouble(quantityText.isEmpty() ? "0" : quantityText);
                return new Food(
                        selected.getFoodId(),
                        selected.getFoodDescription(),
                        quantity,
                        quantity * (selected.getCaloriesPer100g() / 100));
            } catch (NumberFormatException e) {
                return null;
            }
        }

        public void clearSelection() {
            foodDropdown.setSelectedIndex(0);
            quantityField.setText("");
        }

        public JPanel getPanel() {
            return panel;
        }
    }

    // helper to load foods from database into one final var
    private static List<FoodName> loadFoodsFromDatabase(JPanel errorPanel) {
        try {
            FoodNameDAO foodDao = new DatabaseFoodNameDAO();
            List<FoodName> loadedFoods = foodDao.getAllFoodNames();
            if (loadedFoods.isEmpty()) {
                System.out.println("No foods found.");
            }
            return loadedFoods;
        } catch (Exception e) {
            e.printStackTrace();
            showError(errorPanel, "Failed to load food list.");
            return new ArrayList<>();
        }
    }

    private static JPanel buildSwapTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<NutrientType> nutrientBox = new JComboBox<>(NutrientType.values());
        JTextField intensityField = new JTextField("10");
        JCheckBox percentCheck = new JCheckBox("Use Percentage", true);
        JComboBox<CFGVersion> cfgBox = new JComboBox<>(CFGVersion.values());
        JButton applySwapBtn = new JButton("Apply Swap");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Target Nutrient:"), gbc);
        gbc.gridx = 1;
        panel.add(nutrientBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Intensity:"), gbc);
        gbc.gridx = 1;
        panel.add(intensityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("CFG Version:"), gbc);
        gbc.gridx = 1;
        panel.add(cfgBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Mode:"), gbc);
        gbc.gridx = 1;
        panel.add(percentCheck, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(applySwapBtn, gbc);

        applySwapBtn.addActionListener(e -> {
            if (currentUser == null) {
                showError(panel, "Please log in first.");
                return;
            }

            try {
                NutrientType nutrient = (NutrientType) nutrientBox.getSelectedItem();
                CFGVersion cfg = (CFGVersion) cfgBox.getSelectedItem();
                double intensity = Double.parseDouble(intensityField.getText());
                boolean isPercent = percentCheck.isSelected();

                DateRange range = new DateRange(LocalDate.now().minusDays(1), LocalDate.now());
                List<Meal> meals = intakeLog.getMealsBetween(currentUser.getUserID(), range);
                if (meals == null || meals.isEmpty()) {
                    showError(panel, "No meals found in selected range.");
                    return;
                }

                SwapRequest request = new SwapRequest(currentUser, range, nutrient, cfg,
                        intensity / (isPercent ? 100.0 : 1.0), isPercent);
                List<Meal> swapped = swapEngine.applySwap(meals, request);
                intakeLog.updateMeals(currentUser.getUserID(), swapped);

                JOptionPane.showMessageDialog(panel, "Swaps applied to " + swapped.size() + " meals.");
                // showProgressDialog();

            } catch (NumberFormatException ex) {
                showError(panel, "Invalid intensity value.");
            } catch (Exception ex) {
                showError(panel, "Swap failed: " + ex.getMessage());
            }
        });

        return panel;
    }

    private static JPanel buildSwapCompareTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton loadComparisonButton = new JButton("Compare Swaps (Past 1 day)");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        loadComparisonButton.addActionListener(e -> {
            if (currentUser == null) {
                showError(panel, "Please log in first.");
                return;
            }
            DateRange range = new DateRange(LocalDate.now().minusDays(1), LocalDate.now());
            List<Meal> originalMeals = intakeLog.getOriginalMealsBetween(currentUser.getUserID(), range);
            if (originalMeals == null || originalMeals.isEmpty()) {
                showError(panel, "No meals found to compare.");
                return;
            }
            for (Meal meal : originalMeals) {
                if (meal.getItems().isEmpty()) {
                    showError(panel, "No meals found to compare");
                    return;
                }
            }
            List<Meal> currentMeals = intakeLog.getMealsBetween(currentUser.getUserID(), range);

            originalMeals = loadCaloriesForMeals(originalMeals);
            currentMeals = loadCaloriesForMeals(currentMeals);

            NutrientCalculator calc = new NutrientCalculator(new DatabaseNutrientLookup());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < originalMeals.size(); i++) {
                Meal orig = originalMeals.get(i);
                Meal swap = currentMeals.get(i);
                sb.append("\nDate: ").append(orig.getDate()).append("\n");
                sb.append("Original Meal:\n");
                for (Food f : orig.getItems()) {
                    sb.append(" - ").append(f).append("\n");
                }
                Map<NutrientType, Double> origNutrients = calc.calculate(orig);
                sb.append("\nSwapped Meal:\n");
                for (Food f : swap.getItems()) {
                    sb.append(" - ").append(f).append("\n");
                }
                Map<NutrientType, Double> swapNutrients = calc.calculate(swap);
                sb.append("\nNutrient Comparison:\n");
                for (NutrientType t : NutrientType.values()) {
                    double before = origNutrients.getOrDefault(t, 0.0);
                    double after = swapNutrients.getOrDefault(t, 0.0);
                    double diff = after - before;
                    String change = diff > 0 ? "↑" : (diff < 0 ? "↓" : "→");
                    sb.append(String.format(" %s: %.1f → %.1f (%+.1f) %s\n", t, before, after, diff, change));
                }
                sb.append("\n-----------------------------------\n");
            }
            resultArea.setText(sb.toString());
        });
        panel.add(loadComparisonButton, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        return panel;
    }

    private static List<Meal> loadCaloriesForMeals(List<Meal> meals) {
        List<Meal> result = new ArrayList<>();
        NutrientAmountDAO nutrientDAO = new DatabaseNutrientAmountDAO();

        for (Meal meal : meals) {
            Meal.Builder builder = new Meal.Builder()
                    .withDate(meal.getDate())
                    .withId(meal.getId())
                    .withType(meal.getType());

            for (Food food : meal.getItems()) {
                try {
                    // Load nutrient data for this food
                    List<NutrientAmount> nutrients = nutrientDAO.findByFoodId(food.getFoodID());
                    Map<NutrientType, Double> nutrientMap = new HashMap<>();
                    double scaleFactor = food.getQuantity() / 100.0;

                    for (NutrientAmount na : nutrients) {
                        NutrientType type = mapIdToNutrientType(na.getNutrientNameId());
                        if (type != null) {
                            nutrientMap.put(type, na.getNutrientValue().doubleValue() * scaleFactor);
                        }
                    }

                    // Calculate calories
                    double protein = nutrientMap.getOrDefault(NutrientType.Protein, 0.0);
                    double carbs = nutrientMap.getOrDefault(NutrientType.Carbohydrate, 0.0);
                    double fat = nutrientMap.getOrDefault(NutrientType.Fat, 0.0);
                    double calories = 4 * protein + 4 * carbs + 9 * fat;

                    // Create new food with proper calories
                    Food enrichedFood = new Food(food.getFoodID(), food.getName(), food.getQuantity(), calories);
                    enrichedFood.setNutrients(nutrientMap);
                    builder.add(enrichedFood);

                } catch (SQLException ex) {
                    System.err.println("Failed to load nutrients for " + food.getName() + ": " + ex.getMessage());
                    builder.add(food); // Add original food if loading fails
                }
            }

            result.add(builder.build());
        }

        return result;
    }

    private static NutrientType mapIdToNutrientType(int id) {
        return switch (id) {
            case 203 -> NutrientType.Protein;
            case 205 -> NutrientType.Carbohydrate;
            case 204 -> NutrientType.Fat;
            case 291 -> NutrientType.Fiber;
            case 208 -> NutrientType.Calories;
            default -> null;
        };
    }

    private static void showNutrientBreakdown(Component parent) {
        if (currentUser == null) {
            showError(parent, "Please log in first.");
            return;
        }

        DateRange today = new DateRange(LocalDate.now(), LocalDate.now());
        List<Meal> meals = intakeLog.getMealsBetween(currentUser.getUserID(), today);

        if (meals == null || meals.isEmpty()) {
            showError(parent, "No meals found for today.");
            return;
        }

        NutrientCalculator calc = new NutrientCalculator(new DatabaseNutrientLookup());
        Map<NutrientType, Double> nutrientMap = new HashMap<>();

        for (Meal meal : meals) {
            Map<NutrientType, Double> mealMap = calc.calculate(meal);
            for (Map.Entry<NutrientType, Double> entry : mealMap.entrySet()) {
                nutrientMap.merge(entry.getKey(), entry.getValue(), Double::sum);
            }
        }

        double protein = nutrientMap.getOrDefault(NutrientType.Protein, 0.0);
        double carbs = nutrientMap.getOrDefault(NutrientType.Carbohydrate, 0.0);
        double fat = nutrientMap.getOrDefault(NutrientType.Fat, 0.0);
        double estimatedCalories = (protein * 4) + (carbs * 4) + (fat * 9);

        StringBuilder breakdown = new StringBuilder("Today's Nutrient Breakdown:\n\n");
        for (NutrientType type : NutrientType.values()) {
            double value = nutrientMap.getOrDefault(type, 0.0);
            breakdown.append(type.name()).append(": ").append(String.format("%.2f", value)).append("\n");
        }
        breakdown.append("\nEstimated Calories: ").append(String.format("%.2f", estimatedCalories)).append(" calories");

        JOptionPane.showMessageDialog(parent, breakdown.toString(), "Nutrient Breakdown",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static JPanel buildJournalTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea journalArea = new JTextArea();
        journalArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(journalArea);

        JButton refreshBtn = new JButton("Load Journal");
        refreshBtn.addActionListener(e -> {
            if (currentUser == null) {
                showError(panel, "Please log in first.");
                return;
            }

            List<Meal> meals = intakeLog.getAll(currentUser.getUserID());
            if (meals == null || meals.isEmpty()) {
                showError(panel, "No meals logged yet.");
                return;
            }

            StringBuilder sb = new StringBuilder("Your Meal Journal:\n\n");
            NutrientCalculator calc = new NutrientCalculator(new DatabaseNutrientLookup());

            for (Meal meal : meals) {
                sb.append("Date: ").append(meal.getDate()).append("\n");
                sb.append("Items:\n");
                for (Food item : meal.getItems()) {
                    sb.append(" - ").append(item.getName())
                            .append(" (").append(item.getQuantity()).append("g)\n");
                }

                Map<NutrientType, Double> nutrientMap = calc.calculate(meal);

                double protein = nutrientMap.getOrDefault(NutrientType.Protein, 0.0);
                double carbs = nutrientMap.getOrDefault(NutrientType.Carbohydrate, 0.0);
                double fat = nutrientMap.getOrDefault(NutrientType.Fat, 0.0);

                double estimatedCalories = (protein * 4) + (carbs * 4) + (fat * 9);
                sb.append("Estimated Calories: ").append(String.format("%.2f", estimatedCalories)).append(" kcal\n\n");

            }

            journalArea.setText(sb.toString());
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);
        return panel;
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
            if (currentUser == null) {
                showError(panel, "Please log in first.");
                return;
            }

            List<Meal> meals = intakeLog.getAll(currentUser.getUserID());
            if (meals == null || meals.isEmpty()) {
                showError(panel, "No meals found.");
                return;
            }

            Analyzer<List<Meal>, FoodGroupStats> analyzer = analyzerFactory.createFoodGroupAnalyzer();
            FoodGroupStats stats = analyzer.analyze(meals);
            AlignmentScore result = cfgComparer.analyze(stats, CFGVersion.V2019);
            System.out.println("[CFGComparer] Score: " + result);

            ChartType selectedType = (ChartType) chartTypeBox.getSelectedItem();
            ChartPanel chartPanel;

            switch (selectedType) {
                case PIE -> {
                    JFreeChart chart = CFGChartFactory.createGroupAlignmentPieChart(result);
                    chartPanel = new ChartPanel(chart);
                }
                case BAR -> {
                    JFreeChart chart = CFGChartFactory.createGroupAlignmentBarChart(result);
                    chartPanel = new ChartPanel(chart);
                }
                case LINE -> {
                    Map<String, Double> chartData = Map.of("Alignment Score", result.getScore());
                    chartPanel = Visualizer.createLineChartFromSimpleData(chartData, "CFGComparer Line View");
                }
                default -> {
                    showError(panel, "Invalid chart type.");
                    return;
                }
            }

            JLabel scoreLabel = new JLabel(String.format("Average Alignment Score: %.1f%%", result.getScore()));
            scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
            scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel wrapper = new JPanel();
            wrapper.setLayout(new BorderLayout());
            wrapper.add(scoreLabel, BorderLayout.NORTH);
            wrapper.add(chartPanel, BorderLayout.CENTER);

            JFrame chartFrame = new JFrame("CFGComparer Chart");
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.add(wrapper);
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
