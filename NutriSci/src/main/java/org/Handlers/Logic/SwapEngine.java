package org.Handlers.Logic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.Entity.Food;
import org.Entity.FoodName;
import org.Entity.Meal;
import org.Entity.SwapRequest;
import org.Enums.NutrientType;
import org.Handlers.Database.*;
import java.sql.Connection;

public class SwapEngine {

    private final DatabaseFoodNameDAO foodNameDAO = new DatabaseFoodNameDAO();

    // Result wrapper class to track whether swaps were actually applied
    public static class SwapResult {
        private final List<Meal> meals;
        private final boolean swapsWereApplied;
        private final int swapCount;

        public SwapResult(List<Meal> meals, boolean swapsWereApplied, int swapCount) {
            this.meals = meals;
            this.swapsWereApplied = swapsWereApplied;
            this.swapCount = swapCount;
        }

        public List<Meal> getMeals() {
            return meals;
        }

        public boolean swapsWereApplied() {
            return swapsWereApplied;
        }

        public int getSwapCount() {
            return swapCount;
        }
    }

    // Internal helper class for tracking swap application results
    private static class SwapApplicationResult {
        final List<Meal> meals;
        final int swapsApplied;

        SwapApplicationResult(List<Meal> meals, int swapsApplied) {
            this.meals = meals;
            this.swapsApplied = swapsApplied;
        }
    }

    public SwapResult applySwapWithResult(List<Meal> meals, SwapRequest request) {
        List<Meal> mealsWithNutrients = loadNutrientsForMeals(meals);

        if (request.hasSecondTarget()) {
            return applyDualTargetSwapWithResult(mealsWithNutrients, request);
        } else {
            return applySingleTargetSwapWithResult(mealsWithNutrients, request);
        }
    }

    // Legacy method for backward compatibility
    public List<Meal> applySwap(List<Meal> meals, SwapRequest request) {
        return applySwapWithResult(meals, request).getMeals();
    }

    private SwapResult applySingleTargetSwapWithResult(List<Meal> mealsWithNutrients, SwapRequest request) {
        NutrientType target = request.getTargetNutrient();

        double currentTotal = calculateTotalNutrient(mealsWithNutrients, target);
        System.out.println("Current total " + target + ": " + currentTotal);

        double targetAmount;
        if (request.isPercentage()) {
            targetAmount = currentTotal * (1 + request.getIntensityAmount());
        } else {
            targetAmount = currentTotal + request.getIntensityAmount();
        }

        System.out.println("Target amount needed: " + targetAmount);
        System.out.println(
                "Request intensity: " + request.getIntensityAmount() + " (percentage: " + request.isPercentage() + ")");

        double deficit = targetAmount - currentTotal;
        System.out.println("Deficit to fill: " + deficit);

        // if no meaningful deficit (either positive or negative), return original meals
        // with no swaps applied
        if (Math.abs(deficit) <= 0.1) {
            System.out.println("No meaningful deficit - returning original meals");
            return new SwapResult(new ArrayList<>(mealsWithNutrients), false, 0);
        }

        // find best swaps to achieve the goal
        List<SwapCandidate> swapCandidates = findSwapCandidates(mealsWithNutrients, target, deficit);

        // apply up to 2 best swaps
        int maxSwaps = Math.min(2, swapCandidates.size());
        SwapApplicationResult result = applyBestSwapsWithResult(mealsWithNutrients, swapCandidates, maxSwaps);

        return new SwapResult(result.meals, result.swapsApplied > 0, result.swapsApplied);
    }

    // Legacy method for backward compatibility
    private List<Meal> applySingleTargetSwap(List<Meal> mealsWithNutrients, SwapRequest request) {
        return applySingleTargetSwapWithResult(mealsWithNutrients, request).getMeals();
    }

