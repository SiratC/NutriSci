package org.Handlers.UI;

import org.Dao.FoodNameDAO;
import org.Dao.NutrientAmountDAO;
import org.Entity.*;
import org.Enums.CFGVersion;
import org.Enums.ChartType;
import org.Enums.NutrientType;
import org.Enums.Sex;
import org.Handlers.Controller.MealManager;
import org.Handlers.Controller.ProfileManager;
import org.Handlers.Database.*;
import org.Handlers.Logic.*;
import org.Handlers.Visual.CFGChartFactory;
import org.Handlers.Visual.Visualizer;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

public class MainUI {

    private static final ProfileManager profileManager = ProfileManager.getInstance();
    private static final MealManager mealManager = MealManager.getInstance();
    private static final IntakeLog intakeLog = new IntakeLog();
    private static final SwapEngine swapEngine = new SwapEngine();
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
        tabs.addTab("Saved Swaps", buildSavedSwapsTab());
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

            ProfileData data = new ProfileData();
            data.setUserID(id);
            data.setName(username);
            data.setPassword(password);
            data.setSex(selectedSex);
            data.setDob(LocalDate.of(1990, 1, 1));
            data.setHeight(170);
            data.setWeight(70);
            data.setUnits("metric");
            data.setCreatedAt(now);
            data.setModifiedAt(now);

            Profile profile = new Profile(data);


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

//        JButton nutrientBreakdownTodayBtn = new JButton("Show Nutrient Breakdown (Today)");
//        nutrientBreakdownTodayBtn.addActionListener(e -> showNutrientBreakdown(panel));

        JButton nutrientBreakdownRangeBtn = new JButton("Show Nutrient Breakdown (Date Range)");
        nutrientBreakdownRangeBtn.addActionListener(e -> {
            if (currentUser == null) {
                showError(panel, "Please log in first.");

                return;
            }

            // date and chart type panel
            JPanel datePanel = new JPanel(new GridLayout(3, 2));
            datePanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
            JTextField startDateField = new JTextField(LocalDate.now().minusDays(7).toString());
            datePanel.add(startDateField);

            datePanel.add(new JLabel("End Date (YYYY-MM-DD):"));
            JTextField endDateField = new JTextField(LocalDate.now().toString());
            datePanel.add(endDateField);

            datePanel.add(new JLabel("Chart Type:"));
            JComboBox<ChartType> chartTypeBox = new JComboBox<>(ChartType.values());
            datePanel.add(chartTypeBox);

            int result = JOptionPane.showConfirmDialog(panel, datePanel, "Select Date Range & Chart Type", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    LocalDate start = LocalDate.parse(startDateField.getText().trim());
                    LocalDate end = LocalDate.parse(endDateField.getText().trim());

                    if (start.isAfter(end)) {
                        showError(panel, "Start date must be before end date.");
                        return;
                    }

                    ChartType selectedChart = (ChartType) chartTypeBox.getSelectedItem();

                    // Run analyzer
                    TrendAnalyzer analyzer = new TrendAnalyzer();
                    TrendResult trend = analyzer.analyzeForDateRange(currentUser.getUserID(), start, end);

                    // Show visual
                    Visualizer visualizer = new Visualizer();
                    JFreeChart chart = visualizer.createChartFromTrend(trend, selectedChart);
                    showChartDialog(panel, chart, selectedChart + " Nutrient Breakdown from " + start + " to " + end);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(panel, "Invalid date format. Use YYYY-MM-DD.");
                }
            }
        });

        JButton editProfileBtn = new JButton("Edit Profile Info");
        editProfileBtn.addActionListener(e -> showEditProfileDialog(panel));

        panel.add(viewProfileBtn);
