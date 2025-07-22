package org.Handlers.Logic;
import org.Handlers.Database.DatabaseNutrientAmountDAO;
import org.Dao.NutrientAmountDAO;
import org.Enums.NutrientType;
import org.Entity.NutrientAmount;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseNutrientLookup implements NutrientLookup {

    private final NutrientAmountDAO dao = new DatabaseNutrientAmountDAO();

    private final Map<Integer, NutrientType> idToType = Map.of(1003, NutrientType.Protein, 1004, NutrientType.Fat, 1005, NutrientType.Carbohydrate, 1079, NutrientType.Fiber);


    @Override

    public Map<NutrientType, Double> getPerUnit(String foodName) {

        throw new UnsupportedOperationException("Use the foodID version.");
    }

    public Map<NutrientType, Double> getPerUnit(int foodId) {

        try {
            List<NutrientAmount> amounts = dao.findByFoodId(foodId);

            return amounts.stream()

                    .filter(na -> idToType.containsKey(na.getNutrientNameId()))
                    .collect(Collectors.toMap(na -> idToType.get(na.getNutrientNameId()), na -> na.getNutrientValue().doubleValue()));
        }

        catch (Exception e) {

            throw new RuntimeException("Failed DB lookup for foodID " + foodId, e);
        }
    }
}
