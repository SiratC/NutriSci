package org.Entity;

import org.Enums.NutrientType;

import java.util.HashMap;
import java.util.Map;


/**
 * Handles the amount of progress a user currently has to the set goal.
 */
public class ProgressStatus {
    private Map<NutrientType, Double> intake;
    private Map<NutrientType, Double> goals;

    /**
     * Specifies a ProgressStatus with current intake and goals.
     * @param intake nutrient intake
     * @param goals nutrient goals
     */
    public ProgressStatus(Map<NutrientType, Double> intake, Map<NutrientType, Double> goals) {
        this.intake = intake;
        this.goals = goals;
    }

    /**
     * Returns nutrient intake.
     * @return nutrient intake
     */
    public Map<NutrientType, Double> getIntake() {
        return intake;
    }

    /**
     * Returns nutrient goals.
     * @return nutrient goals
     */
    public Map<NutrientType, Double> getGoals() {
        return goals;
    }

    /**
     * Returns the current progress of the goal.
     * @param type nutrient type of goal
     * @return percentage of progress to goal
     */
    public double progressOf(NutrientType type) {
        double goal = goals.getOrDefault(type, 0.0);
        if (goal <= 0) return 0.0;

        double value = intake.getOrDefault(type, 0.0);
        return Math.min(100.0, (value / goal) * 100.0);
    }

    /**
     * Checks if goal has been reached.
     * @param type nutrient type of goal
     * @return true if nutrient has reached goal, false otherwise
     */
    public boolean hasReached(NutrientType type) {
        return intake.getOrDefault(type, 0.0) >= goals.getOrDefault(type, 0.0);
    }

    /**
     * Returns progress of all nutrient types in goal.
     * @return map of desired nutrients
     */
    public Map<NutrientType, Double> getAllProgress() {

        Map<NutrientType, Double> progressMap = new HashMap<>();

        for (NutrientType type : goals.keySet()) {

            progressMap.put(type, progressOf(type));
        }
        return progressMap;
    }
}