import static org.junit.jupiter.api.Assertions.*;
import org.Entity.Food;
import org.Enums.NutrientType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class FoodTest {

    private Food food;

    @BeforeEach
    void setUp() {
        food = new Food(1, "Sample Food", 1.0, 200.0);
    }

    @Test
    void testNutrientBreakdown() {
        Map<NutrientType, Double> nutrients = food.getNutrients();

        assertEquals(50.0, nutrients.get(NutrientType.Protein), 0.001);
        assertEquals(100.0, nutrients.get(NutrientType.Carbohydrate), 0.001);
        assertEquals(40.0, nutrients.get(NutrientType.Fat), 0.001);
        assertEquals(10.0, nutrients.get(NutrientType.Fiber), 0.001);
    }

    @Test
    void testGetters() {
        assertEquals(1, food.getFoodID());
        assertEquals("Sample Food", food.getName());
        assertEquals(1.0, food.getQuantity(), 0.001);
        assertEquals(200.0, food.getCalories(), 0.001);
    }

    @Test
    void testSetters() {
        food.setName("Updated Food");
        food.setFoodID(2);
        food.setQuantity(3.0);
        food.setCalories(300.0);

        assertEquals("Updated Food", food.getName());
        assertEquals(2, food.getFoodID());
        assertEquals(3.0, food.getQuantity(), 0.001);
        assertEquals(300.0, food.getCalories(), 0.001);
    }

    @Test
    void testToStringFormat() {
        assertEquals("Sample Food (1.0g): 200.0 cal", food.toString());
    }
}
