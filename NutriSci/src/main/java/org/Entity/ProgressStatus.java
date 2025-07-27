package org.Entity;

import org.Enums.NutrientType;

import java.util.HashMap;
import java.util.Map;

public class ProgressStatus {
    private Map<NutrientType, Double> intake;
    private Map<NutrientType, Double> goals;

    public ProgressStatus(Map<NutrientType, Double> intake, Map<NutrientType, Double> goals) {
        this.intake = intake;
        this.goals = goals;
    }

    public Map<NutrientType, Double> getIntake() {
        return intake;
    }

    public Map<NutrientType, Double> getGoals() {
        return goals;
    }

    public double progressOf(NutrientType type) {
        double goal = goals.getOrDefault(type, 0.0);
        if (goal <= 0) return 0.0;

        double value = intake.getOrDefault(type, 0.0);
        return Math.min(100.0, (value / goal) * 100.0);
    }

    public boolean hasReached(NutrientType type) {
        return intake.getOrDefault(type, 0.0) >= goals.getOrDefault(type, 0.0);
    }

    public Map<NutrientType, Double> getAllProgress() {

        Map<NutrientType, Double> progressMap = new HashMap<>();

        for (NutrientType type : goals.keySet()) {

            progressMap.put(type, progressOf(type));
        }
        return progressMap;
    }
    public double getOverallCompletion() {
        if (goals.isEmpty()) return 0.0;

        double total = 0;
        int count = 0;

        for (NutrientType type : goals.keySet()) {
            if (goals.get(type) > 0) {
                total += progressOf(type);
                count++;
            }
        }

        return count > 0 ? total / count : 0.0;
    }
}