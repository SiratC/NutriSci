package org.Dao;

import org.Entity.Food;
import org.Entity.Meal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface MealLogDAO {
    UUID insertMeal(UUID profileId, String type, List<Food> foods) throws SQLException;

    List<Meal> getAllMealsByProfileId(UUID profileId) throws SQLException;

    List<Food> getAllFoodsByMealId(UUID mealId) throws SQLException;

    List<Meal> getMealsByDateRange(UUID profileId, String startDate, String endDate) throws SQLException;

    List<Meal> getMealsByDate(UUID profileId, String date) throws SQLException;

    void updateMeal(UUID mealId, List<Food> foods) throws SQLException;

    void removeMeal(UUID mealId) throws SQLException;

    Meal getMealById(UUID mealId) throws SQLException;
}
