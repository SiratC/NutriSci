package org.Handlers.Logic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.Dao.NutrientProfileDAO;
import org.Entity.Food;
import org.Entity.FoodName;
import org.Entity.Meal;
import org.Entity.SwapRequest;
import org.Enums.NutrientType;
import org.Handlers.Database.*;
import java.sql.Connection;

public class SwapEngine {

    private final DatabaseFoodNameDAO foodNameDAO = new DatabaseFoodNameDAO();
    private final NutrientProfileDAO nutrientProfileDAO = new DatabaseNutrientProfileDAO();
    private final MealEnricher mealEnricher = new MealEnricher();
    private final NutrientTypeMapper nutrientTypeMapper = new NutrientTypeMapper();

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
        List<Meal> mealsWithNutrients = mealEnricher.enrich(meals);

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

    private record TargetInfo(NutrientType nutrient, double current, double target, double deficit) {
    }

    private TargetInfo targetInfo(List<Meal> meals, NutrientType n, boolean pct, double amt) {
        double cur = calculateTotalNutrient(meals, n);
        double tar = pct ? cur * (1 + amt) : cur + amt;
        return new TargetInfo(n, cur, tar, tar - cur);
    }

    private SwapResult applyTargetSwapWithResult(List<Meal> meals, SwapRequest r) {
        TargetInfo a = targetInfo(meals, r.getTargetNutrient(), r.isPercentage(), r.getIntensityAmount());
        if (!r.hasSecondTarget())
            return resolveSingle(meals, r, a);
        TargetInfo b = targetInfo(meals, r.getSecondTargetNutrient(), r.isSecondPercentage(),
                r.getSecondIntensityAmount());
        return resolveDual(meals, r, a, b);
    }

    private SwapResult resolveSingle(List<Meal> meals, SwapRequest r, TargetInfo t) {
        System.out.println("Current total " + t.nutrient() + ": " + t.current());
        System.out.println("Target amount needed: " + t.target());
        System.out.println("Request intensity: " + r.getIntensityAmount() + " (percentage: " + r.isPercentage() + ")");
        System.out.println("Deficit to fill: " + t.deficit());
        if (Math.abs(t.deficit()) <= 0.1) {
            System.out.println("No meaningful deficit - returning original meals");
            return new SwapResult(new ArrayList<>(meals), false, 0);
        }
        List<SwapCandidate> c = findSwapCandidates(meals, t.nutrient(), t.deficit());
        int max = Math.min(2, c.size());
        SwapApplicationResult res = applyBestSwapsWithResult(meals, c, max);
        return new SwapResult(res.meals, res.swapsApplied > 0, res.swapsApplied);
    }

    private SwapResult resolveDual(List<Meal> meals, SwapRequest r, TargetInfo a, TargetInfo b) {
        System.out.println("Current total " + a.nutrient() + ": " + a.current());
        System.out.println("Current total " + b.nutrient() + ": " + b.current());
        System.out.println(
                "Target amount needed for " + a.nutrient() + ": " + a.target() + " (deficit: " + a.deficit() + ")");
        System.out.println(
                "Target amount needed for " + b.nutrient() + ": " + b.target() + " (deficit: " + b.deficit() + ")");
        if (Math.abs(a.deficit()) <= 0.1 && Math.abs(b.deficit()) <= 0.1) {
            System.out.println("No meaningful deficits - returning original meals");
            return new SwapResult(new ArrayList<>(meals), false, 0);
        }
        List<SwapCandidate> c = findDualTargetSwapCandidates(meals, a.nutrient(), b.nutrient(), a.deficit(),
                b.deficit());
        int max = Math.min(2, c.size());
        SwapApplicationResult res = applyBestSwapsWithResult(meals, c, max);
        return new SwapResult(res.meals, res.swapsApplied > 0, res.swapsApplied);
    }

    private SwapResult applySingleTargetSwapWithResult(List<Meal> meals, SwapRequest r) {
        return applyTargetSwapWithResult(meals, r);
    }

