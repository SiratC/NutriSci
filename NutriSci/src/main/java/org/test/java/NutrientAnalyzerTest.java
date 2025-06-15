
import org.Entity.Food;
import org.Entity.Meal;
import org.Handlers.Logic.NutrientAnalyzer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NutrientAnalyzerTest {
    @Test
    void test1() { // no item
        NutrientAnalyzer na = new NutrientAnalyzer();
        assertEquals(0, na.analyze(List.of()).getTotalItems());
    }

    @Test
    void test2() { // with 1 added item
        Meal m = new Meal(LocalDate.now());
        m.addItem(new Food("Egg", 2));
        NutrientAnalyzer na = new NutrientAnalyzer();
        assertEquals(1, na.analyze(List.of(m)).getTotalItems());

    }
}