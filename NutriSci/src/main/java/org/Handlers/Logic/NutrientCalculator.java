package org.Handlers.Logic;
import org.Entity.Food;
import org.Entity.Meal;
import org.Enums.NutrientType;
import java.util.EnumMap;
import java.util.Map;

public class NutrientCalculator {

    private final DatabaseNutrientLookup lookup;

    public NutrientCalculator(DatabaseNutrientLookup lookup) {

        this.lookup = lookup;

    }

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