    private SwapResult applyDualTargetSwapWithResult(List<Meal> meals, SwapRequest r) {
        return applyTargetSwapWithResult(meals, r);
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

    private Optional<Food> findBetterReplacement(
            Food original,
            NutrientType target,
            double deficit,
            List<Integer> usedIds) throws SQLException {

        boolean reduction = deficit < 0;
        int nutrientId = nutrientTypeMapper.mapNutrientTypeToId(target);

        List<RawFood> raws = fetchRawFoods(original, nutrientId, reduction, usedIds);

        List<CandidateFood> candidates = buildCandidates(
                raws, original, target, deficit, reduction);

        if (candidates.isEmpty())
            return Optional.empty();

        CandidateFood best = candidates.get(0);
        Map<NutrientType, Double> profile = nutrientProfileDAO.profile(best.foodId, original.getQuantity());
        double calories = calculateCalories(best.foodId, original.getQuantity());

        Food f = new Food(best.foodId, best.description, original.getQuantity(), calories);
        f.setNutrients(profile);
        f.setCalories(calories);
        return Optional.of(f);
    }

    private List<RawFood> fetchRawFoods(
            Food original,
            int nutrientId,
            boolean reduction,
            List<Integer> usedIds) throws SQLException {

        String order = reduction ? "ASC" : "DESC";
        double minVal = minimumDatabaseValue(original, reduction);

        String sql = """
                SELECT f.foodId, f.foodDescription, na.nutrientValue
                FROM   FoodName f
                JOIN   NutrientAmount na ON f.foodId = na.foodId
                WHERE  na.nutrientNameId = ?
                  AND  f.foodId <> ?
                  AND  na.nutrientValue >= ?
                ORDER BY na.nutrientValue """ + order + " LIMIT 20";

        List<RawFood> out = new ArrayList<>();

        try (Connection c = DatabaseConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, nutrientId);
            ps.setInt(2, original.getFoodID());
            ps.setDouble(3, minVal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt(1);
                    if (usedIds.contains(id))
                        continue;
                    out.add(new RawFood(id, rs.getString(2), rs.getDouble(3)));
                }
            }
        }
        return out;
    }

    private double minimumDatabaseValue(Food original, boolean reduction) {
        if (!reduction)
            return 0.1;
        double minAcc = Math.max(
                original.getNutrients().values().stream().mapToDouble(Double::doubleValue).sum() * 0.25,
                1.0);
        return minAcc / (original.getQuantity() / 100.0);
    }

    private List<CandidateFood> buildCandidates(
            List<RawFood> raws,
            Food original,
            NutrientType target,
            double deficit,
            boolean reduction) {

        double origVal = original.getNutrients().getOrDefault(target, 0.0);
        double scale = original.getQuantity() / 100.0;

        List<CandidateFood> list = new ArrayList<>();

        for (RawFood r : raws) {
            double scaled = r.per100g * scale;
            double improvement = scaled - origVal;

            boolean accept;
            if (reduction) {
                boolean ok = improvement < -0.1;
                boolean safe = scaled >= origVal * 0.25;
                accept = ok && safe;
            } else {
                accept = improvement > 0.1;
            }
            if (!accept)
                continue;

            double score;
            if (reduction) {
                score = 1.0 / (1.0 + Math.abs(improvement - deficit));
            } else {
                score = improvement;
            }
            list.add(new CandidateFood(r.foodId, r.description, improvement, score));
        }

        list.sort(Comparator.comparingDouble(c -> -c.targetProximityScore));
        return list;
    }

    private record RawFood(int foodId, String description, double per100g) {
    }

    private Optional<Food> findBetterDualTargetReplacement(Food original, NutrientType target1, NutrientType target2,
            double deficit1, double deficit2, List<Integer> usedReplacementFoodIds) throws SQLException {

        double originalTarget1Value = original.getNutrients().getOrDefault(target1, 0.0);
        double originalTarget2Value = original.getNutrients().getOrDefault(target2, 0.0);

        System.out.println("Finding dual-target replacement for: " + original.getName() +
                " (current " + target1 + ": " + originalTarget1Value +
                ", " + target2 + ": " + originalTarget2Value + ")");
        System.out.println("  Already used food IDs: " + usedReplacementFoodIds);

        int target1NutrientId = nutrientTypeMapper.mapNutrientTypeToId(target1);
        int target2NutrientId = nutrientTypeMapper.mapNutrientTypeToId(target2);

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
        Map<NutrientType, Double> nutrients = nutrientProfileDAO.profile(best.foodId, original.getQuantity());
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
