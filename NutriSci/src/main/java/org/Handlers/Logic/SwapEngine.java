package org.Handlers.Logic;

import java.math.BigDecimal;
import org.Dao.*;
import org.Entity.*;
import org.Enums.NutrientType;
import org.Handlers.Database.DatabaseFoodNameDAO;
import org.Handlers.Database.DatabaseNutrientAmountDAO;
import org.Handlers.Database.DatabaseNutrientNameDao;

import java.sql.SQLException;
import java.util.*;

public class SwapEngine {

    private final NutrientAmountDAO nutrientAmountDAO = new DatabaseNutrientAmountDAO();
    private final NutrientNameDAO nutrientNameDAO = new DatabaseNutrientNameDao();
    private final FoodNameDAO foodNameDAO = new DatabaseFoodNameDAO();

    public List<Meal> applySwap(List<Meal> meals, SwapRequest request) {
        NutrientType target = request.getTargetNutrient();
        List<Meal> swappedMeals = new ArrayList<>();

        for (Meal meal : meals) {
            Meal.Builder builder = new Meal.Builder().withDate(meal.getDate()).withId(meal.getId());
            int swapCount = 0;

            for (Food food : meal.getItems()) {
                if (swapCount >= 2) {
                    builder.add(food);
                    continue;
                }

                double targetValue = food.getNutrients().getOrDefault(target, 0.0);

                if (targetValue < 5.0) {
                    try {
                        Optional<Food> better = findBetterSwap(food, target, request);
                        if (better.isPresent()) {
                            builder.add(better.get());
                            swapCount++;
                            continue;
                        }
                    } catch (SQLException e) {
                        System.err.println("DB error during swap: " + e.getMessage());
                    }
                }

                builder.add(food);
            }

            swappedMeals.add(builder.build());
        }

        return swappedMeals;
    }

    private Optional<Food> findBetterSwap(Food original, NutrientType targetNutrient, SwapRequest request)
        throws SQLException {
        int targetId = mapNutrientTypeToId(targetNutrient);
        Map<NutrientType, Double> originalNutrients = original.getNutrients();
        String originalGroup = guessGroup(original.getName());
        List<NutrientAmount> topFoods = new ArrayList<>();

        for (int id = 100; id <= 200; id++) {
            List<NutrientAmount> naList = nutrientAmountDAO.findByFoodId(id);
            Map<NutrientType, Double> candidateNutrients = new HashMap<>();

            for (NutrientAmount na : naList) {
                NutrientType type = mapIdToNutrientType(na.getNutrientNameId());
                if (type != null) {
                    candidateNutrients.put(type, na.getNutrientValue().doubleValue());
                }
            }

            Double candidateTargetVal = candidateNutrients.get(targetNutrient);
            double originalVal = originalNutrients.getOrDefault(targetNutrient, 0.0);

            boolean meetsGoal = false;
            if (candidateTargetVal != null) {
                if (request.isPercentage()) {
                    meetsGoal = candidateTargetVal >= originalVal * (1 + request.getIntensityAmount());
                } else {
                    meetsGoal = candidateTargetVal >= originalVal + request.getIntensityAmount();
                }
            }

            if (candidateTargetVal != null && candidateTargetVal > 5.0 &&
                meetsGoal &&
                isWithinThreshold(originalNutrients, candidateNutrients, targetNutrient)) {
                topFoods.add(new NutrientAmount(id, targetId, BigDecimal.valueOf(candidateTargetVal)));
            }
        }

        topFoods.sort((a, b) -> {
            try {
                String descA = foodNameDAO.findDescriptionById(a.getFoodId());
                String descB = foodNameDAO.findDescriptionById(b.getFoodId());

                String groupA = guessGroup(descA);
                String groupB = guessGroup(descB);

                boolean sameGroupA = groupA.equals(originalGroup);
                boolean sameGroupB = groupB.equals(originalGroup);

                if (sameGroupA && !sameGroupB)
                    return -1;
                if (!sameGroupA && sameGroupB)
                    return 1;

                return Double.compare(b.getNutrientValue().doubleValue(), a.getNutrientValue().doubleValue());
            } catch (SQLException e) {
                System.err.println("Error comparing food groups: " + e.getMessage());
                return 0;
            }
        });

        for (NutrientAmount na : topFoods) {
            String desc = foodNameDAO.findDescriptionById(na.getFoodId());
            if (desc != null && !desc.equalsIgnoreCase(original.getName())) {
                List<NutrientAmount> naList = nutrientAmountDAO.findByFoodId(na.getFoodId());
                Map<NutrientType, Double> candidateNutrients = new HashMap<>();
                for (NutrientAmount item : naList) {
                    NutrientType type = mapIdToNutrientType(item.getNutrientNameId());
                    if (type != null) {
                        candidateNutrients.put(type, item.getNutrientValue().doubleValue());
                    }
                }

                double protein = candidateNutrients.getOrDefault(NutrientType.Protein, 0.0);
                double carbs = candidateNutrients.getOrDefault(NutrientType.Carbohydrate, 0.0);
                double fat = candidateNutrients.getOrDefault(NutrientType.Fat, 0.0);
                double calories = (4 * protein + 4 * carbs + 9 * fat) * (original.getQuantity() / 100.0);
                candidateNutrients.put(NutrientType.Calories, calories);
                Food swapped = new Food(na.getFoodId(), desc, original.getQuantity(), calories);
                swapped.setNutrients(candidateNutrients);
                swapped.setCalories(calories);
                return Optional.of(swapped);
            }
        }

        return Optional.empty();
    }

    private int mapNutrientTypeToId(NutrientType type) {
        return switch (type) {
            case Protein -> 1003;
            case Carbohydrate -> 1005;
            case Fat -> 1004;
            case Fiber -> 1079;
            default -> 9999;
        };
    }

    private NutrientType mapIdToNutrientType(int id) {
        return switch (id) {
            case 1003 -> NutrientType.Protein;
            case 1005 -> NutrientType.Carbohydrate;
            case 1004 -> NutrientType.Fat;
            case 1079 -> NutrientType.Fiber;
            case 208  -> NutrientType.Calories;
            default   -> null;
        };

    }

    private boolean isWithinThreshold(Map<NutrientType, Double> original,
                                      Map<NutrientType, Double> candidate,
                                      NutrientType target) {
        for (NutrientType type : original.keySet()) {
            if (type == target)
                continue;

            double origVal = original.getOrDefault(type, 0.0);
            double candVal = candidate.getOrDefault(type, 0.0);

            if (origVal == 0.0 && candVal != 0.0)
                return false;
            if (origVal != 0.0) {
                double diffRatio = Math.abs(candVal - origVal) / origVal;
                if (diffRatio > 0.10)
                    return false;
            }
        }
        return true;
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
}

