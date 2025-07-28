package org.Handlers.Logic;

import org.Enums.NutrientType;
import java.util.Map;

public interface NutrientLookup {

    Map<NutrientType,Double> getPerUnit(String foodName);
}