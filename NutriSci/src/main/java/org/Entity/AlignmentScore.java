package org.Entity;
import org.Enums.FoodGroup;
import java.util.Collections;
import java.util.Map;

/**
 * Manages alignment score based on CFG Guidelines.
 */
public class AlignmentScore {

    private double score;

    private Map<FoodGroup, Double> details;

    /**
     * Creates new AlignmentScore with given information of score and details.
     * @param score the alignment score defined
     * @param details the details of the alignment score
     */
    public AlignmentScore(double score, Map<FoodGroup, Double> details) {

        this.score = score;
        this.details = details;
    }

    /**
     * Returns the alignment score.
     * @return score
     */
    public double getScore() {

        return score;
    }

    /**
     * Sets the alignment score.
     * @param score alignment score
     */
    public void setScore(double score) {

        this.score = score;
    }

    /**
     * Returns the details of the alignment score.
     * @return map of details
     */
    public Map<FoodGroup, Double> getDetails() {

        return Collections.unmodifiableMap(details);
    }

    /**
     * Sets the details of the alignment score.
     * @param details map of details
     */
    public void setDetails(Map<FoodGroup, Double> details) {

        this.details = details;
    }

    /**
     * Returns the score of the food group specified
     * @param group food group
     * @return alignment score of food group
     */
    public double getScoreForGroup(FoodGroup group) {

        return details.getOrDefault(group, 0.0);
    }

    // debug
    @Override
    public String toString() {

        return String.format("AlignmentScore [score=%.1f, details=%s]", score, details);
    }
}
