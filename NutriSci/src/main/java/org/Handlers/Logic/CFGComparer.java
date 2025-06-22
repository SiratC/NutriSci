package org.Handlers.Logic;

import org.Entity.AlignmentScore;
import org.Entity.FoodGroupStats;
import org.Enums.CFGVersion;
import org.Enums.FoodGroup;

/**
 * Implements {@link Analyzer} and scores the meal's food group stats.
 */
public class CFGComparer implements Analyzer<FoodGroupStats, AlignmentScore> {
    /**
     * Analyzes the meal's adherence to the CFG and returns a score to the user.
     * @param stats the statistics of the meal
     * @return the analysis score based on the CFG
     */
    @Override
    public AlignmentScore analyze(FoodGroupStats stats) {
        // Default version
        return analyze(stats, CFGVersion.V2019);
    }

    /**
     * Analyzes the meals adherence based on a given CFG.
     * @param stats the statistics of the meal
     * @param version the version of the CFG being used
     * @return the analysis score based on the CFG
     */
    public AlignmentScore analyze(FoodGroupStats stats, CFGVersion version) {

        return new AlignmentScore();
    }
}
