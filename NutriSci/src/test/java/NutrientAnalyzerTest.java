
import org.Entity.Food;
import org.Entity.Meal;
import org.Handlers.Logic.NutrientAnalyzer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;

public class NutrientAnalyzerTest {
    @Test
    void getTotalItemsTest1() { // no item
        NutrientAnalyzer na = new NutrientAnalyzer();
        Assertions.assertEquals(0, na.analyze(List.of()).getTotalItems());
    }

    @Test
    void getTotalItemsTest2() { // with 1 added item
        Meal m = new Meal(LocalDate.now());
        m.addItem(new Food(1001, "Oatmeal", 1, 100));
        NutrientAnalyzer na = new NutrientAnalyzer();
        Assertions.assertEquals(1, na.analyze(List.of(m)).getTotalItems());
    }
}