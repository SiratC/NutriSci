package org.Entity;

import org.Enums.NutrientType;

import java.time.LocalDate;
import java.util.Map;

/**
 * Holds the information of nutrient changes over time.
 */
public class NutrientChangeStats {
    private Map<LocalDate, Map<NutrientType, Double>> changesOverTime;
}
