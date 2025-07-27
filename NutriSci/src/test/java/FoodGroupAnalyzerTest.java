import static org.junit.jupiter.api.Assertions.*;
import org.Entity.Food;
import org.Entity.FoodGroupStats;
import org.Entity.Meal;
import org.Enums.FoodGroup;
import org.Handlers.Logic.FoodGroupAnalyzer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FoodGroupAnalyzerTest {

    @Test
    void analyze_mixedFoodTypes_returnsCorrectPercentages() {
        Meal meal = new Meal(UUID.randomUUID(),LocalDate.now(), "Lunch");
        meal.addItem(new Food(1, "Chicken", 1.0, 200.0));    // Protein
        meal.addItem(new Food(2, "Broccoli", 1.0, 100.0));   // Vegetables
        meal.addItem(new Food(3, "Rice", 1.0, 100.0));       // Grains

        FoodGroupAnalyzer analyzer = new FoodGroupAnalyzer();
        FoodGroupStats result = analyzer.analyze(List.of(meal));
        Map<FoodGroup, Double> percentages = result.getGroupPercentages();

        assertEquals(50.0, percentages.get(FoodGroup.Protein), 0.1);
        assertEquals(25.0, percentages.get(FoodGroup.Vegetables), 0.1);
        assertEquals(25.0, percentages.get(FoodGroup.Grains), 0.1);
        assertEquals(3, percentages.size());
    }

    @Test
    void analyze_unknownFoodName_defaultsToOtherGroup() {
        Meal meal = new Meal(UUID.randomUUID(),LocalDate.now(), "Snack");
        meal.addItem(new Food(4, "MysterySnack", 1.0, 300.0));  // Should go to Other

        FoodGroupAnalyzer analyzer = new FoodGroupAnalyzer();
        FoodGroupStats result = analyzer.analyze(List.of(meal));
        Map<FoodGroup, Double> percentages = result.getGroupPercentages();

        assertEquals(100.0, percentages.get(FoodGroup.Other), 0.1);
        assertEquals(1, percentages.size());
    }

    @Test
    void analyze_emptyMeal_returnsEmptyGroupPercentages() {
        Meal meal = new Meal(UUID.randomUUID(),LocalDate.now(), "Snack");

        FoodGroupAnalyzer analyzer = new FoodGroupAnalyzer();
        FoodGroupStats result = analyzer.analyze(List.of(meal));

        assertTrue(result.getGroupPercentages().isEmpty());
    }
}

