package org.Handlers.Logic;

import org.Enums.NutrientType;
import java.util.Map;

/**
 * An interface that handles nutrient searching in the database.
 */
public interface NutrientLookup {

    /**
     * Finds and returns the nutrients of the food specified in a map.
     *
     * @param foodName the name of the food
     * @return the nutrients given
     */
    Map<NutrientType,Double> getPerUnit(String foodName);
}