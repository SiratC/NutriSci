package org.Entity;

import org.Enums.NutrientType;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class NutrientChangeStats {

    private Map<LocalDate, Map<NutrientType, Double>> changesOverTime = new HashMap<>();

    public void addChange(LocalDate date, NutrientType type, double value) {

        changesOverTime
                .computeIfAbsent(date, d -> new HashMap<>())
                .merge(type, value, Double::sum);
    }

    public Map<LocalDate, Map<NutrientType, Double>> getChangesOverTime() {
        return changesOverTime;
    }

    public void setChangesOverTime(Map<LocalDate, Map<NutrientType, Double>> data) {
        this.changesOverTime = data;
    }

    @Override //debug
    public String toString() {

        return "NutrientChangeStats [" + "changesOverTime=" + changesOverTime + ']';
    }
}
