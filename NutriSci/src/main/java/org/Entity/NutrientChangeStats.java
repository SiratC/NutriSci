package org.Entity;

import org.Enums.NutrientType;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


/**
 * Holds the information of nutrient changes over time.
 */
public class NutrientChangeStats {

    private Map<LocalDate, Map<NutrientType, Double>> changesOverTime = new HashMap<>();

    /**
     * Adds the change over time of a nutrient.
     * @param date the time specified
     * @param type the nutrient type
     * @param value the value of the nutrient
     */
    public void addChange(LocalDate date, NutrientType type, double value) {

        changesOverTime
                .computeIfAbsent(date, d -> new HashMap<>())
                .merge(type, value, Double::sum);
    }

    /**
     * Returns the changes over time.
     * @return changes of nutrient over date
     */
    public Map<LocalDate, Map<NutrientType, Double>> getChangesOverTime() {
        return changesOverTime;
    }

    /**
     * Sets the changes over time.
     * @param data the nutrient data specified
     */
    public void setChangesOverTime(Map<LocalDate, Map<NutrientType, Double>> data) {
        this.changesOverTime = data;
    }

    @Override //debug
    public String toString() {

        return "NutrientChangeStats [" + "changesOverTime=" + changesOverTime + ']';
    }
}
