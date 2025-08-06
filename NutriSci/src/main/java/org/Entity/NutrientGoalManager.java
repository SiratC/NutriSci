package org.Entity;

import org.Enums.NutrientType;
import java.util.HashMap;
import java.util.Map;

public class NutrientGoalManager {
    private Map<NutrientType, Double> goals = new HashMap<>();

    public void setGoal(NutrientType type, double value) {
        goals.put(type, value);
    }

    public Map<NutrientType, Double> getGoals() {
        return goals;
    }

    public void setGoals(Map<NutrientType, Double> newGoals) {
        this.goals = newGoals;
    }
}

