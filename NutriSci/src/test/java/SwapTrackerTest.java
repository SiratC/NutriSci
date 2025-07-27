import static org.junit.jupiter.api.Assertions.*;
import org.Entity.*;
import org.Enums.FoodGroup;
import org.Handlers.Logic.SwapTracker;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

public class SwapTrackerTest {

    @Test
    void testAnalyzeCFG_CalculatesCorrectDifference() {
        SwapTracker tracker = new SwapTracker();

        FoodGroupStats beforeStats = new FoodGroupStats();
        beforeStats.setGroupPercentages(Map.of(
                FoodGroup.Vegetables, 30.0,
                FoodGroup.Protein, 30.0,
                FoodGroup.Grains, 40.0
        ));

        FoodGroupStats afterStats = new FoodGroupStats();
        afterStats.setGroupPercentages(Map.of(
                FoodGroup.Vegetables, 50.0,
                FoodGroup.Protein, 25.0,
                FoodGroup.Grains, 25.0
        ));

        CFGDifference diff = tracker.analyzeCFG(beforeStats, afterStats);

        assertEquals(20.0, diff.getChangeForGroup(FoodGroup.Vegetables), 0.01);
        assertEquals(-5.0, diff.getChangeForGroup(FoodGroup.Protein), 0.01);
        assertEquals(-15.0, diff.getChangeForGroup(FoodGroup.Grains), 0.01);
    }

    @Test
    void testAnalyzeCFG_NoChange() {
        SwapTracker tracker = new SwapTracker();

        Map<FoodGroup, Double> same = Map.of(
                FoodGroup.Vegetables, 40.0,
                FoodGroup.Protein, 30.0,
                FoodGroup.Grains, 30.0
        );

        FoodGroupStats before = new FoodGroupStats();
        before.setGroupPercentages(same);

        FoodGroupStats after = new FoodGroupStats();
        after.setGroupPercentages(same);

        CFGDifference diff = tracker.analyzeCFG(before, after);

        assertEquals(0.0, diff.getChangeForGroup(FoodGroup.Vegetables), 0.01);
        assertEquals(0.0, diff.getChangeForGroup(FoodGroup.Protein), 0.01);
        assertEquals(0.0, diff.getChangeForGroup(FoodGroup.Grains), 0.01);
    }

    @Test
    void testAnalyzeCFG_MissingGroupDefaultsToZero() {
        SwapTracker tracker = new SwapTracker();

        FoodGroupStats before = new FoodGroupStats();
        before.setGroupPercentages(Map.of(FoodGroup.Protein, 20.0));

        FoodGroupStats after = new FoodGroupStats();
        after.setGroupPercentages(Map.of(FoodGroup.Vegetables, 40.0));

        CFGDifference diff = tracker.analyzeCFG(before, after);

        assertEquals(40.0, diff.getChangeForGroup(FoodGroup.Vegetables), 0.01);
        assertEquals(-20.0, diff.getChangeForGroup(FoodGroup.Protein), 0.01);
        assertEquals(0.0, diff.getChangeForGroup(FoodGroup.Grains), 0.01);
    }
}



