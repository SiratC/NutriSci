import static org.junit.jupiter.api.Assertions.*;
import org.Entity.*;
import org.Enums.*;
import org.Handlers.Controller.MealManager;
import org.Handlers.Controller.ProfileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class MealManagerTest {

    private MealManager mealManager;
    private ProfileManager profileManager;
    private Profile profile;
    private UUID userId;

    @BeforeEach
    void setUp() {
        mealManager = MealManager.getInstance();

        profileManager = ProfileManager.getInstance();
        userId = UUID.randomUUID();

        profile = new Profile(
            userId,
            "Alex",
            "pass123",
            Sex.Female,
            LocalDate.of(2000, 1, 1),
            165.0,
            60.0,
            "metric",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        profileManager.saveProfile(profile);

        profile = profileManager.loadProfileByName("Alex");
    }

    @Test
    void testRequestCFGAlignment_PerfectMatch_V2019() {
        Meal meal = new Meal(UUID.randomUUID(),LocalDate.now(), "Dinner");
        meal.addItem(new Food(2374, "Broccoli", 1.0, 200));
        meal.addItem(new Food(563, "Chicken", 1.0, 100));
        meal.addItem(new Food(4497, "Rice", 1.0, 100));

        mealManager.logMeal(profile.getUserID(), meal);

        DateRange range = new DateRange(LocalDate.now(), LocalDate.now());
        CFGReport report = mealManager.requestCFGAlignment(userId, range, CFGVersion.V2019);
        AlignmentScore score = report.getAlignmentScore();

        assertEquals(66.6, score.getScore(), 0.1);
        assertEquals(50.0, score.getScoreForGroup(FoodGroup.Vegetables), 0.1);
        assertEquals(75.0, score.getScoreForGroup(FoodGroup.Protein), 0.1);
        assertEquals(75.0, score.getScoreForGroup(FoodGroup.Grains), 0.1);
    }
}



