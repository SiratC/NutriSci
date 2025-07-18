package org.Entity;
import org.Enums.FoodGroup;
import java.util.Collections;
import java.util.Map;

public class CFGReport extends Report {

    private final Map<FoodGroup, Double> groupPercentages;

    private final AlignmentScore alignmentScore;

    public CFGReport(AlignmentScore alignmentScore, FoodGroupStats stats) {

        this.alignmentScore = alignmentScore;

        this.groupPercentages = stats.getGroupPercentages();
    }

    public Map<FoodGroup, Double> getGroupPercentages() {

        return Collections.unmodifiableMap(groupPercentages);
    }

    public double getPercentageFor(FoodGroup group) {

        return groupPercentages.getOrDefault(group, 0.0);
    }

    public AlignmentScore getAlignmentScore() {

        return alignmentScore;
    }

    @Override

    public String toString() {

        return "CFGReport[" + "generatedAt=" + generatedAt + ", groupPercentages=" + groupPercentages + ", alignmentScore=" + alignmentScore + ']';
    }
}