    private SwapResult applyDualTargetSwapWithResult(List<Meal> mealsWithNutrients, SwapRequest request) {
        NutrientType target1 = request.getTargetNutrient();
        NutrientType target2 = request.getSecondTargetNutrient();

        // Calculate current totals for both nutrients
        double currentTotal1 = calculateTotalNutrient(mealsWithNutrients, target1);
        double currentTotal2 = calculateTotalNutrient(mealsWithNutrients, target2);

        System.out.println("Current total " + target1 + ": " + currentTotal1);
        System.out.println("Current total " + target2 + ": " + currentTotal2);

        // Calculate target amounts for both nutrients
        double targetAmount1;
        if (request.isPercentage()) {
            targetAmount1 = currentTotal1 * (1 + request.getIntensityAmount());
        } else {
            targetAmount1 = currentTotal1 + request.getIntensityAmount();
        }

        double targetAmount2;
        if (request.isSecondPercentage()) {
            targetAmount2 = currentTotal2 * (1 + request.getSecondIntensityAmount());
        } else {
            targetAmount2 = currentTotal2 + request.getSecondIntensityAmount();
        }

        double deficit1 = targetAmount1 - currentTotal1;
        double deficit2 = targetAmount2 - currentTotal2;

        System.out
                .println("Target amount needed for " + target1 + ": " + targetAmount1 + " (deficit: " + deficit1 + ")");
        System.out
                .println("Target amount needed for " + target2 + ": " + targetAmount2 + " (deficit: " + deficit2 + ")");

        // if both deficits are negligible (either positive or negative), return
        // original meals with no swaps applied
        if (Math.abs(deficit1) <= 0.1 && Math.abs(deficit2) <= 0.1) {
            System.out.println("No meaningful deficits - returning original meals");
            return new SwapResult(new ArrayList<>(mealsWithNutrients), false, 0);
        }

        // find best swaps to achieve both goals
        List<SwapCandidate> swapCandidates = findDualTargetSwapCandidates(mealsWithNutrients, target1, target2,
                deficit1, deficit2);

        // apply up to 2 best swaps
        int maxSwaps = Math.min(2, swapCandidates.size());
        SwapApplicationResult result = applyBestSwapsWithResult(mealsWithNutrients, swapCandidates, maxSwaps);

        return new SwapResult(result.meals, result.swapsApplied > 0, result.swapsApplied);
    }

    // Legacy method for backward compatibility
    private List<Meal> applyDualTargetSwap(List<Meal> mealsWithNutrients, SwapRequest request) {
        return applyDualTargetSwapWithResult(mealsWithNutrients, request).getMeals();
    }

    private double calculateTotalNutrient(List<Meal> meals, NutrientType nutrient) {
        double total = 0.0;
        for (Meal meal : meals) {
            for (Food food : meal.getItems()) {
                total += food.getNutrients().getOrDefault(nutrient, 0.0);
            }
        }
        return total;
    }

    private List<SwapCandidate> findSwapCandidates(List<Meal> meals, NutrientType target, double deficit) {
        List<SwapCandidate> candidates = new ArrayList<>();
        List<Integer> usedReplacementFoodIds = new ArrayList<>(); // track used replacement foods

        for (int mealIndex = 0; mealIndex < meals.size(); mealIndex++) {
            Meal meal = meals.get(mealIndex);
            for (int foodIndex = 0; foodIndex < meal.getItems().size(); foodIndex++) {
                Food originalFood = meal.getItems().get(foodIndex);

                try {
                    Optional<Food> betterFood = findBetterReplacement(originalFood, target, deficit,
                            usedReplacementFoodIds);
                    if (betterFood.isPresent()) {
                        Food replacement = betterFood.get();
                        double improvement = calculateImprovement(originalFood, replacement, target);

                        candidates.add(new SwapCandidate(
                                mealIndex, foodIndex, originalFood, replacement, improvement));

                        // mark this replacement food as used
                        usedReplacementFoodIds.add(replacement.getFoodID());
                    }
                } catch (SQLException e) {
                    System.err.println(
                            "DB error finding replacement for " + originalFood.getName() + ": " + e.getMessage());
                }
            }
        }

        // sort by target proximity (descending) and prefer same-group swaps
        candidates.sort((a, b) -> {
            String originalGroupA = guessGroup(a.originalFood.getName());
            String replacementGroupA = guessGroup(a.replacementFood.getName());
            String originalGroupB = guessGroup(b.originalFood.getName());
            String replacementGroupB = guessGroup(b.replacementFood.getName());

            boolean sameGroupA = originalGroupA.equals(replacementGroupA);
            boolean sameGroupB = originalGroupB.equals(replacementGroupB);

            // prefer same-group swaps
            if (sameGroupA && !sameGroupB)
                return -1;
            if (!sameGroupA && sameGroupB)
                return 1;

            // For target proximity-based sorting, we need access to the original food's
            // target proximity
            // Since SwapCandidate doesn't have target proximity, use improvement as
            // fallback for now
            return Double.compare(b.improvement, a.improvement);
        });

        return candidates;
    }

