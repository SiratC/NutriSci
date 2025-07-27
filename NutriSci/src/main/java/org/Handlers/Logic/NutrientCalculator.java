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

            Map<NutrientType, Double> perUnit = lookup.getPerUnit(f.getFoodID());

            for (NutrientType t : NutrientType.values()) {

                double updated = totals.get(t) + perUnit.getOrDefault(t, 0.0) * f.getQuantity();

                totals.put(t, updated);
            }
        }

        return totals;
    }

}
