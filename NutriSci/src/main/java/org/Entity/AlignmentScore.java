package org.Entity;
import org.Enums.FoodGroup;
import java.util.Collections;
import java.util.Map;

public class AlignmentScore {

    private double score;

    private Map<FoodGroup, Double> details;

    public AlignmentScore(double score, Map<FoodGroup, Double> details) {

        this.score = score;
        this.details = details;
    }

    public double getScore() {

        return score;
    }

    public void setScore(double score) {

        this.score = score;
    }

    public Map<FoodGroup, Double> getDetails() {

        return Collections.unmodifiableMap(details);
    }

    public void setDetails(Map<FoodGroup, Double> details) {

        this.details = details;
    }

    public double getScoreForGroup(FoodGroup group) {

        return details.getOrDefault(group, 0.0);
    }

    // debug
    @Override
    public String toString() {

        return String.format("AlignmentScore [score=%.1f, details=%s]", score, details);
    }
}