    private List<SwapCandidate> findDualTargetSwapCandidates(List<Meal> meals, NutrientType target1,
            NutrientType target2,
            double deficit1, double deficit2) {
        List<SwapCandidate> candidates = new ArrayList<>();
        List<Integer> usedReplacementFoodIds = new ArrayList<>();

        for (int mealIndex = 0; mealIndex < meals.size(); mealIndex++) {
            Meal meal = meals.get(mealIndex);
            for (int foodIndex = 0; foodIndex < meal.getItems().size(); foodIndex++) {
                Food originalFood = meal.getItems().get(foodIndex);

                try {
                    Optional<Food> betterFood = findBetterDualTargetReplacement(originalFood, target1, target2,
                            deficit1, deficit2, usedReplacementFoodIds);
                    if (betterFood.isPresent()) {
                        Food replacement = betterFood.get();
                        double improvement = calculateDualTargetImprovement(originalFood, replacement, target1, target2,
                                deficit1, deficit2);

                        candidates.add(new SwapCandidate(
                                mealIndex, foodIndex, originalFood, replacement, improvement));

                        // mark this replacement food as used
                        usedReplacementFoodIds.add(replacement.getFoodID());
                    }
                } catch (SQLException e) {
                    System.err.println(
                            "DB error finding dual-target replacement for " + originalFood.getName() + ": "
                                    + e.getMessage());
                }
            }
        }

        // sort by improvement (descending) and prefer same-group swaps
        candidates.sort((a, b) -> {
            String originalGroupA = guessGroup(a.originalFood.getName());
            String replacementGroupA = guessGroup(a.replacementFood.getName());
            String originalGroupB = guessGroup(b.originalFood.getName());
            String replacementGroupB = guessGroup(b.replacementFood.getName());

            boolean sameGroupA = originalGroupA.equals(replacementGroupA);
            boolean sameGroupB = originalGroupB.equals(replacementGroupB);

            // prefer same-group swaps
            if (sameGroupA && !sameGroupB)
                return -1;
            if (!sameGroupA && sameGroupB)
                return 1;

            // then by improvement amount
            return Double.compare(b.improvement, a.improvement);
        });

        return candidates;
    }

    private Optional<Food> findBetterReplacement(Food original, NutrientType targetNutrient, double deficit,
            List<Integer> usedReplacementFoodIds)
            throws SQLException {
        double originalTargetValue = original.getNutrients().getOrDefault(targetNutrient, 0.0);

        boolean isReduction = deficit < 0;
        System.out.println("Finding replacement for: " + original.getName() +
                " (current " + targetNutrient + ": " + originalTargetValue + ", " +
                (isReduction ? "REDUCTION" : "INCREASE") + " needed)");
        System.out.println("  Already used food IDs: " + usedReplacementFoodIds);

        int targetNutrientId = mapNutrientTypeToId(targetNutrient);
        System.out.println("  Looking for nutrient ID: " + targetNutrientId);

        // For reductions, look for foods with LOWER nutrient values (ASC)
        // For increases, look for foods with HIGHER nutrient values (DESC)
        String sortOrder = isReduction ? "ASC" : "DESC";

        // Calculate minimum nutrient value for database filtering
        double minimumDatabaseValue = 0.1; // Default minimum for increases
        if (isReduction) {
            // For reductions, set minimum based on original value (at least 25% or 1g)
            double minimumAcceptableValue = Math.max(originalTargetValue * 0.25, 1.0);
            // Convert back to per-100g for database query
            minimumDatabaseValue = minimumAcceptableValue / (original.getQuantity() / 100.0);
        }

        String sql = """
                SELECT f.foodId, f.foodDescription, na.nutrientValue
                FROM FoodName f
                JOIN NutrientAmount na ON f.foodId = na.foodId
                WHERE na.nutrientNameId = ?
                AND f.foodId != ?
                AND na.nutrientValue >= ?
                ORDER BY na.nutrientValue """ + " " + sortOrder + """

                LIMIT 20
                """;

        List<CandidateFood> candidates = new ArrayList<>();

        System.out.println(
                "  Database filter minimum value: " + String.format("%.1f", minimumDatabaseValue) + "g per 100g");

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, targetNutrientId);
            ps.setInt(2, original.getFoodID());
            ps.setDouble(3, minimumDatabaseValue);

            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    int foodId = rs.getInt("foodId");
                    String description = rs.getString("foodDescription");
                    double nutrientValuePer100g = rs.getDouble("nutrientValue");

