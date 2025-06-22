import static org.junit.jupiter.api.Assertions.*;
import org.Entity.Food;
import org.Entity.Meal;
import org.Handlers.Logic.InMemNutrientLookUp;
import org.Handlers.Logic.NutrientCalculator;
import org.Enums.NutrientType;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Map;

class NutrientCalculatorTest {
    @Test
    void dummyLoopUp() {
        Meal meal = new Meal(LocalDate.now());
        meal.addItem(new Food("Test", 3));

        NutrientCalculator calc = new NutrientCalculator(new InMemNutrientLookUp());

        Map<NutrientType, Double> result = calc.calculate(meal);

        // for exact number
        assertTrue(result.get(NutrientType.Protein) == 30.0); // 3 (quantity) x 10 (protein) = 30

        // for floating number
        double expected = 30.0;
        double actual   = result.get(NutrientType.Protein);
        double e  = 1e-6;

        assertTrue(Math.abs(actual - expected) < e,
                "Protein should approximately = " + expected + " but was " + actual);
    }
}

