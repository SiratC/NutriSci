package org.Entity;
import org.Enums.FoodGroup;
import java.util.Collections;
import java.util.Map;

/**
 * Defines a report of the selected meal as it adheres to the CFG.
 */
public class CFGReport extends Report {

    private final Map<FoodGroup, Double> groupPercentages;

    private final AlignmentScore alignmentScore;

    /**
     * Defines an existing report with given alignment score and food group stats.
     * @param alignmentScore existing score
     * @param stats existing stats
     */
    public CFGReport(AlignmentScore alignmentScore, FoodGroupStats stats) {

        this.alignmentScore = alignmentScore;

        this.groupPercentages = stats.getGroupPercentages();
    }

    /**
     * Returns the food group percentages
     * @return map of food percentages
     */
    public Map<FoodGroup, Double> getGroupPercentages() {

        return Collections.unmodifiableMap(groupPercentages);
    }

    /**
     * Returns a specific food group's percentage
     * @param group specified food group
     * @return percentage of food
     */
    public double getPercentageFor(FoodGroup group) {

        return groupPercentages.getOrDefault(group, 0.0);
    }

    /**
     * Returns the alignment score.
     * @return alignment score
     */
    public AlignmentScore getAlignmentScore() {

        return alignmentScore;
    }

    @Override

    public String toString() {

        return "CFGReport[" + "generatedAt=" + generatedAt + ", groupPercentages=" + groupPercentages + ", alignmentScore=" + alignmentScore + ']';
    }
}