                    // skip if this food has already been used as a replacement
                    if (usedReplacementFoodIds.contains(foodId)) {
                        System.out.println("    Row " + count + ": " + description + " (SKIPPED - already used)");
                        continue;
                    }

                    // calculate improvement for the same quantity as original food
                    double scaledValue = nutrientValuePer100g * (original.getQuantity() / 100.0);
                    double improvement = scaledValue - originalTargetValue;

                    System.out.println("    Row " + count + ": " + description +
                            " (per100g: " + nutrientValuePer100g +
                            ", scaled: " + String.format("%.1f", scaledValue) +
                            ", improvement: " + String.format("%.1f", improvement) + ")");

                    // For reductions, prevent over-reduction by setting minimum thresholds
                    boolean isGoodCandidate;
                    if (isReduction) {
                        // Calculate minimum acceptable nutrient value (at least 25% of original, or 1g
                        // minimum)
                        double minimumAcceptableValue = Math.max(originalTargetValue * 0.25, 1.0);

                        // For meaningful reduction but not too much
                        boolean meaningfulReduction = improvement < -0.1;
                        boolean notOverReduced = scaledValue >= minimumAcceptableValue;

                        isGoodCandidate = meaningfulReduction && notOverReduced;

                        if (meaningfulReduction && !notOverReduced) {
                            System.out.println("      -> REJECTED: Over-reduction (scaled: " +
                                    String.format("%.1f", scaledValue) + "g < minimum: " +
                                    String.format("%.1f", minimumAcceptableValue) + "g)");
                        }
                    } else {
                        // For increases, use original logic
                        isGoodCandidate = improvement > 0.1;
                    }
                    if (isGoodCandidate) {
                        // Calculate target proximity score - how close this gets us to filling the
                        // deficit
                        double targetProximityScore;
                        if (isReduction) {
                            // For reductions, prefer candidates that don't over-reduce
                            // Score based on how close the improvement is to the deficit needed
                            double targetImprovement = deficit; // deficit is negative for reductions
                            double proximityToTarget = Math.abs(improvement - targetImprovement);
                            targetProximityScore = 1.0 / (1.0 + proximityToTarget); // Higher score for closer to target
                        } else {
                            // For increases, prefer larger improvements (original logic)
                            targetProximityScore = improvement;
                        }

                        candidates.add(new CandidateFood(foodId, description, improvement, targetProximityScore));
                        System.out.println("      -> Added as candidate (target proximity: " +
                                String.format("%.3f", targetProximityScore) + ")");
                    }
                }
                System.out.println("  Total rows found: " + count);
            }
        }

        if (candidates.isEmpty()) {
            System.out.println(
                    "  No candidates found, checking if nutrient ID " + targetNutrientId + " exists in database...");

            String debugSql2 = "SELECT COUNT(*) FROM NutrientAmount WHERE nutrientNameId = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement ps = conn.prepareStatement(debugSql2)) {
                ps.setInt(1, targetNutrientId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        System.out.println("  Found " + count + " records with nutrient ID " + targetNutrientId);
                    }
                }
            }

            return Optional.empty();
        }

        // take the best candidate (highest improvement)
        CandidateFood best = candidates.get(0);

        // load full nutrient profile for the selected food
        Map<NutrientType, Double> nutrients = loadNutrientProfile(best.foodId, original.getQuantity());
        double calories = calculateCalories(best.foodId, original.getQuantity());

        System.out.println("  Selected: " + best.description + " (improvement: +" +
                String.format("%.1f", best.improvement) + "g " + targetNutrient + ")");

        Food replacement = new Food(best.foodId, best.description, original.getQuantity(), calories);
        replacement.setNutrients(nutrients);
        replacement.setCalories(calories);

        return Optional.of(replacement);
    }

    private Optional<Food> findBetterDualTargetReplacement(Food original, NutrientType target1, NutrientType target2,
            double deficit1, double deficit2, List<Integer> usedReplacementFoodIds) throws SQLException {

        double originalTarget1Value = original.getNutrients().getOrDefault(target1, 0.0);
        double originalTarget2Value = original.getNutrients().getOrDefault(target2, 0.0);

        System.out.println("Finding dual-target replacement for: " + original.getName() +
                " (current " + target1 + ": " + originalTarget1Value +
                ", " + target2 + ": " + originalTarget2Value + ")");
        System.out.println("  Already used food IDs: " + usedReplacementFoodIds);

        int target1NutrientId = mapNutrientTypeToId(target1);
        int target2NutrientId = mapNutrientTypeToId(target2);

        // Query foods with data for both nutrients, prioritizing foods that improve
        // either nutrient
        String sql = """
                SELECT f.foodId, f.foodDescription,
                       na1.nutrientValue as target1Value,
                       na2.nutrientValue as target2Value
                FROM FoodName f
                JOIN NutrientAmount na1 ON f.foodId = na1.foodId AND na1.nutrientNameId = ?
                JOIN NutrientAmount na2 ON f.foodId = na2.foodId AND na2.nutrientNameId = ?
                WHERE f.foodId != ?
                ORDER BY (na1.nutrientValue + na2.nutrientValue) DESC
                LIMIT 20
                """;

        List<DualTargetCandidateFood> candidates = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, target1NutrientId);
            ps.setInt(2, target2NutrientId);
            ps.setInt(3, original.getFoodID());

            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    int foodId = rs.getInt("foodId");
                    String description = rs.getString("foodDescription");
                    double target1ValuePer100g = rs.getDouble("target1Value");
                    double target2ValuePer100g = rs.getDouble("target2Value");

                    // skip if this food has already been used as a replacement
                    if (usedReplacementFoodIds.contains(foodId)) {
                        System.out.println("    Row " + count + ": " + description + " (SKIPPED - already used)");
                        continue;
                    }

                    // calculate scaled values for the same quantity as original food
                    double scaledTarget1Value = target1ValuePer100g * (original.getQuantity() / 100.0);
                    double scaledTarget2Value = target2ValuePer100g * (original.getQuantity() / 100.0);

                    double improvement1 = scaledTarget1Value - originalTarget1Value;
                    double improvement2 = scaledTarget2Value - originalTarget2Value;

                    System.out.println("    Row " + count + ": " + description +
                            " (" + target1 + ": " + String.format("%.1f", scaledTarget1Value) +
                            ", " + target2 + ": " + String.format("%.1f", scaledTarget2Value) +
                            ", improvements: " + String.format("%.1f", improvement1) +
                            ", " + String.format("%.1f", improvement2) + ")");

                    // Accept candidates that improve at least one nutrient meaningfully in the
                    // correct direction
                    boolean target1IsReduction = deficit1 < 0;
                    boolean target2IsReduction = deficit2 < 0;
                    boolean improvement1Good = target1IsReduction ? improvement1 < -0.1 : improvement1 > 0.1;
                    boolean improvement2Good = target2IsReduction ? improvement2 < -0.1 : improvement2 > 0.1;

                    if (improvement1Good || improvement2Good) {
                        candidates.add(new DualTargetCandidateFood(foodId, description, improvement1, improvement2));
                        System.out.println("      -> Added as candidate");
                    }
                }
                System.out.println("  Total rows found: " + count);
            }
        }

        if (candidates.isEmpty()) {
            System.out.println("  No dual-target candidates found");
            return Optional.empty();
        }

        // Sort by combined improvement score weighted by deficits
        candidates.sort((a, b) -> {
            double scoreA = calculateDualTargetScore(a.improvement1, a.improvement2, deficit1, deficit2);
            double scoreB = calculateDualTargetScore(b.improvement1, b.improvement2, deficit1, deficit2);
            return Double.compare(scoreB, scoreA);
        });

        // take the best candidate
        DualTargetCandidateFood best = candidates.get(0);

        // load full nutrient profile for the selected food
        Map<NutrientType, Double> nutrients = loadNutrientProfile(best.foodId, original.getQuantity());
        double calories = calculateCalories(best.foodId, original.getQuantity());

        System.out.println("  Selected: " + best.description + " (improvements: " +
                String.format("%.1f", best.improvement1) + "g " + target1 + ", " +
                String.format("%.1f", best.improvement2) + "g " + target2 + ")");

        Food replacement = new Food(best.foodId, best.description, original.getQuantity(), calories);
        replacement.setNutrients(nutrients);
        replacement.setCalories(calories);

        return Optional.of(replacement);
    }

    private double calculateDualTargetScore(double improvement1, double improvement2, double deficit1,
            double deficit2) {
        double weight1 = Math.abs(deficit1) > 0.1 ? Math.abs(deficit1) : 1.0;
        double weight2 = Math.abs(deficit2) > 0.1 ? Math.abs(deficit2) : 1.0;

        // Normalize weights
        double totalWeight = weight1 + weight2;
        weight1 /= totalWeight;
        weight2 /= totalWeight;

        return (improvement1 * weight1) + (improvement2 * weight2);
    }

    private double calculateDualTargetImprovement(Food original, Food replacement, NutrientType target1,
            NutrientType target2, double deficit1, double deficit2) {
        double improvement1 = replacement.getNutrients().getOrDefault(target1, 0.0) -
                original.getNutrients().getOrDefault(target1, 0.0);
        double improvement2 = replacement.getNutrients().getOrDefault(target2, 0.0) -
                original.getNutrients().getOrDefault(target2, 0.0);

        return calculateDualTargetScore(improvement1, improvement2, deficit1, deficit2);
    }

    // Helper class for dual target candidate foods
    private static class DualTargetCandidateFood {
        final int foodId;
        final String description;
        final double improvement1;
        final double improvement2;

        DualTargetCandidateFood(int foodId, String description, double improvement1, double improvement2) {
            this.foodId = foodId;
            this.description = description;
            this.improvement1 = improvement1;
            this.improvement2 = improvement2;
        }
    }

    private Map<NutrientType, Double> loadNutrientProfile(int foodId, double quantity) throws SQLException {
        Map<NutrientType, Double> nutrients = new HashMap<>();
        double scaleFactor = quantity / 100.0;

        String sql = """
                SELECT na.nutrientNameId, na.nutrientValue
                FROM NutrientAmount na
                WHERE na.foodId = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, foodId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int nutrientId = rs.getInt("nutrientNameId");
                    double valuePer100g = rs.getDouble("nutrientValue");

                    NutrientType type = mapIdToNutrientType(nutrientId);
                    if (type != null) {
                        nutrients.put(type, valuePer100g * scaleFactor);
                    }
                }
            }
        }

        return nutrients;
    }

    private List<Meal> loadNutrientsForMeals(List<Meal> originalMeals) {
        List<Meal> result = new ArrayList<>();

        for (Meal meal : originalMeals) {
            if (meal.getId() == null) {
                result.add(loadNutrientsForUnsavedMeal(meal));
            } else {
                result.add(loadNutrientsForSavedMeal(meal));
            }
        }

        return result;
    }

    private Meal loadNutrientsForSavedMeal(Meal meal) {
        try {
            String sql = """
                    SELECT mlf.foodId, mlf.quantity, f.foodDescription,
                           na.nutrientNameId, na.nutrientValue
                    FROM MealLogFoods mlf
                    JOIN FoodName f ON mlf.foodId = f.foodId
                    JOIN NutrientAmount na ON f.foodId = na.foodId
                    WHERE mlf.logId = ?
                    ORDER BY mlf.foodId, na.nutrientNameId
                    """;

            Map<Integer, Food> foodMap = new HashMap<>();
            Map<Integer, Map<NutrientType, Double>> nutrientMaps = new HashMap<>();

            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setObject(1, meal.getId());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int foodId = rs.getInt("foodId");
                        double quantity = rs.getDouble("quantity");
                        String description = rs.getString("foodDescription");
                        int nutrientId = rs.getInt("nutrientNameId");
                        double nutrientValuePer100g = rs.getDouble("nutrientValue");

                        if (!foodMap.containsKey(foodId)) {
                            foodMap.put(foodId, new Food(foodId, description, quantity, 0.0));
                            nutrientMaps.put(foodId, new HashMap<>());
                        }

                        NutrientType type = mapIdToNutrientType(nutrientId);
                        if (type != null) {
                            double scaledValue = nutrientValuePer100g * (quantity / 100.0);
                            nutrientMaps.get(foodId).put(type, scaledValue);
                        }
                    }
                }
            }

            Meal.Builder builder = new Meal.Builder()
                    .withDate(meal.getDate())
                    .withId(meal.getId())
                    .withType(meal.getType());

            for (Food food : foodMap.values()) {
                Map<NutrientType, Double> nutrients = nutrientMaps.get(food.getFoodID());
                double calories = calculateCalories(food.getFoodID(), food.getQuantity());

                food.setNutrients(nutrients);
                food.setCalories(calories);
                builder.add(food);
            }

            return builder.build();

        } catch (SQLException e) {
            System.err.println("Failed to load nutrients for saved meal: " + e.getMessage());
            return meal; // Return original meal if loading fails
        }
    }

    private Meal loadNutrientsForUnsavedMeal(Meal meal) {
        Meal.Builder builder = new Meal.Builder()
                .withDate(meal.getDate())
                .withId(meal.getId())
                .withType(meal.getType());

        for (Food food : meal.getItems()) {
            if (food.getNutrients().isEmpty()) {
                try {
                    Map<NutrientType, Double> nutrients = loadNutrientProfile(food.getFoodID(), food.getQuantity());
                    double calories = calculateCalories(food.getFoodID(), food.getQuantity());

                    Food enrichedFood = new Food(food.getFoodID(), food.getName(), food.getQuantity(), calories);
                    enrichedFood.setNutrients(nutrients);
                    enrichedFood.setCalories(calories);
                    builder.add(enrichedFood);
                } catch (SQLException e) {
                    System.err.println("Failed to load nutrients for " + food.getName() + ": " + e.getMessage());
                    builder.add(food);
                }
            } else {
                builder.add(food);
            }
        }

        return builder.build();
    }

    // Helper class for candidate foods
    private static class CandidateFood {
        final int foodId;
        final String description;
        final double improvement;
        final double targetProximityScore;

        CandidateFood(int foodId, String description, double improvement, double targetProximityScore) {
            this.foodId = foodId;
            this.description = description;
            this.improvement = improvement;
            this.targetProximityScore = targetProximityScore;
        }

        // Backward compatibility constructor
        CandidateFood(int foodId, String description, double improvement) {
            this(foodId, description, improvement, Math.abs(improvement));
        }
    }

    private double calculateImprovement(Food original, Food replacement, NutrientType target) {
        double originalValue = original.getNutrients().getOrDefault(target, 0.0);
        double replacementValue = replacement.getNutrients().getOrDefault(target, 0.0);
        return replacementValue - originalValue;
    }

    private double calculateCalories(int foodId, double quantity) throws SQLException {
        try {
            FoodName food = foodNameDAO.findById(foodId);
            return food != null ? (food.getCaloriesPer100g() * (quantity / 100.0)) : 0.0;
        } catch (SQLException e) {
            System.err.println("Failed to calculate calories for food ID " + foodId + ": " + e.getMessage());
            return 0.0;
        }
    }

    private SwapApplicationResult applyBestSwapsWithResult(List<Meal> originalMeals, List<SwapCandidate> candidates,
            int maxSwaps) {
        List<Meal> result = new ArrayList<>();

        // Create deep copies of all meals first
        for (Meal meal : originalMeals) {
            List<Food> copiedFoods = new ArrayList<>(meal.getItems());
            Meal.Builder builder = new Meal.Builder()
                    .withDate(meal.getDate())
                    .withId(meal.getId())
                    .withType(meal.getType());

            for (Food food : copiedFoods) {
                builder.add(food);
            }
            result.add(builder.build());
        }

        int swapsApplied = 0;
        System.out.println("Applying swaps (max: " + maxSwaps + ", candidates: " + candidates.size() + ")");

        for (SwapCandidate candidate : candidates) {
            if (swapsApplied >= maxSwaps)
                break;

            System.out.println("Swap " + (swapsApplied + 1) + ": " +
                    candidate.originalFood.getName() + " -> " + candidate.replacementFood.getName() +
                    " (improvement: +" + String.format("%.1f", candidate.improvement) + "g)");

            Meal mealToModify = result.get(candidate.mealIndex);
            List<Food> foods = new ArrayList<>(mealToModify.getItems());

            // 1:1 replacement - replace the food at the specific index with exactly one
            // replacement
            foods.set(candidate.foodIndex, candidate.replacementFood);

            Meal.Builder builder = new Meal.Builder()
                    .withDate(mealToModify.getDate())
                    .withId(mealToModify.getId())
                    .withType(mealToModify.getType());

            for (Food food : foods) {
                builder.add(food);
            }

            result.set(candidate.mealIndex, builder.build());
            swapsApplied++;
        }

        System.out.println("Successfully applied " + swapsApplied + " swaps");
        return new SwapApplicationResult(result, swapsApplied);
    }

    // Legacy method for backward compatibility
    private List<Meal> applyBestSwaps(List<Meal> originalMeals, List<SwapCandidate> candidates, int maxSwaps) {
        return applyBestSwapsWithResult(originalMeals, candidates, maxSwaps).meals;
    }

    private int mapNutrientTypeToId(NutrientType type) {
        return switch (type) {
            case Protein -> 203;
            case Carbohydrate -> 205;
            case Fat -> 204;
            case Fiber -> 291;
            case Calories -> 208;
            default -> 9999;
        };
    }

    private NutrientType mapIdToNutrientType(int id) {
        return switch (id) {
            case 203 -> NutrientType.Protein;
            case 205 -> NutrientType.Carbohydrate;
            case 204 -> NutrientType.Fat;
            case 291 -> NutrientType.Fiber;
            case 208 -> NutrientType.Calories; // Technically not a nutrient but considered as such for swaps
            default -> null;
        };
    }

    private String guessGroup(String description) {
        description = description.toLowerCase();
        if (description.contains("milk") || description.contains("cheese") || description.contains("yogurt"))
            return "dairy";
        if (description.contains("chicken") || description.contains("beef") || description.contains("pork") ||
                description.contains("lamb") || description.contains("meat"))
            return "meat";
        if (description.contains("fish") || description.contains("salmon") ||
                description.contains("tuna") || description.contains("cod"))
            return "fish";
        if (description.contains("carrot") || description.contains("broccoli") ||
                description.contains("spinach") || description.contains("vegetable"))
            return "vegetable";
        if (description.contains("apple") || description.contains("banana") ||
                description.contains("fruit") || description.contains("berry"))
            return "fruit";
        if (description.contains("rice") || description.contains("bread") ||
                description.contains("grain") || description.contains("cereal"))
            return "grain";
        return "other";
    }

    // helper class
    private static class SwapCandidate {
        final int mealIndex;
        final int foodIndex;
        final Food originalFood;
        final Food replacementFood;
        final double improvement;

        SwapCandidate(int mealIndex, int foodIndex, Food originalFood, Food replacementFood, double improvement) {
            this.mealIndex = mealIndex;
            this.foodIndex = foodIndex;
            this.originalFood = originalFood;
            this.replacementFood = replacementFood;
            this.improvement = improvement;
        }
    }
}
