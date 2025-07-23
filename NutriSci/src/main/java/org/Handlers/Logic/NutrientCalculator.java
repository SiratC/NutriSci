package org.Handlers.Logic;
import org.Entity.Food;
import org.Entity.Meal;
import org.Enums.NutrientType;
import java.util.EnumMap;
import java.util.Map;

/**
 * Handles the calculation and information of nutrients.
 * <p>Used for looking up nutrients and calculating the given meal's nutrient content</p>
 */
public class NutrientCalculator {

    private final DatabaseNutrientLookup lookup;

    /**
     * NutrientCalculator constructor with a defined look up for the database
     * @param lookup nutrient to look for
     */
    public NutrientCalculator(DatabaseNutrientLookup lookup) {

        this.lookup = lookup;

    }

    /**
     * Calculates the nutrients within the meal given.
     *
     * @param meal the user's meal
     * @return a map of nutrient types and nutrient amounts
     */
    public Map<NutrientType, Double> calculate(Meal meal) {

        Map<NutrientType, Double> totals = new EnumMap<>(NutrientType.class);

        for (NutrientType t : NutrientType.values()) {

            totals.put(t, 0.0);
        }

        for (Food f : meal.getItems()) {

            Map<NutrientType, Double> perUnit = lookup.getPerUnit(f.getFoodID());

            for (NutrientType t : NutrientType.values()) {

                double updated = totals.get(t) + perUnit.getOrDefault(t, 0.0) * f.getQuantity();

                totals.put(t, updated);
            }
        }

        return totals;
    }

}
