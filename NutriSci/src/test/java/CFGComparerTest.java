import static org.junit.jupiter.api.Assertions.*;

import org.Entity.FoodGroupStats;
import org.Entity.AlignmentScore;
import org.Enums.CFGVersion;
import org.Enums.FoodGroup;
import org.Handlers.Logic.CFGComparer;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

public class CFGComparerTest {

    @Test
    void analyze_returnsPerfectScore_whenStatsMatchV2019Targets() {

        Map<FoodGroup, Double> expectedPercentages = new EnumMap<>(FoodGroup.class);
        expectedPercentages.put(FoodGroup.Vegetables, 50.0);
        expectedPercentages.put(FoodGroup.Protein, 25.0);
        expectedPercentages.put(FoodGroup.Grains, 25.0);

        FoodGroupStats stats = new FoodGroupStats();
        stats.setGroupPercentages(expectedPercentages);

        CFGComparer comparer = new CFGComparer();


        AlignmentScore result = comparer.analyze(stats, CFGVersion.V2019);


        assertEquals(100.0, result.getScore(), 0.01, "Expected overall alignment score to be 100");
        assertEquals(100.0, result.getScoreForGroup(FoodGroup.Vegetables), 0.01);
        assertEquals(100.0, result.getScoreForGroup(FoodGroup.Protein), 0.01);
        assertEquals(100.0, result.getScoreForGroup(FoodGroup.Grains), 0.01);
    }
}



