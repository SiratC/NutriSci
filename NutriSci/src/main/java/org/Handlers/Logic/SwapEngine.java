package org.Handlers.Logic;
import org.Dao.*;
import org.Entity.*;
import org.Enums.NutrientType;
import org.Handlers.Database.DatabaseFoodNameDAO;
import org.Handlers.Database.DatabaseNutrientAmountDAO;
import org.Handlers.Database.DatabaseNutrientNameDao;
import java.util.*;

/**
 * Handles swaps within a meal.
 */
public class SwapEngine {

    private final NutrientAmountDAO nutrientAmountDAO = new DatabaseNutrientAmountDAO();

    private final NutrientNameDAO nutrientNameDAO = new DatabaseNutrientNameDao();

    private final FoodNameDAO foodNameDAO = new DatabaseFoodNameDAO();

    /**
     * Processes the swap of a food item within a meal.
     *
     * @param meals the list of food items
     * @param request the food item to swap
     * @return the changed list of food items including the swap
     */
    public List<Meal> applySwap(List<Meal> meals, SwapRequest request) {
        NutrientType target = request.getTargetNutrient();

        // copies meals and swap in place
        List<Meal> swappedMeals = new ArrayList<>();

        for (Meal meal : meals) {
            Meal.Builder builder = new Meal.Builder().withDate(meal.getDate());

            for (Food food : meal.getItems()) {
                // determines nutrient contribution
                Map<NutrientType, Double> foodNutrients = food.getNutrients();
                double targetValue = foodNutrients.getOrDefault(target, 0.0);

                // checks if nutrient contribution is low; try to swap
                if (targetValue < 5.0) {
                    Optional<Food> better = findBetterSwap(food, target);
                    builder.add(better.orElse(food)); // if better swap found, use it
                } else {
                    builder.add(food);
                }
            }

            swappedMeals.add(builder.build());
        }

        return swappedMeals;
    }

    /**
     * Tries to find a better food (higher in target nutrient) from the DB.
     * @param original the original food item
     * @param targetNutrient the target nutrient to find
     * @return the preferred food item
     */

    private Optional<Food> findBetterSwap(Food original, NutrientType targetNutrient) {
        try {
            // map nutrient type to nutrientNameId from DB. For example, Protein = 1003
            int targetId = mapNutrientTypeToId(targetNutrient);
            List<NutrientAmount> amounts = nutrientAmountDAO.findByFoodId(original.getName().hashCode());

            // find foods high in the target nutrient
            List<NutrientAmount> topFoods = new ArrayList<>();
            for (int id = 100; id <= 8000; id++) { // arbitrary range
                List<NutrientAmount> naList = nutrientAmountDAO.findByFoodId(id);
                for (NutrientAmount na : naList) {
                    if (na.getNutrientNameId() == targetId && na.getNutrientValue().doubleValue() > 5.0) {
                        topFoods.add(na);
                    }
                }
            }

            // sorts by nutrient value
            topFoods.sort((a, b) -> Double.compare(b.getNutrientValue().doubleValue(), a.getNutrientValue().doubleValue()));

            for (NutrientAmount na : topFoods) {

                String desc = foodNameDAO.findDescriptionById(na.getFoodId());

                if (desc != null && !desc.equalsIgnoreCase(original.getName())) {

                    return Optional.of(new Food(na.getFoodId(), desc, original.getQuantity(), original.getCalories()));

                }
            }

        } catch (Exception e) {
            System.err.println("Swap error: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * dummy mapping (for debug), later implementing nutrientNameID from DB
     */

    private int mapNutrientTypeToId(NutrientType type) {
        return switch (type) {
            case Protein -> 1003;
            case Carbohydrate -> 1005;
            case Fat -> 1004;
            case Fiber -> 1079;
            default -> 9999;
        };
    }
}
