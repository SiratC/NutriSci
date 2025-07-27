import static org.junit.jupiter.api.Assertions.*;
import org.Entity.*;
import org.Enums.CFGVersion;
import org.Handlers.Logic.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class AnalyzerFactoryTest {

    private AnalyzerFactory factory;

    @BeforeEach
    void setUp() {
        factory = new AnalyzerFactory();
    }

    @Test
    void createTrendAnalyzer_returnsTrendAnalyzerInstance() {
        Analyzer<List<Meal>, TrendResult> analyzer = factory.createTrendAnalyzer();
        assertNotNull(analyzer, "TrendAnalyzer should not be null");
        assertTrue(analyzer instanceof TrendAnalyzer, "Expected instance of TrendAnalyzer");
    }

    @Test
    void createFoodGroupAnalyzer_returnsFoodGroupAnalyzerInstance() {
        Analyzer<List<Meal>, FoodGroupStats> analyzer = factory.createFoodGroupAnalyzer();
        assertNotNull(analyzer, "FoodGroupAnalyzer should not be null");
        assertTrue(analyzer instanceof FoodGroupAnalyzer, "Expected instance of FoodGroupAnalyzer");
    }

    @Test
    void createNutrientAnalyzer_returnsNutrientAnalyzerInstance() {
        Analyzer<List<Meal>, NutrientStats> analyzer = factory.createNutrientAnalyzer();
        assertNotNull(analyzer, "NutrientAnalyzer should not be null");
        assertTrue(analyzer instanceof NutrientAnalyzer, "Expected instance of NutrientAnalyzer");
    }

    @Test
    void createSwapTracker_returnsSwapTrackerInstance() {
        SwapTracker tracker = factory.createSwapTracker();
        assertNotNull(tracker, "SwapTracker should not be null");
        assertTrue(tracker instanceof SwapTracker, "Expected instance of SwapTracker");
    }

    @Test
    void createCFGComparer_returnsCFGComparerInstance() {
        CFGComparer comparer = factory.createCFGComparer();
        assertNotNull(comparer, "CFGComparer should not be null");
        assertTrue(comparer instanceof CFGComparer, "Expected instance of CFGComparer");
    }
}

