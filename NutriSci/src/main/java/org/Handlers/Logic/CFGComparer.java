package org.Handlers.Logic;

import org.Entity.AlignmentScore;
import org.Entity.FoodGroupStats;
import org.Entity.Meal;
import org.Enums.CFGVersion;
import org.Enums.FoodGroup;

import java.util.*;

public class CFGComparer implements Analyzer<FoodGroupStats, AlignmentScore> {


    private static final Map<CFGVersion, Map<FoodGroup, Double>> CFG_TARGETS = Map.of(
        CFGVersion.V2019, Map.of(
            FoodGroup.Vegetables, 50.0,
            FoodGroup.Protein, 25.0,
            FoodGroup.Grains, 25.0
        ),
        CFGVersion.V2007, Map.of(
            FoodGroup.Vegetables, 35.0,
            FoodGroup.Protein, 30.0,
            FoodGroup.Grains, 35.0
        )
    );

    @Override
    public AlignmentScore analyze(FoodGroupStats stats) {
        return analyze(stats, CFGVersion.V2019);
    }

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


    private Map<FoodGroup, Double> getTargets(CFGVersion version) {
        return new HashMap<>(CFG_TARGETS.getOrDefault(version, CFG_TARGETS.get(CFGVersion.V2019)));
    }

    public void update(String action, UUID userId, List<Meal> meals) {
        System.out.println("[CFGComparer] update triggered: " + action + " for user " + userId);
    }
}