//        panel.add(nutrientBreakdownTodayBtn);
        panel.add(nutrientBreakdownRangeBtn);
        panel.add(editProfileBtn);

        return panel;
    }

    private static void showChartDialog(JPanel parent, JFreeChart chart, String title) {
        if (chart == null) {
            JOptionPane.showMessageDialog(parent, "No data available for chart.", "No Chart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(850, 650);
        dialog.setLocationRelativeTo(parent);
        dialog.add(chartPanel);
        dialog.setModal(true);
        dialog.setVisible(true);
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

        // First target nutrient components
        JComboBox<NutrientType> nutrientBox = new JComboBox<>(NutrientType.values());
        JTextField intensityField = new JTextField("10");
        JCheckBox percentCheck = new JCheckBox("Use Percentage", true);

        // Second target nutrient components (optional)
        JCheckBox enableSecondTargetCheck = new JCheckBox("Enable Second Target", false);
        JComboBox<NutrientType> secondNutrientBox = new JComboBox<>(NutrientType.values());
        JTextField secondIntensityField = new JTextField("10");
        JCheckBox secondPercentCheck = new JCheckBox("Use Percentage", true);

        // Initially disable second target components
        secondNutrientBox.setEnabled(false);
        secondIntensityField.setEnabled(false);
        secondPercentCheck.setEnabled(false);

        JButton applySwapBtn = new JButton("Apply Swap");

        int row = 0;

        // First target nutrient
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("First Target:"), gbc);
        gbc.gridx = 1;
        panel.add(nutrientBox, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Intensity:"), gbc);
        gbc.gridx = 1;
        panel.add(intensityField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Mode:"), gbc);
        gbc.gridx = 1;
        panel.add(percentCheck, gbc);
        row++;

        // Enable second target checkbox
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(enableSecondTargetCheck, gbc);
        gbc.gridwidth = 1;
        row++;

        // Second target nutrient (optional)
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Second Target:"), gbc);
        gbc.gridx = 1;
        panel.add(secondNutrientBox, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Second Intensity:"), gbc);
        gbc.gridx = 1;
        panel.add(secondIntensityField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Second Mode:"), gbc);
        gbc.gridx = 1;
        panel.add(secondPercentCheck, gbc);
        row++;

        // Date Range Selection
        JCheckBox customRangeCheck = new JCheckBox("Custom Date Range", false);
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));

        // Initially disable date spinners
        startDateSpinner.setEnabled(false);
        endDateSpinner.setEnabled(false);

        // Set default dates (yesterday to today)
        startDateSpinner
                .setValue(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        endDateSpinner.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(customRangeCheck, gbc);
        gbc.gridwidth = 1;
        row++;

        // Quick select buttons
        JPanel quickSelectPanel = new JPanel(new FlowLayout());
        JButton last24HoursBtn = new JButton("Last 24h");
        JButton last3DaysBtn = new JButton("Last 3 days");
        JButton lastWeekBtn = new JButton("Last week");
        JButton lastMonthBtn = new JButton("Last month");

        quickSelectPanel.add(last24HoursBtn);
        quickSelectPanel.add(last3DaysBtn);
        quickSelectPanel.add(lastWeekBtn);
        quickSelectPanel.add(lastMonthBtn);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(quickSelectPanel, gbc);
        gbc.gridwidth = 1;
        row++;

        // Add listeners for quick select buttons
        last24HoursBtn.addActionListener(e -> {
            customRangeCheck.setSelected(true);
            startDateSpinner.setEnabled(true);
            endDateSpinner.setEnabled(true);
            startDateSpinner
                    .setValue(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            endDateSpinner.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        });

        last3DaysBtn.addActionListener(e -> {
            customRangeCheck.setSelected(true);
            startDateSpinner.setEnabled(true);
            endDateSpinner.setEnabled(true);
            startDateSpinner
                    .setValue(Date.from(LocalDate.now().minusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            endDateSpinner.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        });

        lastWeekBtn.addActionListener(e -> {
            customRangeCheck.setSelected(true);
            startDateSpinner.setEnabled(true);
            endDateSpinner.setEnabled(true);
            startDateSpinner.setValue(
                    Date.from(LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            endDateSpinner.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        });

        lastMonthBtn.addActionListener(e -> {
            customRangeCheck.setSelected(true);
            startDateSpinner.setEnabled(true);
            endDateSpinner.setEnabled(true);
            startDateSpinner.setValue(
                    Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            endDateSpinner.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        });

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        panel.add(startDateSpinner, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1;
        panel.add(endDateSpinner, gbc);
        row++;

        // Add listener to enable/disable date range components
        customRangeCheck.addActionListener(e -> {
            boolean enabled = customRangeCheck.isSelected();
            startDateSpinner.setEnabled(enabled);
            endDateSpinner.setEnabled(enabled);
        });

        // Apply and Save buttons
        JButton saveSwapBtn = new JButton("Save This Swap");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(applySwapBtn);
        buttonPanel.add(saveSwapBtn);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Add listener to enable/disable second target components
        enableSecondTargetCheck.addActionListener(e -> {
            boolean enabled = enableSecondTargetCheck.isSelected();
            secondNutrientBox.setEnabled(enabled);
            secondIntensityField.setEnabled(enabled);
            secondPercentCheck.setEnabled(enabled);
        });

        applySwapBtn.addActionListener(e -> {
            if (currentUser == null) {
                showError(panel, "Please log in first.");
                return;
            }

            try {
                NutrientType nutrient = (NutrientType) nutrientBox.getSelectedItem();
                double intensity = Double.parseDouble(intensityField.getText());
                boolean isPercent = percentCheck.isSelected();

                // Create date range based on user selection
                DateRange range;
                if (customRangeCheck.isSelected()) {
                    LocalDate startDate = ((Date) startDateSpinner.getValue()).toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate endDate = ((Date) endDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    // Validate date range
                    if (startDate.isAfter(endDate)) {
                        showError(panel, "Start date cannot be after end date.");
                        return;
                    }

                    // Limit to 90 days to prevent performance issues
                    long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                    if (daysBetween > 90) {
                        showError(panel, "Date range cannot exceed 90 days. Selected range: " + daysBetween + " days.");
                        return;
                    }

                    range = new DateRange(startDate, endDate);
                    System.out.println(
                            "Using custom date range: " + startDate + " to " + endDate + " (" + daysBetween + " days)");
                } else {
                    // Default to last 24 hours
                    range = new DateRange(LocalDate.now().minusDays(1), LocalDate.now());
                    System.out.println("Using default date range: last 24 hours");
                }
                List<Meal> meals = intakeLog.getMealsBetween(currentUser.getUserID(), range);
                if (meals == null || meals.isEmpty()) {
                    showError(panel, "No meals found in selected range.");
                    return;
                }

                SwapRequest request;
                if (enableSecondTargetCheck.isSelected()) {
                    // Create dual target request
                    NutrientType secondNutrient = (NutrientType) secondNutrientBox.getSelectedItem();
                    double secondIntensity = Double.parseDouble(secondIntensityField.getText());
                    boolean secondIsPercent = secondPercentCheck.isSelected();

                    // Validate that the two target nutrients are different
                    if (nutrient.equals(secondNutrient)) {
                        showError(panel, "Second target nutrient must be different from the first.");
                        return;
                    }

                    request = new SwapRequest(currentUser, range, nutrient,
                            intensity / (isPercent ? 100.0 : 1.0), isPercent,
                            secondNutrient, secondIntensity / (secondIsPercent ? 100.0 : 1.0), secondIsPercent);

                    System.out.println("Applying dual-target swap: " + nutrient + " + " + secondNutrient);
                } else {
                    // Create single target request (backward compatibility)
                    request = new SwapRequest(currentUser, range, nutrient,
                            intensity / (isPercent ? 100.0 : 1.0), isPercent);

                    System.out.println("Applying single-target swap: " + nutrient);
                }

                SwapEngine.SwapResult swapResult = swapEngine.applySwapWithResult(meals, request);
                intakeLog.updateMealsFromSwap(currentUser.getUserID(), swapResult);

                String rangeInfo = customRangeCheck.isSelected()
                        ? " from " + range.getStart() + " to " + range.getEnd() + " (" + range.getLengthInDays()
                                + " days)"
                        : " (last 24 hours)";

                String message;
                if (swapResult.swapsWereApplied()) {
                    message = enableSecondTargetCheck.isSelected()
                            ? "Dual-target: " + swapResult.getSwapCount() + " swaps applied to "
                                    + swapResult.getMeals().size() + " meals" + rangeInfo + "."
                            : swapResult.getSwapCount() + " swaps applied to " + swapResult.getMeals().size() + " meals"
                                    + rangeInfo + ".";
                } else {
                    message = "No swaps needed - deficit was too small" + rangeInfo + ".";
                }
                JOptionPane.showMessageDialog(panel, message);
                // showProgressDialog();

            } catch (NumberFormatException ex) {
                showError(panel, "Invalid intensity value.");
            } catch (Exception ex) {
                showError(panel, "Swap failed: " + ex.getMessage());
            }
        });

        // Save swap button action listener
        saveSwapBtn.addActionListener(e -> {
            if (currentUser == null) {
                showError(panel, "Please log in first.");
                return;
            }

            try {
                NutrientType nutrient = (NutrientType) nutrientBox.getSelectedItem();
                double intensity = Double.parseDouble(intensityField.getText());
                boolean isPercent = percentCheck.isSelected();

                // Show dialog to get name and description
                JTextField nameField = new JTextField(20);
                JTextArea descriptionArea = new JTextArea(3, 20);
                descriptionArea.setLineWrap(true);
                descriptionArea.setWrapStyleWord(true);

                JPanel savePanel = new JPanel(new GridBagLayout());
                GridBagConstraints sgbc = new GridBagConstraints();
                sgbc.insets = new Insets(5, 5, 5, 5);
                sgbc.fill = GridBagConstraints.HORIZONTAL;

                sgbc.gridx = 0;
                sgbc.gridy = 0;
                savePanel.add(new JLabel("Name:"), sgbc);
                sgbc.gridx = 1;
                savePanel.add(nameField, sgbc);

                sgbc.gridx = 0;
                sgbc.gridy = 1;
                savePanel.add(new JLabel("Description:"), sgbc);
                sgbc.gridx = 1;
                savePanel.add(new JScrollPane(descriptionArea), sgbc);

                int result = JOptionPane.showConfirmDialog(panel, savePanel, "Save Swap Configuration",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    String name = nameField.getText().trim();
                    if (name.isEmpty()) {
                        showError(panel, "Please enter a name for this swap.");
                        return;
                    }

                    String description = descriptionArea.getText().trim();

                    try {
                        SavedSwapRequest savedSwap;
                        if (enableSecondTargetCheck.isSelected()) {
                            NutrientType secondNutrient = (NutrientType) secondNutrientBox.getSelectedItem();
                            double secondIntensity = Double.parseDouble(secondIntensityField.getText());
                            boolean secondIsPercent = secondPercentCheck.isSelected();

                            // Validate that the two target nutrients are different
                            if (nutrient.equals(secondNutrient)) {
                                showError(panel, "Second target nutrient must be different from the first.");
                                return;
                            }

                            savedSwap = new SavedSwapRequest(currentUser.getUserID(), name, description,
                                    nutrient, intensity / (isPercent ? 100.0 : 1.0), isPercent,
                                    secondNutrient, secondIntensity / (secondIsPercent ? 100.0 : 1.0), secondIsPercent);
                        } else {
                            savedSwap = new SavedSwapRequest(currentUser.getUserID(), name, description,
                                    nutrient, intensity / (isPercent ? 100.0 : 1.0), isPercent);
                        }

                        DatabaseSavedSwapRequestDAO dao = new DatabaseSavedSwapRequestDAO();
                        UUID savedId = dao.save(savedSwap);

                        String swapType = enableSecondTargetCheck.isSelected() ? "Dual-target" : "Single-target";
                        JOptionPane.showMessageDialog(panel,
                                swapType + " swap '" + name + "' saved successfully!\nTargets: "
                                        + savedSwap.getTargetDescription());

                    } catch (Exception ex) {
                        showError(panel, "Failed to save swap: " + ex.getMessage());
                    }
                }

            } catch (NumberFormatException ex) {
                showError(panel, "Invalid intensity values.");
            }
        });

        return panel;
    }

    private static JPanel buildSavedSwapsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top panel with controls
        JPanel topPanel = new JPanel(new FlowLayout());
        JButton refreshBtn = new JButton("Refresh List");
        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setEnabled(false);

        topPanel.add(refreshBtn);
        topPanel.add(deleteBtn);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create table model and table for saved swaps
        String[] columnNames = { "Name", "Description", "Targets", "Created", "Last Used" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        JTable savedSwapsTable = new JTable(tableModel);
        savedSwapsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        savedSwapsTable.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        savedSwapsTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Name
        savedSwapsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Description
        savedSwapsTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Targets
        savedSwapsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Created
        savedSwapsTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Last Used

        JScrollPane tableScrollPane = new JScrollPane(savedSwapsTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        // Bottom panel with application controls
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date range selection for application
        JCheckBox customRangeCheck = new JCheckBox("Custom Date Range", false);
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));

        // Initially disable date spinners
        startDateSpinner.setEnabled(false);
        endDateSpinner.setEnabled(false);

        // Set default dates (yesterday to today)
        startDateSpinner
                .setValue(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        endDateSpinner.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        bottomPanel.add(customRangeCheck, gbc);
        gbc.gridwidth = 1;
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        bottomPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        bottomPanel.add(startDateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = ++row;
        bottomPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1;
        bottomPanel.add(endDateSpinner, gbc);

        // Application buttons
        JButton applyToRangeBtn = new JButton("Apply to Date Range");
        JButton applyToAllBtn = new JButton("Apply to All Meals");

        applyToRangeBtn.setEnabled(false);
        applyToAllBtn.setEnabled(false);

        gbc.gridx = 0;
        gbc.gridy = ++row;
        bottomPanel.add(applyToRangeBtn, gbc);
        gbc.gridx = 1;
        bottomPanel.add(applyToAllBtn, gbc);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Add listener to enable/disable date range components
        customRangeCheck.addActionListener(e -> {
            boolean enabled = customRangeCheck.isSelected();
            startDateSpinner.setEnabled(enabled);
            endDateSpinner.setEnabled(enabled);
        });

        // Table selection listener
        savedSwapsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = savedSwapsTable.getSelectedRow() != -1;
                deleteBtn.setEnabled(hasSelection);
                applyToRangeBtn.setEnabled(hasSelection);
                applyToAllBtn.setEnabled(hasSelection);
            }
        });

        // Refresh button listener
        refreshBtn.addActionListener(e -> loadSavedSwaps(tableModel));

        // Delete button listener
        deleteBtn.addActionListener(e -> {
            int selectedRow = savedSwapsTable.getSelectedRow();
            if (selectedRow != -1) {
                String swapName = (String) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(panel,
                        "Are you sure you want to delete the saved swap '" + swapName + "'?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        // Get the SavedSwapRequest object stored in the table model
                        SavedSwapRequest savedSwap = getSavedSwapFromTable(tableModel, selectedRow);
                        DatabaseSavedSwapRequestDAO dao = new DatabaseSavedSwapRequestDAO();
                        dao.delete(savedSwap.getId());

                        JOptionPane.showMessageDialog(panel, "Saved swap '" + swapName + "' deleted successfully.");
                        loadSavedSwaps(tableModel); // Refresh the list

                    } catch (Exception ex) {
                        showError(panel, "Failed to delete saved swap: " + ex.getMessage());
                    }
                }
            }
        });

        // Apply to range button listener
        applyToRangeBtn.addActionListener(e -> {
            int selectedRow = savedSwapsTable.getSelectedRow();
            if (selectedRow != -1) {
                applySavedSwap(panel, tableModel, selectedRow, customRangeCheck, startDateSpinner, endDateSpinner,
                        false);
            }
        });

        // Apply to all button listener
        applyToAllBtn.addActionListener(e -> {
            int selectedRow = savedSwapsTable.getSelectedRow();
            if (selectedRow != -1) {
                applySavedSwap(panel, tableModel, selectedRow, customRangeCheck, startDateSpinner, endDateSpinner,
                        true);
            }
        });

        // Load initial data
        loadSavedSwaps(tableModel);

        return panel;
    }

    private static JPanel buildSwapCompareTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top panel for date range controls
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date Range Selection
        JCheckBox customRangeCheck = new JCheckBox("Custom Date Range", false);
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd"));
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd"));

        // Initially disable date spinners
        startDateSpinner.setEnabled(false);
        endDateSpinner.setEnabled(false);

        // Set default dates (yesterday to today)
        startDateSpinner
                .setValue(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        endDateSpinner.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        topPanel.add(customRangeCheck, gbc);
        gbc.gridwidth = 1;
        row++;

        // Quick select buttons
        JPanel quickSelectPanel2 = new JPanel(new FlowLayout());
        JButton last24HoursBtn2 = new JButton("Last 24h");
        JButton last3DaysBtn2 = new JButton("Last 3 days");
        JButton lastWeekBtn2 = new JButton("Last week");
        JButton lastMonthBtn2 = new JButton("Last month");

        quickSelectPanel2.add(last24HoursBtn2);
        quickSelectPanel2.add(last3DaysBtn2);
        quickSelectPanel2.add(lastWeekBtn2);
        quickSelectPanel2.add(lastMonthBtn2);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        topPanel.add(quickSelectPanel2, gbc);
        gbc.gridwidth = 1;
        row++;

        // Add listeners for quick select buttons
        last24HoursBtn2.addActionListener(e -> {
            customRangeCheck.setSelected(true);
            startDateSpinner.setEnabled(true);
            endDateSpinner.setEnabled(true);
            startDateSpinner
                    .setValue(Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            endDateSpinner.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        });

        last3DaysBtn2.addActionListener(e -> {
            customRangeCheck.setSelected(true);
            startDateSpinner.setEnabled(true);
            endDateSpinner.setEnabled(true);
            startDateSpinner
                    .setValue(Date.from(LocalDate.now().minusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            endDateSpinner.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        });

        lastWeekBtn2.addActionListener(e -> {
            customRangeCheck.setSelected(true);
            startDateSpinner.setEnabled(true);
            endDateSpinner.setEnabled(true);
            startDateSpinner.setValue(
                    Date.from(LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            endDateSpinner.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        });

        lastMonthBtn2.addActionListener(e -> {
            customRangeCheck.setSelected(true);
            startDateSpinner.setEnabled(true);
            endDateSpinner.setEnabled(true);
            startDateSpinner.setValue(
                    Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            endDateSpinner.setValue(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        });

        gbc.gridx = 0;
        gbc.gridy = row;
        topPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        topPanel.add(startDateSpinner, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        topPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1;
        topPanel.add(endDateSpinner, gbc);
        row++;

        // Add listener to enable/disable date range components
        customRangeCheck.addActionListener(e -> {
            boolean enabled = customRangeCheck.isSelected();
            startDateSpinner.setEnabled(enabled);
            endDateSpinner.setEnabled(enabled);
        });

        JButton loadComparisonButton = new JButton("Compare Swaps");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        topPanel.add(loadComparisonButton, gbc);

        JTextPane resultPane = new JTextPane();
        resultPane.setContentType("text/html");
        resultPane.setEditable(false);

        loadComparisonButton.addActionListener(e -> {
            if (currentUser == null) {
                showError(panel, "Please log in first.");
                return;
            }

            // Create date range based on user selection
            DateRange range;
            if (customRangeCheck.isSelected()) {
                LocalDate startDate = ((Date) startDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate endDate = ((Date) endDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate();

                // Validate date range
                if (startDate.isAfter(endDate)) {
                    showError(panel, "Start date cannot be after end date.");
                    return;
                }

                // Limit to 90 days to prevent performance issues
                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                if (daysBetween > 90) {
                    showError(panel, "Date range cannot exceed 90 days. Selected range: " + daysBetween + " days.");
                    return;
                }

                range = new DateRange(startDate, endDate);
                System.out.println("Comparing swaps for custom date range: " + startDate + " to " + endDate + " ("
                        + daysBetween + " days)");
            } else {
                // Default to last 24 hours
                range = new DateRange(LocalDate.now().minusDays(1), LocalDate.now());
                System.out.println("Comparing swaps for default date range: last 24 hours");
            }
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
            StringBuilder sb = new StringBuilder("<html><body style='font-family:monospace;'>");

            for (int i = 0; i < originalMeals.size(); i++) {
                Meal orig = originalMeals.get(i);
                Meal swap = currentMeals.get(i);

                sb.append("<h3>Date: ").append(orig.getDate()).append("</h3>");

                sb.append("<b>Original Meal:</b><ul>");
                for (Food f : orig.getItems()) {
                    sb.append("<li>").append(f.getName()).append(" (").append(f.getQuantity()).append("g)</li>");
                }
                sb.append("</ul>");

                sb.append("<b>Swapped Meal:</b><ul>");
                for (Food f : swap.getItems()) {
                    String itemStr = f.getName() + " (" + f.getQuantity() + "g)";
                    boolean isNew = orig.getItems().stream()
                            .noneMatch(of -> of.getName().equalsIgnoreCase(f.getName()));
                    if (isNew) {
                        sb.append("<li><span style='color:blue;font-weight:bold;'>").append(itemStr)
                                .append("</span></li>");
                    } else {
                        sb.append("<li>").append(itemStr).append("</li>");
                    }
                }
                sb.append("</ul>");

                Map<NutrientType, Double> origNutrients = calc.calculate(orig);
                Map<NutrientType, Double> swapNutrients = calc.calculate(swap);

                sb.append("<b>Nutrient Comparison:</b>");
                sb.append("<table border='1' cellspacing='0' cellpadding='4'>");
                sb.append("<tr><th>Nutrient</th><th>Before</th><th>After</th><th>Change</th></tr>");

                for (NutrientType t : NutrientType.values()) {
                    double before = origNutrients.getOrDefault(t, 0.0);
                    double after = swapNutrients.getOrDefault(t, 0.0);
                    double diff = after - before;
                    String arrow = diff > 0 ? "↑" : (diff < 0 ? "↓" : "→");

                    sb.append("<tr>");
                    sb.append("<td>").append(t.name()).append("</td>");
                    sb.append("<td>").append(String.format("%.1f", before)).append("</td>");
                    sb.append("<td>").append(String.format("%.1f", after)).append("</td>");
                    sb.append("<td>").append(String.format("%+.1f %s", diff, arrow)).append("</td>");
                    sb.append("</tr>");
                }
                sb.append("</table><hr>");
            }

            sb.append("</body></html>");
            resultPane.setText(sb.toString());
            resultPane.setCaretPosition(0);
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultPane), BorderLayout.CENTER);
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

        StringBuilder breakdown = new StringBuilder("Today's Nutrient Breakdown:\n\n");

        // Display all nutrients including actual calories
        for (NutrientType type : NutrientType.values()) {
            double value = nutrientMap.getOrDefault(type, 0.0);
            breakdown.append(type.name()).append(": ").append(String.format("%.2f", value));
            if (type == NutrientType.Calories) {
                breakdown.append(" kcal");
            } else {
                breakdown.append("g");
            }
            breakdown.append("\n");
        }

        // Add estimation note if calories are missing from database
        double actualCalories = nutrientMap.getOrDefault(NutrientType.Calories, 0.0);
        if (actualCalories <= 0) {
            double protein = nutrientMap.getOrDefault(NutrientType.Protein, 0.0);
            double carbs = nutrientMap.getOrDefault(NutrientType.Carbohydrate, 0.0);
            double fat = nutrientMap.getOrDefault(NutrientType.Fat, 0.0);
            double estimatedCalories = (protein * 4) + (carbs * 4) + (fat * 9);
            breakdown.append("\nNote: Estimated Calories (fallback): ").append(String.format("%.2f", estimatedCalories))
                    .append(" kcal");
        }

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

                // Use actual calories from database, fallback to estimation if not available
                double actualCalories = nutrientMap.getOrDefault(NutrientType.Calories, 0.0);
                if (actualCalories > 0) {
                    sb.append("Calories: ").append(String.format("%.2f", actualCalories)).append(" kcal\n\n");
                } else {
                    // Fallback to estimation if database calories are missing
                    double protein = nutrientMap.getOrDefault(NutrientType.Protein, 0.0);
                    double carbs = nutrientMap.getOrDefault(NutrientType.Carbohydrate, 0.0);
                    double fat = nutrientMap.getOrDefault(NutrientType.Fat, 0.0);
                    double estimatedCalories = (protein * 4) + (carbs * 4) + (fat * 9);
                    sb.append("Estimated Calories: ").append(String.format("%.2f", estimatedCalories))
                            .append(" kcal\n\n");
                }

            }

            journalArea.setText(sb.toString());
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel buildAnalysisTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Configuration Panel (North)
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(BorderFactory.createTitledBorder("Analysis Configuration"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Analyzer Type Selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        configPanel.add(new JLabel("Analyzer Type:"), gbc);

        String[] analyzerTypes = { "NutrientAnalyzer", "FoodGroupAnalyzer", "CFGComparer" };
        JComboBox<String> analyzerTypeBox = new JComboBox<>(analyzerTypes);
        gbc.gridx = 1;
        configPanel.add(analyzerTypeBox, gbc);

        // Chart Type Selection
        gbc.gridx = 2;
        gbc.gridy = 0;
        configPanel.add(new JLabel("Chart Type:"), gbc);

        JComboBox<ChartType> chartTypeBox = new JComboBox<>(ChartType.values());
        gbc.gridx = 3;
        configPanel.add(chartTypeBox, gbc);

        // Date Range Selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        configPanel.add(new JLabel("Start Date:"), gbc);

        LocalDate defaultStart = LocalDate.now().minusDays(7); // Default to last 7 days
        Date defaultStartDate = Date.from(defaultStart.atStartOfDay(ZoneId.systemDefault()).toInstant());
        JSpinner startDateSpinner = new JSpinner(
                new SpinnerDateModel(defaultStartDate, null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startDateEditor);
        gbc.gridx = 1;
        configPanel.add(startDateSpinner, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        configPanel.add(new JLabel("End Date:"), gbc);

        LocalDate defaultEnd = LocalDate.now();
        Date defaultEndDate = Date.from(defaultEnd.atStartOfDay(ZoneId.systemDefault()).toInstant());
        JSpinner endDateSpinner = new JSpinner(
                new SpinnerDateModel(defaultEndDate, null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        endDateSpinner.setEditor(endDateEditor);
        gbc.gridx = 3;
        configPanel.add(endDateSpinner, gbc);

        // Nutrient Filter Selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        configPanel.add(new JLabel("Nutrients:"), gbc);

        JPanel nutrientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox proteinCB = new JCheckBox("Protein", true);
        JCheckBox carbsCB = new JCheckBox("Carbohydrate", true);
        JCheckBox fatCB = new JCheckBox("Fat", true);
        JCheckBox fiberCB = new JCheckBox("Fiber", true);
        JCheckBox caloriesCB = new JCheckBox("Calories", true);

        nutrientPanel.add(proteinCB);
        nutrientPanel.add(carbsCB);
        nutrientPanel.add(fatCB);
        nutrientPanel.add(fiberCB);
        nutrientPanel.add(caloriesCB);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        configPanel.add(nutrientPanel, gbc);

        // Before/After Swap Comparison Toggle
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        configPanel.add(new JLabel("Show Swap Comparison:"), gbc);

        JCheckBox showSwapComparisonCB = new JCheckBox("Before/After Swaps");
        gbc.gridx = 1;
        configPanel.add(showSwapComparisonCB, gbc);

        panel.add(configPanel, BorderLayout.NORTH);

        // Action Panel (South)
        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton analyzeButton = new JButton("Run Analysis");

        // Unified Analysis Action Handler
        analyzeButton.addActionListener(e -> {
            try {
                // Validate user is logged in
                if (currentUser == null) {
                    showError(panel, "Please log in first.");
                    return;
                }

                // Get configuration values
                String selectedAnalyzer = (String) analyzerTypeBox.getSelectedItem();
                ChartType selectedChartType = (ChartType) chartTypeBox.getSelectedItem();
                boolean showSwapComparison = showSwapComparisonCB.isSelected();

                // Get date range
                Date startDate = (Date) startDateSpinner.getValue();
                Date endDate = (Date) endDateSpinner.getValue();
                LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                if (startLocalDate.isAfter(endLocalDate)) {
                    showError(panel, "Start date must be before end date.");
                    return;
                }

                DateRange dateRange = new DateRange(startLocalDate, endLocalDate);

                // Get selected nutrients
                List<NutrientType> selectedNutrients = new ArrayList<>();
                if (proteinCB.isSelected())
                    selectedNutrients.add(NutrientType.Protein);
                if (carbsCB.isSelected())
                    selectedNutrients.add(NutrientType.Carbohydrate);
                if (fatCB.isSelected())
                    selectedNutrients.add(NutrientType.Fat);
                if (fiberCB.isSelected())
                    selectedNutrients.add(NutrientType.Fiber);
                if (caloriesCB.isSelected())
                    selectedNutrients.add(NutrientType.Calories);

                if (selectedNutrients.isEmpty()) {
                    showError(panel, "Please select at least one nutrient.");
                    return;
                }

                // Create VisualizationOps with user selections
                VisualizationOps ops = new VisualizationOps(dateRange, selectedNutrients,
                        selectedNutrients.size(), true, selectedChartType, showSwapComparison);

                // Get meals based on date range using proper database filtering
                List<Meal> meals = intakeLog.getMealsBetween(currentUser.getUserID(), dateRange);
                if (meals == null || meals.isEmpty()) {
                    showError(panel, "No meals found in the selected date range.");
                    return;
                }

                // For swap comparison, also get original meals
                List<Meal> originalMeals = null;
                if (showSwapComparison) {
                    System.out.println("[MainUI] Attempting to retrieve original meals for comparison");
                    originalMeals = intakeLog.getOriginalMealsBetween(currentUser.getUserID(), dateRange);

                    if (originalMeals == null || originalMeals.isEmpty()) {
                        System.out
                                .println("[MainUI] No original meal data found - proceeding with empty 'BEFORE' data");
                        // Create empty list instead of failing - allows comparison with placeholder
                        // "BEFORE" chart
                        originalMeals = new ArrayList<>();
                    } else {
                        System.out.println("[MainUI] Found " + originalMeals.size() + " original meals for comparison");
                    }
                }

                // Run the selected analyzer with comparison data if needed
                if (showSwapComparison) {
                    System.out.println("[MainUI] Running comparison analysis with original meals: " +
                            (originalMeals != null ? originalMeals.size() : "NULL"));
                    runComparisonAnalysis(selectedAnalyzer, originalMeals, meals, ops, panel);
                } else {
                    runAnalysis(selectedAnalyzer, meals, ops, panel);
                }

            } catch (Exception ex) {
                showError(panel, "Analysis failed: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        actionPanel.add(analyzeButton);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Helper method to run the selected analysis
    private static void runAnalysis(String analyzerType, List<Meal> meals, VisualizationOps ops, JPanel parentPanel) {
        try {
            System.out.println("[MainUI] Starting analysis: " + analyzerType);
            System.out.println("[MainUI] Meals count: " + meals.size());
            System.out.println("[MainUI] Chart type: " + ops.getChartType());
            System.out.println("[MainUI] Selected nutrients: " + ops.getNutrients());

            ChartPanel chartPanel = null;
            String chartTitle = analyzerType;

            switch (analyzerType) {
                case "FoodGroupAnalyzer" -> {
                    System.out.println("[MainUI] Running FoodGroupAnalyzer...");
                    Analyzer<List<Meal>, FoodGroupStats> analyzer = analyzerFactory.createFoodGroupAnalyzer();
                    FoodGroupStats result = analyzer.analyze(meals);
                    System.out.println(
                            "[MainUI] FoodGroupAnalyzer completed. Result: " + (result != null ? "SUCCESS" : "NULL"));
                    if (result != null) {
                        System.out.println(
                                "[MainUI] Group percentages: " + result.getGroupPercentages().size() + " groups");
                    }

                    Map<String, Double> chartData = new HashMap<>();
                    result.getGroupPercentages().forEach((k, v) -> chartData.put(k.name(), v));
                    System.out.println("[MainUI] Converted chart data size: " + chartData.size());

                    switch (ops.getChartType()) {
                        case PIE -> chartPanel = Visualizer.createPieChartPanel(chartData, "Food Group Distribution");
                        case BAR ->
                            chartPanel = Visualizer.createBarChartFromSimpleData(chartData, "Food Group Bar View");
                        case LINE ->
                            chartPanel = Visualizer.createLineChartFromSimpleData(chartData, "Food Group Line View");
                    }
                    chartTitle = "Food Group Analysis";
                }

                case "CFGComparer" -> {
                    System.out.println("[MainUI] Running CFGComparer...");
                    Analyzer<List<Meal>, FoodGroupStats> analyzer = analyzerFactory.createFoodGroupAnalyzer();
                    FoodGroupStats stats = analyzer.analyze(meals);
                    System.out.println("[MainUI] FoodGroupStats for CFG: " + (stats != null ? "SUCCESS" : "NULL"));
                    AlignmentScore result = cfgComparer.analyze(stats, CFGVersion.V2019);
                    System.out.println(
                            "[MainUI] CFGComparer completed. Score: " + (result != null ? result.getScore() : "NULL"));

                    switch (ops.getChartType()) {
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
                            chartPanel = Visualizer.createLineChartFromSimpleData(chartData, "CFG Alignment Score");
                        }
                    }

                    // Add score label for CFG
                    JLabel scoreLabel = new JLabel(String.format("Average Alignment Score: %.1f%%", result.getScore()));
                    scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                    JPanel wrapper = new JPanel(new BorderLayout());
                    wrapper.add(scoreLabel, BorderLayout.NORTH);
                    wrapper.add(chartPanel, BorderLayout.CENTER);

                    JFrame chartFrame = new JFrame("CFG Comparison");
                    chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    chartFrame.add(wrapper);
                    chartFrame.pack();
                    chartFrame.setLocationRelativeTo(null);
                    chartFrame.setVisible(true);
                    return; // Early return for CFG special case
                }

                case "NutrientAnalyzer" -> {
                    System.out.println("[MainUI] Running NutrientAnalyzer...");
                    Analyzer<List<Meal>, NutrientStats> analyzer = analyzerFactory.createNutrientAnalyzer();
                    NutrientStats result = analyzer.analyze(meals);
                    System.out.println(
                            "[MainUI] NutrientAnalyzer completed. Result: " + (result != null ? "SUCCESS" : "NULL"));
                    if (result != null) {
                        System.out.println("[MainUI] Top nutrients: " + result.getTopNutrients().size() + " nutrients");
                        System.out.println("[MainUI] Total items: " + result.getTotalItems());
                    }

                    Visualizer visualizer = new Visualizer();
                    Map<String, Double> chartData = visualizer.convertToChartData(result, ops);
                    System.out.println("[MainUI] Converted chart data size: " + chartData.size());

                    switch (ops.getChartType()) {
                        case PIE -> chartPanel = Visualizer.createPieChartPanel(chartData, "Nutrient Distribution");
                        case BAR ->
                            chartPanel = Visualizer.createBarChartFromSimpleData(chartData, "Nutrient Bar View");
                        case LINE ->
                            chartPanel = Visualizer.createLineChartFromSimpleData(chartData, "Nutrient Line View");
                    }
                    chartTitle = "Nutrient Analysis";
                }

                default -> {
                    showError(parentPanel, "Unknown analyzer type: " + analyzerType);
                    return;
                }
            }

            if (chartPanel == null) {
                System.out.println("[MainUI] ERROR: chartPanel is null!");
                showError(parentPanel, "Failed to generate chart.");
                return;
            }

            System.out.println("[MainUI] Chart created successfully, displaying in new frame");
            // Display the chart on EDT
            final ChartPanel finalChartPanel = chartPanel;
            final String finalTitle = chartTitle + " - " + ops.getChartType();
            SwingUtilities.invokeLater(() -> {
                try {
                    System.out.println("[MainUI] Creating chart frame on EDT");
                    JFrame chartFrame = new JFrame(finalTitle);
                    chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    chartFrame.add(finalChartPanel);
                    chartFrame.pack();
                    chartFrame.setLocationRelativeTo(null);
                    chartFrame.setVisible(true);
                    System.out.println("[MainUI] Chart frame displayed successfully");
                } catch (Exception frameEx) {
                    System.err.println("[MainUI] Error displaying chart frame: " + frameEx.getMessage());
                    frameEx.printStackTrace();
                }
            });

        } catch (Exception ex) {
            showError(parentPanel, "Analysis failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Helper method to run comparison analysis (before/after swaps)
    private static void runComparisonAnalysis(String analyzerType, List<Meal> originalMeals, List<Meal> currentMeals,
            VisualizationOps ops, JPanel parentPanel) {
        try {
            System.out.println("[MainUI] Starting comparison analysis: " + analyzerType);
            System.out.println("[MainUI] Original meals: " + (originalMeals != null ? originalMeals.size() : "NULL"));
            System.out.println("[MainUI] Current meals: " + (currentMeals != null ? currentMeals.size() : "NULL"));

            // Validate that we have meaningful swap data
            if (!hasValidSwapData(originalMeals)) {
                showError(parentPanel,
                        "No swap history available for comparison. Perform meal swaps first to see before/after analysis.");
                return;
            }

            System.out.println("[MainUI] Chart type: " + ops.getChartType());
            System.out.println("[MainUI] Selected nutrients: " + ops.getNutrients());

            JPanel beforeChartPanel = null;
            JPanel afterChartPanel = null;
            Map<String, Map<String, Double>> summaryData = null;
            String chartTitle = analyzerType + " - Before/After Swap Comparison";

            // Run analyzer-specific comparison based on selected analyzer type
            switch (analyzerType) {
                case "FoodGroupAnalyzer" -> {
                    System.out.println("[MainUI] Running FoodGroupAnalyzer comparison");
                    Analyzer<List<Meal>, FoodGroupStats> foodGroupAnalyzer = analyzerFactory.createFoodGroupAnalyzer();

                    FoodGroupStats beforeResult = foodGroupAnalyzer.analyze(originalMeals);
                    FoodGroupStats afterResult = foodGroupAnalyzer.analyze(currentMeals);

                    Map<String, Map<String, Double>> comparisonData = prepareFoodGroupComparisonData(beforeResult,
                            afterResult);
                    summaryData = comparisonData;
                    beforeChartPanel = createComparisonChart(comparisonData.get("Before"), "BEFORE SWAPS - Food Groups",
                            ops.getChartType());
                    afterChartPanel = createComparisonChart(comparisonData.get("After"), "AFTER SWAPS - Food Groups",
                            ops.getChartType());
                }
                case "NutrientAnalyzer" -> {
                    System.out.println("[MainUI] Running NutrientAnalyzer comparison");
                    Analyzer<List<Meal>, NutrientStats> nutrientAnalyzer = analyzerFactory.createNutrientAnalyzer();

                    NutrientStats beforeResult = nutrientAnalyzer.analyze(originalMeals);
                    NutrientStats afterResult = nutrientAnalyzer.analyze(currentMeals);

                    Map<String, Map<String, Double>> comparisonData = prepareNutrientAnalyzerComparisonData(
                            beforeResult, afterResult, ops);
                    summaryData = comparisonData;
                    beforeChartPanel = createComparisonChart(comparisonData.get("Before"), "BEFORE SWAPS - Nutrients",
                            ops.getChartType());
                    afterChartPanel = createComparisonChart(comparisonData.get("After"), "AFTER SWAPS - Nutrients",
                            ops.getChartType());
                }
                case "CFGComparer" -> {
                    System.out.println("[MainUI] Running CFGComparer comparison");
                    Analyzer<List<Meal>, FoodGroupStats> foodGroupAnalyzer = analyzerFactory.createFoodGroupAnalyzer();

                    FoodGroupStats beforeStats = foodGroupAnalyzer.analyze(originalMeals);
                    FoodGroupStats afterStats = foodGroupAnalyzer.analyze(currentMeals);

                    AlignmentScore beforeResult = cfgComparer.analyze(beforeStats, CFGVersion.V2019);
                    AlignmentScore afterResult = cfgComparer.analyze(afterStats, CFGVersion.V2019);

                    Map<String, Map<String, Double>> comparisonData = prepareCFGComparisonData(beforeResult,
                            afterResult);
                    // Create CFG-specific summary data that includes overall scores
                    summaryData = prepareCFGSummaryData(beforeResult, afterResult);

                    // Create charts with food group details only
                    JPanel beforeChart = createComparisonChart(comparisonData.get("Before"),
                            "BEFORE SWAPS - CFG Alignment", ops.getChartType());
                    JPanel afterChart = createComparisonChart(comparisonData.get("After"),
                            "AFTER SWAPS - CFG Alignment", ops.getChartType());

                    // Add score labels above each chart (similar to single CFG mode)
                    double beforeScore = beforeResult != null ? beforeResult.getScore() : 0.0;
                    double afterScore = afterResult != null ? afterResult.getScore() : 0.0;

                    JLabel beforeScoreLabel = new JLabel(String.format("Before Swaps: %.1f%%", beforeScore));
                    beforeScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    beforeScoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                    JLabel afterScoreLabel = new JLabel(String.format("After Swaps: %.1f%%", afterScore));
                    afterScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    afterScoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                    // Wrap each chart with its score label
                    JPanel beforeWrapper = new JPanel(new BorderLayout());
                    beforeWrapper.add(beforeScoreLabel, BorderLayout.NORTH);
                    beforeWrapper.add(beforeChart, BorderLayout.CENTER);

                    JPanel afterWrapper = new JPanel(new BorderLayout());
                    afterWrapper.add(afterScoreLabel, BorderLayout.NORTH);
                    afterWrapper.add(afterChart, BorderLayout.CENTER);

                    // Use the wrapped panels as chart panels
                    beforeChartPanel = beforeWrapper;
                    afterChartPanel = afterWrapper;
                }
                default -> {
                    System.out.println("[MainUI] ERROR: Unknown analyzer type for comparison: " + analyzerType);
                    showError(parentPanel, "Unknown analyzer type: " + analyzerType);
                    return;
                }
            }

            if (beforeChartPanel == null || afterChartPanel == null) {
                System.out.println("[MainUI] ERROR: Failed to generate comparison charts");
                showError(parentPanel, "Failed to generate comparison charts for " + analyzerType);
                return;
            }

            System.out.println(
                    "[MainUI] Both analyzer-specific charts created successfully, preparing side-by-side display");

            // Add summary information
            JPanel summaryPanel = createComparisonSummary(summaryData, ops.getNutrients());

            // Create side-by-side chart panel
            JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 1 row, 2 columns, 10px horizontal gap
            chartsPanel.add(beforeChartPanel);
            chartsPanel.add(afterChartPanel);

            // Create wrapper panel with summary at top and side-by-side charts below
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.add(summaryPanel, BorderLayout.NORTH);
            wrapper.add(chartsPanel, BorderLayout.CENTER);

            // Display the comparison charts on EDT (consistent with regular analysis)
            final JPanel finalWrapper = wrapper;
            final String finalTitle = chartTitle;
            SwingUtilities.invokeLater(() -> {
                try {
                    System.out.println("[MainUI] Creating comparison chart frame on EDT");
                    JFrame chartFrame = new JFrame(finalTitle);
                    chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    chartFrame.add(finalWrapper);
                    chartFrame.pack();
                    chartFrame.setLocationRelativeTo(null);
                    chartFrame.setVisible(true);
                    System.out.println("[MainUI] Comparison chart frame displayed successfully");
                } catch (Exception frameEx) {
                    System.err.println("[MainUI] Error displaying comparison chart frame: " + frameEx.getMessage());
                    frameEx.printStackTrace();
                }
            });

        } catch (Exception ex) {
            showError(parentPanel, "Comparison analysis failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Helper method to distinguish between food group data and nutrient data
    private static boolean isFoodGroupData(Map<String, Double> data) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        
        // Check for nutrient-specific keys that definitively indicate nutrient data
        // These keys only exist in NutrientType, not in FoodGroup
        if (data.containsKey("Carbohydrate") || data.containsKey("Fat") || 
            data.containsKey("Fiber") || data.containsKey("Calories")) {
            return false; // Definitely nutrient data
        }
        
        // Check for food group-specific keys that don't conflict with nutrients
        // These keys only exist in FoodGroup, not in NutrientType
        return data.containsKey("Grains") || data.containsKey("Vegetables") || 
               data.containsKey("Fruit") || data.containsKey("Dairy");
    }

    // Helper method to create summary panel showing differences
    private static JPanel createComparisonSummary(Map<String, Map<String, Double>> comparisonData,
            List<NutrientType> nutrients) {
        JPanel summaryPanel = new JPanel(new GridLayout(0, 1));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Swap Impact Summary"));

        Map<String, Double> beforeData = comparisonData.get("Before");
        Map<String, Double> afterData = comparisonData.get("After");

        // Check if this is CFG alignment data (contains "Overall Alignment" key)
        boolean isCFGData = (beforeData != null && beforeData.containsKey("Overall Alignment")) ||
                (afterData != null && afterData.containsKey("Overall Alignment"));
        
        // Check if this is Food Group data using robust detection method
        boolean isFoodGroupData = false;
        if (!isCFGData) {
            // Check both before and after data for food group indicators
            isFoodGroupData = isFoodGroupData(beforeData) || isFoodGroupData(afterData);
        }

        if (isCFGData) {
            // Handle CFG alignment summary
            createCFGAlignmentSummary(summaryPanel, beforeData, afterData);
        } else if (isFoodGroupData) {
            // Handle food group percentage summary
            createFoodGroupSummary(summaryPanel, beforeData, afterData);
        } else {
            // Handle regular nutrient summary
            createNutrientSummary(summaryPanel, beforeData, afterData, nutrients);
        }

        return summaryPanel;
    }
    
    private static void createFoodGroupSummary(JPanel summaryPanel, Map<String, Double> beforeData, Map<String, Double> afterData) {
        // Check if we have original data or not
        boolean hasOriginalData = beforeData != null && beforeData.values().stream().anyMatch(v -> v > 0.0);

        if (!hasOriginalData) {
            // Add informational message when no swap history exists
            JLabel infoLabel = new JLabel("No swap history found - showing current food group distribution only");
            infoLabel.setForeground(Color.BLUE.darker());
            infoLabel.setFont(infoLabel.getFont().deriveFont(Font.ITALIC));
            summaryPanel.add(infoLabel);

            // Show current food group percentages
            JLabel currentLabel = new JLabel("Current Food Group Distribution:");
            currentLabel.setFont(currentLabel.getFont().deriveFont(Font.BOLD));
            summaryPanel.add(currentLabel);

            // Order food groups logically
            String[] orderedGroups = {"Grains", "Vegetables", "Fruit", "Protein", "Dairy", "Other"};
            for (String groupName : orderedGroups) {
                double percentage = afterData.getOrDefault(groupName, 0.0);
                if (percentage > 0.0) {
                    String currentText = String.format("  %s: %.1f%%", groupName, percentage);
                    JLabel currentValueLabel = new JLabel(currentText);
                    summaryPanel.add(currentValueLabel);
                }
            }
        } else {
            // Show food group percentage changes
            JLabel changesLabel = new JLabel("Food Group Distribution Changes:");
            changesLabel.setFont(changesLabel.getFont().deriveFont(Font.BOLD));
            summaryPanel.add(changesLabel);

            // Show significant food group changes (threshold: 2% change)
            boolean hasSignificantChanges = false;
            String[] orderedGroups = {"Grains", "Vegetables", "Fruit", "Protein", "Dairy", "Other"};
            
            for (String groupName : orderedGroups) {
                double before = beforeData.getOrDefault(groupName, 0.0);
                double after = afterData.getOrDefault(groupName, 0.0);
                double change = after - before;

                if (Math.abs(change) >= 2.0) { // Show changes >= 2%
                    hasSignificantChanges = true;
                    String changeText = String.format("  %s: %.1f%% → %.1f%% (%.1f%% %s)",
                            groupName, before, after, Math.abs(change),
                            change >= 0 ? "increase" : "decrease");

                    JLabel changeLabel = new JLabel(changeText);
                    changeLabel.setForeground(change >= 0 ? Color.GREEN.darker() : Color.RED.darker());
                    summaryPanel.add(changeLabel);
                }
            }

            if (!hasSignificantChanges) {
                JLabel noChangesLabel = new JLabel("  No significant food group changes (< 2%)");
                noChangesLabel.setForeground(Color.GRAY);
                noChangesLabel.setFont(noChangesLabel.getFont().deriveFont(Font.ITALIC));
                summaryPanel.add(noChangesLabel);
            }
        }
    }

    private static void createCFGAlignmentSummary(JPanel summaryPanel, Map<String, Double> beforeData,
            Map<String, Double> afterData) {
        // Check if we have original data or not
        boolean hasOriginalData = beforeData != null && beforeData.values().stream().anyMatch(v -> v > 0.0);

        if (!hasOriginalData) {
            // Add informational message when no swap history exists
            JLabel infoLabel = new JLabel("No swap history found - showing current CFG alignment only");
            infoLabel.setForeground(Color.BLUE.darker());
            infoLabel.setFont(infoLabel.getFont().deriveFont(Font.ITALIC));
            summaryPanel.add(infoLabel);

            // Show current alignment score
            double currentOverall = afterData.getOrDefault("Overall Alignment", 0.0);
            JLabel currentLabel = new JLabel(String.format("Current Overall Alignment: %.1f%%", currentOverall));
            currentLabel.setFont(currentLabel.getFont().deriveFont(Font.BOLD));
            summaryPanel.add(currentLabel);
        } else {
            // Show CFG alignment changes
            double beforeOverall = beforeData.getOrDefault("Overall Alignment", 0.0);
            double afterOverall = afterData.getOrDefault("Overall Alignment", 0.0);
            double overallChange = afterOverall - beforeOverall;

            String overallText = String.format("Overall Alignment: %.1f%% → %.1f%% (%.1f%% %s)",
                    beforeOverall, afterOverall, Math.abs(overallChange),
                    overallChange >= 0 ? "improvement" : "decline");

            JLabel overallLabel = new JLabel(overallText);
            overallLabel.setFont(overallLabel.getFont().deriveFont(Font.BOLD));
            overallLabel.setForeground(overallChange >= 0 ? Color.GREEN.darker() : Color.RED.darker());
            summaryPanel.add(overallLabel);

            // Show significant food group changes (threshold: 5% change)
            JLabel groupChangesLabel = new JLabel("Significant Food Group Changes:");
            groupChangesLabel.setFont(groupChangesLabel.getFont().deriveFont(Font.BOLD));
            summaryPanel.add(groupChangesLabel);

            boolean hasSignificantChanges = false;
            for (String key : beforeData.keySet()) {
                if (!"Overall Alignment".equals(key)) { // Skip overall score, show food groups only
                    double before = beforeData.getOrDefault(key, 0.0);
                    double after = afterData.getOrDefault(key, 0.0);
                    double change = after - before;

                    if (Math.abs(change) >= 5.0) { // Show changes >= 5%
                        hasSignificantChanges = true;
                        String changeText = String.format("  %s: %.1f%% → %.1f%% (%.1f%% %s)",
                                key, before, after, Math.abs(change),
                                change >= 0 ? "improvement" : "decline");

                        JLabel changeLabel = new JLabel(changeText);
                        changeLabel.setForeground(change >= 0 ? Color.GREEN.darker() : Color.RED.darker());
                        summaryPanel.add(changeLabel);
                    }
                }
            }

            if (!hasSignificantChanges) {
                JLabel noChangesLabel = new JLabel("  No significant food group changes (< 5%)");
                noChangesLabel.setForeground(Color.GRAY);
                noChangesLabel.setFont(noChangesLabel.getFont().deriveFont(Font.ITALIC));
                summaryPanel.add(noChangesLabel);
            }
        }
    }

    private static void createNutrientSummary(JPanel summaryPanel, Map<String, Double> beforeData,
            Map<String, Double> afterData, List<NutrientType> nutrients) {
        // Check if we have original data or not
        boolean hasOriginalData = beforeData != null && beforeData.values().stream().anyMatch(v -> v > 0.0);

        if (!hasOriginalData) {
            // Add informational message when no swap history exists
            JLabel infoLabel = new JLabel("No swap history found - showing current meals vs. no original data");
            infoLabel.setForeground(Color.BLUE.darker());
            infoLabel.setFont(infoLabel.getFont().deriveFont(Font.ITALIC));
            summaryPanel.add(infoLabel);

            // Show current meal values only
            JLabel currentLabel = new JLabel("Current Meal Totals:");
            currentLabel.setFont(currentLabel.getFont().deriveFont(Font.BOLD));
            summaryPanel.add(currentLabel);

            for (NutrientType nutrient : nutrients) {
                String nutrientName = nutrient.name();
                double after = afterData.getOrDefault(nutrientName, 0.0);

                String currentText = String.format("  %s: %.1fg", nutrientName, after);
                JLabel currentValueLabel = new JLabel(currentText);
                summaryPanel.add(currentValueLabel);
            }
        } else {
            // Show normal before/after comparison when we have original data
            for (NutrientType nutrient : nutrients) {
                String nutrientName = nutrient.name();
                double before = beforeData.getOrDefault(nutrientName, 0.0);
                double after = afterData.getOrDefault(nutrientName, 0.0);
                double change = after - before;
                double percentChange = before > 0 ? ((change / before) * 100) : 0;

                String changeText = String.format("%s: %.1fg → %.1fg (%.1f%% %s)",
                        nutrientName, before, after, Math.abs(percentChange),
                        change >= 0 ? "increase" : "decrease");

                JLabel changeLabel = new JLabel(changeText);
                changeLabel.setForeground(change >= 0 ? Color.GREEN.darker() : Color.RED.darker());
                summaryPanel.add(changeLabel);
            }
        }
    }

    private static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, "Error:  " + msg, "", JOptionPane.ERROR_MESSAGE);
    }

    // Helper methods for Saved Swaps functionality
    private static void loadSavedSwaps(DefaultTableModel tableModel) {
        if (currentUser == null)
            return;

        try {
            DatabaseSavedSwapRequestDAO dao = new DatabaseSavedSwapRequestDAO();
            List<SavedSwapRequest> savedSwaps = dao.findByProfileId(currentUser.getUserID());

            // Clear existing data
            tableModel.setRowCount(0);

            // Add saved swaps to table
            for (SavedSwapRequest savedSwap : savedSwaps) {
                Object[] rowData = {
                        savedSwap.getName(),
                        savedSwap.getDescription() != null ? savedSwap.getDescription() : "",
                        savedSwap.getTargetDescription(),
                        savedSwap.getCreatedAt().toLocalDate().toString(),
                        savedSwap.getLastUsedAt() != null ? savedSwap.getLastUsedAt().toLocalDate().toString() : "Never"
                };
                tableModel.addRow(rowData);
            }

        } catch (Exception e) {
            System.err.println("Failed to load saved swaps: " + e.getMessage());
        }
    }

    private static SavedSwapRequest getSavedSwapFromTable(DefaultTableModel tableModel, int row) throws Exception {
        if (currentUser == null)
            throw new Exception("User not logged in");

        String name = (String) tableModel.getValueAt(row, 0);
        DatabaseSavedSwapRequestDAO dao = new DatabaseSavedSwapRequestDAO();
        List<SavedSwapRequest> matches = dao.findByProfileIdAndName(currentUser.getUserID(), name);

        for (SavedSwapRequest savedSwap : matches) {
            if (savedSwap.getName().equals(name)) {
                return savedSwap;
            }
        }
        throw new Exception("Saved swap not found: " + name);
    }

    private static void applySavedSwap(JPanel panel, DefaultTableModel tableModel, int selectedRow,
            JCheckBox customRangeCheck, JSpinner startDateSpinner, JSpinner endDateSpinner, boolean applyToAll) {

        if (currentUser == null) {
            showError(panel, "Please log in first.");
            return;
        }

        try {
            SavedSwapRequest savedSwap = getSavedSwapFromTable(tableModel, selectedRow);

            DateRange range;
            String rangeDescription;

            if (applyToAll) {
                // Apply to all meals ever - use a very wide date range
                range = new DateRange(LocalDate.of(2000, 1, 1), LocalDate.now().plusDays(1));
                rangeDescription = "meals";
            } else if (customRangeCheck.isSelected()) {
                LocalDate startDate = ((Date) startDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate endDate = ((Date) endDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate();

                // Validate date range
                if (startDate.isAfter(endDate)) {
                    showError(panel, "Start date cannot be after end date.");
                    return;
                }

                // Limit to 90 days to prevent performance issues
                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                if (daysBetween > 90) {
                    showError(panel, "Date range cannot exceed 90 days. Selected range: " + daysBetween + " days.");
                    return;
                }

                range = new DateRange(startDate, endDate);
                rangeDescription = "meals from " + startDate + " to " + endDate + " (" + daysBetween + " days)";
            } else {
                // Default to last 24 hours
                range = new DateRange(LocalDate.now().minusDays(1), LocalDate.now());
                rangeDescription = "meals from last 24 hours";
            }

            // Get meals in the specified range
            List<Meal> meals = intakeLog.getMealsBetween(currentUser.getUserID(), range);
            if (meals == null || meals.isEmpty()) {
                showError(panel, "No meals found in selected range.");
                return;
            }

            // Convert SavedSwapRequest to SwapRequest and apply
            SwapRequest swapRequest = savedSwap.toSwapRequest(currentUser, range);
            SwapEngine.SwapResult swapResult = swapEngine.applySwapWithResult(meals, swapRequest);

            // Update meals in database
            intakeLog.updateMealsFromSwap(currentUser.getUserID(), swapResult);

            // Update last used timestamp if swaps were applied
            if (swapResult.swapsWereApplied()) {
                DatabaseSavedSwapRequestDAO dao = new DatabaseSavedSwapRequestDAO();
                dao.updateLastUsedAt(savedSwap.getId());
            }

            // Show success message
            String swapType = savedSwap.hasSecondTarget() ? "Dual-target" : "Single-target";
            String message;
            if (swapResult.swapsWereApplied()) {
                message = swapType + " swap '" + savedSwap.getName() + "': " + swapResult.getSwapCount() +
                        " swaps applied to " + swapResult.getMeals().size() + " " + rangeDescription + ".\n" +
                        "Targets: " + savedSwap.getTargetDescription();
            } else {
                message = swapType + " swap '" + savedSwap.getName() + "' - no swaps needed (deficit too small) for " +
                        rangeDescription + ".\nTargets: " + savedSwap.getTargetDescription();
            }
            JOptionPane.showMessageDialog(panel, message);

            // Refresh the table to update the "Last Used" column
            loadSavedSwaps(tableModel);

        } catch (Exception ex) {
            showError(panel, "Failed to apply saved swap: " + ex.getMessage());
        }
    }

    // Helper method to validate if we have meaningful swap data
    private static boolean hasValidSwapData(List<Meal> originalMeals) {
        if (originalMeals == null || originalMeals.isEmpty()) {
            return false;
        }

        // Check if original meals actually have food items (not empty meals)
        int totalItems = 0;
        for (Meal meal : originalMeals) {
            totalItems += meal.getItems().size();
        }

        return totalItems > 0;
    }

    // Helper method to create comparison chart with proper chart type handling
    private static JPanel createComparisonChart(Map<String, Double> data, String title, ChartType chartType) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        return Visualizer.createSingleChart(data, title, chartType);
    }

    // Helper methods for analyzer-specific comparison data preparation

    private static Map<String, Map<String, Double>> prepareFoodGroupComparisonData(FoodGroupStats beforeResult,
            FoodGroupStats afterResult) {
        Map<String, Map<String, Double>> comparisonData = new HashMap<>();

        Map<String, Double> beforeData = new HashMap<>();
        Map<String, Double> afterData = new HashMap<>();

        if (beforeResult != null) {
            beforeResult.getGroupPercentages().forEach((k, v) -> beforeData.put(k.name(), v));
        }

        if (afterResult != null) {
            afterResult.getGroupPercentages().forEach((k, v) -> afterData.put(k.name(), v));
        }

        comparisonData.put("Before", beforeData);
        comparisonData.put("After", afterData);

        return comparisonData;
    }

    private static Map<String, Map<String, Double>> prepareNutrientAnalyzerComparisonData(NutrientStats beforeResult,
            NutrientStats afterResult, VisualizationOps ops) {
        Map<String, Map<String, Double>> comparisonData = new HashMap<>();

        Visualizer visualizer = new Visualizer();

        Map<String, Double> beforeData = beforeResult != null ? visualizer.convertToChartData(beforeResult, ops)
                : new HashMap<>();

        Map<String, Double> afterData = afterResult != null ? visualizer.convertToChartData(afterResult, ops)
                : new HashMap<>();

        comparisonData.put("Before", beforeData);
        comparisonData.put("After", afterData);

        return comparisonData;
    }

    private static Map<String, Map<String, Double>> prepareCFGComparisonData(AlignmentScore beforeResult,
            AlignmentScore afterResult) {
        Map<String, Map<String, Double>> comparisonData = new HashMap<>();

        Map<String, Double> beforeData = new HashMap<>();
        Map<String, Double> afterData = new HashMap<>();

        // Extract only food group alignment data (not overall score) for clean chart
        // display
        if (beforeResult != null) {
            // Add per-food-group alignment scores only
            beforeResult.getDetails().forEach((foodGroup, score) -> beforeData.put(foodGroup.name(), score));
        }

        if (afterResult != null) {
            // Add per-food-group alignment scores only
            afterResult.getDetails().forEach((foodGroup, score) -> afterData.put(foodGroup.name(), score));
        }

        comparisonData.put("Before", beforeData);
        comparisonData.put("After", afterData);

        return comparisonData;
    }

    private static Map<String, Map<String, Double>> prepareCFGSummaryData(AlignmentScore beforeResult,
            AlignmentScore afterResult) {
        Map<String, Map<String, Double>> summaryData = new HashMap<>();

        Map<String, Double> beforeData = new HashMap<>();
        Map<String, Double> afterData = new HashMap<>();

        // Include overall scores AND food group details for summary purposes
        if (beforeResult != null) {
            beforeData.put("Overall Alignment", beforeResult.getScore());
            beforeResult.getDetails().forEach((foodGroup, score) -> beforeData.put(foodGroup.name(), score));
        }

        if (afterResult != null) {
            afterData.put("Overall Alignment", afterResult.getScore());
            afterResult.getDetails().forEach((foodGroup, score) -> afterData.put(foodGroup.name(), score));
        }

        summaryData.put("Before", beforeData);
        summaryData.put("After", afterData);

        return summaryData;
    }

}
