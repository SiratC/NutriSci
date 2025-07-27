import static org.junit.jupiter.api.Assertions.*;

import org.Entity.Food;
import org.Entity.Meal;
import org.Enums.NutrientType;
import org.Handlers.Logic.NutrientCalculator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

class NutrientCalculatorTest {

    // Stub class for predictable nutrient values
    static class StubNutrientLookup extends org.Handlers.Logic.DatabaseNutrientLookup {
        @Override
        public Map<NutrientType, Double> getPerUnit(int foodId) {
            Map<NutrientType, Double> nutrients = new EnumMap<>(NutrientType.class);
            nutrients.put(NutrientType.Protein, 10.0); // Stub: 10g protein per unit
            return nutrients;
        }
    }

    @Test
    void calculatesProteinCorrectlyFromStubLookup() {
        // Arrange
        Meal meal = new Meal(UUID.randomUUID(),LocalDate.now(), "Breakfast");
        meal.addItem(new Food(1001, "Oatmeal", 3, 100.0)); // Quantity = 3

        NutrientCalculator calculator = new NutrientCalculator(new StubNutrientLookup());

        // Act
        Map<NutrientType, Double> result = calculator.calculate(meal);

        // Assert
        double expectedProtein = 30.0; // 3 * 10.0
        double actualProtein = result.get(NutrientType.Protein);
        double delta = 1e-6;

        assertEquals(expectedProtein, actualProtein, delta,
                "Protein should be " + expectedProtein + " but was " + actualProtein);
    }
}





