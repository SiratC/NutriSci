package org.Handlers.Logic;
import org.Enums.NutrientType;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

// dummy visualizer for now
public class InMemNutrientLookUp implements NutrientLookup {

    private final Map<String, Map<NutrientType,Double>> data = new HashMap<>();

    public InMemNutrientLookUp() {

        EnumMap<NutrientType,Double>  map = new EnumMap<NutrientType,Double>(NutrientType.class);


        map.put(NutrientType.Protein,    10.0);
        map.put(NutrientType.Fat,         5.0);
        map.put(NutrientType.Carbohydrate,20.0);
        map.put(NutrientType.Fiber,       8.0);
        map.put(NutrientType.Other,       2.0);
        data.put("Test", map);
    }

    @Override

    public Map<NutrientType,Double> getPerUnit(String foodName) {

        if (!data.containsKey(foodName)) {
            throw new IllegalArgumentException("Unknown food: " + foodName);
        }
        return data.get(foodName);
    }
}
