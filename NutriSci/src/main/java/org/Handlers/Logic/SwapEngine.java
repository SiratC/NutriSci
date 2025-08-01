package org.Handlers.Logic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.Dao.FoodNameDAO;
import org.Dao.NutrientAmountDAO;
import org.Dao.NutrientNameDAO;
import org.Entity.Food;
import org.Entity.Meal;
import org.Entity.NutrientAmount;
import org.Entity.SwapRequest;
import org.Enums.NutrientType;
import org.Handlers.Database.*;
import java.sql.Connection;

public class SwapEngine {

    private final NutrientAmountDAO nutrientAmountDAO = new DatabaseNutrientAmountDAO();
    private final NutrientNameDAO nutrientNameDAO = new DatabaseNutrientNameDao();
    private final FoodNameDAO foodNameDAO = new DatabaseFoodNameDAO();

    public List<Meal> applySwap(List<Meal> meals, SwapRequest request) {
        NutrientType target = request.getTargetNutrient();

        // First, ensure all foods have their nutrient data loaded
        List<Meal> mealsWithNutrients = loadNutrientsForMeals(meals);

        // Calculate current total of target nutrient
        double currentTotal = calculateTotalNutrient(mealsWithNutrients, target);
        System.out.println("Current total " + target + ": " + currentTotal);

        // Calculate target amount needed
        double targetAmount;
        if (request.isPercentage()) {
            targetAmount = currentTotal * (1 + request.getIntensityAmount());
        } else {
            targetAmount = currentTotal + request.getIntensityAmount();
        }

        System.out.println("Target amount needed: " + targetAmount);
        System.out.println(
                "Request intensity: " + request.getIntensityAmount() + " (percentage: " + request.isPercentage() + ")");

        // Calculate deficit
        double deficit = targetAmount - currentTotal;
        System.out.println("Deficit to fill: " + deficit);

        // If current total is very low, force a minimum deficit for carbohydrates
        if (target == NutrientType.Carbohydrate && currentTotal < 5.0) {
            deficit = Math.max(deficit, 20.0); // Force at least 20g carb improvement
            System.out.println("Forcing minimum carb deficit: " + deficit);
        }

        // If no meaningful deficit, return original meals
        if (deficit <= 0.1) {
            System.out.println("No meaningful deficit - returning original meals");
            return new ArrayList<>(mealsWithNutrients);
        }

        // Find best swaps to achieve the goal
        List<SwapCandidate> swapCandidates = findSwapCandidates(mealsWithNutrients, target, deficit);

        // Apply up to 2 best swaps
        return applyBestSwaps(mealsWithNutrients, swapCandidates, Math.min(2, swapCandidates.size()));
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
        List<Integer> usedReplacementFoodIds = new ArrayList<>(); // Track used replacement foods

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

                        // Mark this replacement food as used
                        usedReplacementFoodIds.add(replacement.getFoodID());
                    }
                } catch (SQLException e) {
                    System.err.println(
                            "DB error finding replacement for " + originalFood.getName() + ": " + e.getMessage());
                }
            }
        }

        // Sort by improvement (descending) and prefer same-group swaps
        candidates.sort((a, b) -> {
            String originalGroupA = guessGroup(a.originalFood.getName());
            String replacementGroupA = guessGroup(a.replacementFood.getName());
            String originalGroupB = guessGroup(b.originalFood.getName());
            String replacementGroupB = guessGroup(b.replacementFood.getName());

            boolean sameGroupA = originalGroupA.equals(replacementGroupA);
            boolean sameGroupB = originalGroupB.equals(replacementGroupB);

            // Prefer same-group swaps
            if (sameGroupA && !sameGroupB)
                return -1;
            if (!sameGroupA && sameGroupB)
                return 1;

            // Then by improvement amount
            return Double.compare(b.improvement, a.improvement);
        });

        return candidates;
    }

    private Optional<Food> findBetterReplacement(Food original, NutrientType targetNutrient, double neededIncrease,
            List<Integer> usedReplacementFoodIds)
            throws SQLException {
        double originalTargetValue = original.getNutrients().getOrDefault(targetNutrient, 0.0);

        System.out.println("Finding replacement for: " + original.getName() +
                " (current " + targetNutrient + ": " + originalTargetValue + ")");
        System.out.println("  Already used food IDs: " + usedReplacementFoodIds);

        // Get the nutrient ID for the target nutrient
        int targetNutrientId = mapNutrientTypeToId(targetNutrient);
        System.out.println("  Looking for nutrient ID: " + targetNutrientId);

        // debug: First check what nutrient IDs actually exist
        String debugSql = "SELECT DISTINCT nutrientNameId FROM NutrientAmount ORDER BY nutrientNameId LIMIT 10";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement debugPs = conn.prepareStatement(debugSql);
                ResultSet debugRs = debugPs.executeQuery()) {
            System.out.print("  Available nutrient IDs: ");
            while (debugRs.next()) {
                System.out.print(debugRs.getInt(1) + " ");
            }
            System.out.println();
        }

        String sql = """
                SELECT f.foodId, f.foodDescription, na.nutrientValue
                FROM FoodName f
                JOIN NutrientAmount na ON f.foodId = na.foodId
                WHERE na.nutrientNameId = ?
                AND f.foodId != ?
                ORDER BY na.nutrientValue DESC
                LIMIT 20
                """;

        List<CandidateFood> candidates = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, targetNutrientId);
            ps.setInt(2, original.getFoodID());

            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    int foodId = rs.getInt("foodId");
                    String description = rs.getString("foodDescription");
                    double nutrientValuePer100g = rs.getDouble("nutrientValue");

                    // Skip if this food has already been used as a replacement
                    if (usedReplacementFoodIds.contains(foodId)) {
                        System.out.println("    Row " + count + ": " + description + " (SKIPPED - already used)");
                        continue;
                    }

                    // Calculate improvement for the same quantity as original food
                    double scaledValue = nutrientValuePer100g * (original.getQuantity() / 100.0);
                    double improvement = scaledValue - originalTargetValue;

                    System.out.println("    Row " + count + ": " + description +
                            " (per100g: " + nutrientValuePer100g +
                            ", scaled: " + String.format("%.1f", scaledValue) +
                            ", improvement: " + String.format("%.1f", improvement) + ")");

                    if (improvement > 0.1) {
                        candidates.add(new CandidateFood(foodId, description, improvement));
                        System.out.println("      -> Added as candidate");
                    }
                }
                System.out.println("  Total rows found: " + count);
            }
        }

        if (candidates.isEmpty()) {
            System.out.println(
                    "  No candidates found, checking if nutrient ID " + targetNutrientId + " exists in database...");

            // Debug: check if this nutrient ID exists at all
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

        // Take the best candidate (highest improvement)
        CandidateFood best = candidates.get(0);

        // Load full nutrient profile for the selected food
        Map<NutrientType, Double> nutrients = loadNutrientProfile(best.foodId, original.getQuantity());
        double calories = calculateCalories(nutrients, original.getQuantity());

        System.out.println("  Selected: " + best.description + " (improvement: +" +
                String.format("%.1f", best.improvement) + "g " + targetNutrient + ")");

        Food replacement = new Food(best.foodId, best.description, original.getQuantity(), calories);
        replacement.setNutrients(nutrients);
        replacement.setCalories(calories);

        return Optional.of(replacement);
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
                // Meal not saved yet, load nutrients individually
                result.add(loadNutrientsForUnsavedMeal(meal));
            } else {
                // Meal is saved, use efficient batch query
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

                        // Create food object if not exists
                        if (!foodMap.containsKey(foodId)) {
                            foodMap.put(foodId, new Food(foodId, description, quantity, 0.0));
                            nutrientMaps.put(foodId, new HashMap<>());
                        }

                        // Add nutrient data
                        NutrientType type = mapIdToNutrientType(nutrientId);
                        if (type != null) {
                            double scaledValue = nutrientValuePer100g * (quantity / 100.0);
                            nutrientMaps.get(foodId).put(type, scaledValue);
                        }
                    }
                }
            }

            // Build meal with enriched foods
            Meal.Builder builder = new Meal.Builder()
                    .withDate(meal.getDate())
                    .withId(meal.getId())
                    .withType(meal.getType());

            for (Food food : foodMap.values()) {
                Map<NutrientType, Double> nutrients = nutrientMaps.get(food.getFoodID());
                double calories = calculateCalories(nutrients, food.getQuantity());

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
                    double calories = calculateCalories(nutrients, food.getQuantity());

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

        CandidateFood(int foodId, String description, double improvement) {
            this.foodId = foodId;
            this.description = description;
            this.improvement = improvement;
        }
    }

    private double calculateImprovement(Food original, Food replacement, NutrientType target) {
        double originalValue = original.getNutrients().getOrDefault(target, 0.0);
        double replacementValue = replacement.getNutrients().getOrDefault(target, 0.0);
        return replacementValue - originalValue;
    }

    private double calculateCalories(Map<NutrientType, Double> nutrients, double quantity) {
        double protein = nutrients.getOrDefault(NutrientType.Protein, 0.0);
        double carbs = nutrients.getOrDefault(NutrientType.Carbohydrate, 0.0);
        double fat = nutrients.getOrDefault(NutrientType.Fat, 0.0);
        return 4 * protein + 4 * carbs + 9 * fat;
    }

    private List<Meal> applyBestSwaps(List<Meal> originalMeals, List<SwapCandidate> candidates, int maxSwaps) {
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

            // Get the meal to modify
            Meal mealToModify = result.get(candidate.mealIndex);
            List<Food> foods = new ArrayList<>(mealToModify.getItems());

            // 1:1 replacement - replace the food at the specific index with exactly one
            // replacement
            foods.set(candidate.foodIndex, candidate.replacementFood);

            // Create new meal with the replacement
            Meal.Builder builder = new Meal.Builder()
                    .withDate(mealToModify.getDate())
                    .withId(mealToModify.getId())
                    .withType(mealToModify.getType());

            for (Food food : foods) {
                builder.add(food);
            }

            // Replace the meal in the result list
            result.set(candidate.mealIndex, builder.build());
            swapsApplied++;
        }

        System.out.println("Successfully applied " + swapsApplied + " swaps");
        return result;
    }

    private int mapNutrientTypeToId(NutrientType type) {
        return switch (type) {
            case Protein -> 203;
            case Carbohydrate -> 205;
            case Fat -> 204;
            case Fiber -> 291;
            default -> 9999;
        };
    }

    private NutrientType mapIdToNutrientType(int id) {
        return switch (id) {
            case 203 -> NutrientType.Protein;
            case 205 -> NutrientType.Carbohydrate;
            case 204 -> NutrientType.Fat;
            case 291 -> NutrientType.Fiber;
            case 208 -> NutrientType.Calories;
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

    // Helper classes
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
