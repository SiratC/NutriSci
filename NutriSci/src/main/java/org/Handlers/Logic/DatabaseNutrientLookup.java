package org.Handlers.Logic;

import org.Handlers.Database.DatabaseNutrientAmountDAO;
import org.Dao.NutrientAmountDAO;
import org.Enums.NutrientType;
import org.Entity.NutrientAmount;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseNutrientLookup implements NutrientLookup {

    private final NutrientAmountDAO dao = new DatabaseNutrientAmountDAO();

    private final Map<Integer, NutrientType> idToType = Map.of(203, NutrientType.Protein, 204, NutrientType.Fat, 205,
            NutrientType.Carbohydrate, 291, NutrientType.Fiber);

    @Override

    public Map<NutrientType, Double> getPerUnit(String foodName) {

        throw new UnsupportedOperationException("Use the foodID version.");
    }

    // for debug purpose
    public Map<NutrientType, Double> getPerUnit(int foodId) {

        System.out.println("fetching nutrient data for foodID: " + foodId);

        try {
            List<NutrientAmount> amounts = dao.findByFoodId(foodId);

            for (NutrientAmount na : amounts) {
                System.out.println("raw nutrient ID: " + na.getNutrientNameId() + ", value: " + na.getNutrientValue());

                if (!idToType.containsKey(na.getNutrientNameId())) {
                    System.out.println("skipping unmapped nutrient ID: " + na.getNutrientNameId());
                }
            }

            return amounts.stream()
                    .filter(na -> idToType.containsKey(na.getNutrientNameId()))
                    .collect(Collectors.toMap(
                            na -> idToType.get(na.getNutrientNameId()),
                            na -> na.getNutrientValue().doubleValue()));
        }

        catch (Exception e) {

            throw new RuntimeException("failed DB lookup for foodID " + foodId, e);
        }
    }

}
