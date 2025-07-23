package org.Handlers.Logic;
import org.Entity.AlignmentScore;
import org.Entity.FoodGroupStats;
import org.Entity.Meal;
import org.Enums.CFGVersion;
import org.Enums.FoodGroup;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implements {@link Analyzer} and scores the meal's food group stats.
 */
public class CFGComparer implements Analyzer<FoodGroupStats, AlignmentScore> {

    @Override
    public AlignmentScore analyze(FoodGroupStats stats) {
        
        return analyze(stats, CFGVersion.V2019);
    }

    /**
     * Analyzes the meals adherence based on a given CFG.
     * @param stats the statistics of the meal
     * @param version the version of the CFG being used
     * @return the analysis score based on the CFG
     */
    public AlignmentScore analyze(FoodGroupStats stats, CFGVersion version) {
        Map<FoodGroup, Double> targets = getTargets(version);
        Map<FoodGroup, Double> actuals = stats.getGroupPercentages();

        Map<FoodGroup, Double> alignmentMap = new EnumMap<>(FoodGroup.class);
        double totalScore = 0;
        int count = 0;

        for (FoodGroup group : targets.keySet()) {
            double expected = targets.get(group);
            double actual = actuals.getOrDefault(group, 0.0);
            double score = 100.0 - Math.min(100.0, Math.abs(expected - actual));
            alignmentMap.put(group, score);
            totalScore += score;
            count++;
        }

        double avgScore = (count > 0) ? totalScore / count : 0.0;
        return new AlignmentScore(avgScore, alignmentMap);
    }

    /**
     * Returns food group targets based on CFG version information.
     * @param version cfg version used
     * @return map of food group targets
     */
    private Map<FoodGroup, Double> getTargets(CFGVersion version) {
        Map<FoodGroup, Double> map = new EnumMap<>(FoodGroup.class);

        if (version == CFGVersion.V2019) {
            map.put(FoodGroup.Vegetables, 50.0);
            map.put(FoodGroup.Protein, 25.0);
            map.put(FoodGroup.Grains, 25.0);
        } 
        else if (version == CFGVersion.V2007) {
            map.put(FoodGroup.Vegetables, 35.0);
            map.put(FoodGroup.Protein, 30.0);
            map.put(FoodGroup.Grains, 35.0);
        } 
        else {
            map.put(FoodGroup.Vegetables, 50.0);
            map.put(FoodGroup.Protein, 25.0);
            map.put(FoodGroup.Grains, 25.0);
        }

        return map;
    }

    /**
     * Updates the comparison based on action and meals.
     * @param action change for user.
     * @param userId the user's ID
     * @param meals the list of meals
     */
    public void update(String action, UUID userId, List<Meal> meals) {
        
        System.out.println("[CFGComparer] update triggered: " + action + " for user " + userId);
    }
}

