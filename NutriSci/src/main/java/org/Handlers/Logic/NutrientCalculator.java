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

    private final NutrientLookup lookup;

    /**
     * A constructor using an existing NutrientLookup for usage in nutrient searching and calculation.
     * @param lookup the given nutrients to search
     */
    public NutrientCalculator(NutrientLookup lookup) {

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

            var perUnit = lookup.getPerUnit(f.getName());

            for (var t : NutrientType.values()) {
                totals.put(t, totals.get(t) + perUnit.get(t) * f.getQuantity());
            }

        }

        return totals;
    }
}
