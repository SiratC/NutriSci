package org.Handlers.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.Dao.MealLogDAO;
import org.Entity.Food;
import org.Entity.Meal;

public class DatabaseMealLogDAO implements MealLogDAO {
    @Override
    public UUID insertMeal(UUID profileId, String type, List<Food> foods) throws SQLException {
        String sql = """
                INSERT INTO MealLogs
                  (profileId, type)
                VALUES (?, ?)
                RETURNING id
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, profileId);
            ps.setString(2, type);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // Move cursor to first row
                UUID mealLogId = rs.getObject("id", UUID.class);

                // insert all provided food items into junction table MealLogFoods
                foods.forEach(food -> {
                    String foodSql = """
                            INSERT INTO MealLogFoods
                              (logId, foodId, quantity)
                            VALUES (?, ?, ?)
                            ON CONFLICT DO NOTHING
                            """;
                    try (PreparedStatement foodPs = conn.prepareStatement(foodSql)) {
                        foodPs.setObject(1, mealLogId);
                        foodPs.setInt(2, food.getFoodID());
                        foodPs.setDouble(3, food.getQuantity());
                        foodPs.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                return mealLogId;
            } else {
                throw new SQLException("Failed to insert meal - no ID returned");
            }

        }
    }

    @Override
    public List<Meal> getAllMealsByProfileId(UUID profileId) throws SQLException {
        String sql = """
                SELECT * FROM MealLogs
                WHERE profileId = ?
                """;
        List<Meal> meals = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, profileId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID mealId = rs.getObject("id", UUID.class);
                Meal.Builder builder = new Meal.Builder();
                builder.withDate(rs.getObject("createdAt", java.time.LocalDate.class)).withId(mealId)
                        .withType(rs.getString("type"));

                List<Food> foods = getAllFoodsByMealId(mealId);
                foods.forEach(builder::add);

                meals.add(builder.build());
            }
        }
        return meals;
    }

    @Override
    public List<Food> getAllFoodsByMealId(UUID mealId) throws SQLException {
        String sql = """
                SELECT f.*, ml.* FROM MealLogFoods ml
                JOIN FoodName f ON ml.foodId = f.foodId
                WHERE ml.logId = ?
                """;
        List<Food> foods = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, mealId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                double quantity = rs.getDouble("quantity");
                double caloriesPer100g = rs.getDouble("caloriesPer100g");
                double calories = quantity * (caloriesPer100g / 100.0);

                Food food = new Food(rs.getInt("foodId"), rs.getString("foodDescription"),
                        quantity, calories);
                foods.add(food);
            }
        }

        return foods;
    }

    @Override
    public void removeMeal(UUID mealId) throws SQLException {
        String sql = "DELETE FROM MealLogs WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, mealId);
            ps.executeUpdate();
        }
    }

    @Override
    public Meal getMealById(UUID mealId) throws SQLException {
        String sql = "SELECT * FROM MealLogs WHERE id = ?";
        Meal meal = null;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, mealId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Meal.Builder builder = new Meal.Builder();
                builder.withDate(rs.getObject("createdAt", java.time.LocalDate.class)).withId(mealId)
                        .withType(rs.getString("type"));

                List<Food> foods = getAllFoodsByMealId(mealId);
                foods.forEach(builder::add);

                meal = builder.build();
            }
        }

        return meal;
    }

    @Override
    public List<Meal> getMealsByDateRange(UUID profileId, String startDate, String endDate) throws SQLException {
        String sql = """
                SELECT * FROM MealLogs
                WHERE profileId = ? AND createdAt BETWEEN ? AND ?
                """;
        List<Meal> meals = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, profileId);
            ps.setTimestamp(2, Timestamp.valueOf(startDate + " 00:00:00"));
            ps.setTimestamp(3, Timestamp.valueOf(endDate + " 23:59:59"));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID mealId = rs.getObject("id", UUID.class);
                Meal.Builder builder = new Meal.Builder();
                builder.withDate(rs.getObject("createdAt", java.time.LocalDate.class)).withId(mealId)
                        .withType(rs.getString("type"));

                List<Food> foods = getAllFoodsByMealId(mealId);
                foods.forEach(builder::add);

                meals.add(builder.build());
            }
        }
        return meals;
    }

    @Override
    public List<Meal> getMealsByDate(UUID profileId, String date) throws SQLException {
        String sql = """
                SELECT * FROM MealLogs
                WHERE profileId = ? AND createdAt = ?
                """;
        List<Meal> meals = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, profileId);
            ps.setDate(2, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID mealId = rs.getObject("id", UUID.class);
                Meal.Builder builder = new Meal.Builder();
                builder.withDate(rs.getObject("createdAt", java.time.LocalDate.class)).withId(mealId)
                        .withType(rs.getString("type"));

                List<Food> foods = getAllFoodsByMealId(mealId);
                foods.forEach(builder::add);

                meals.add(builder.build());
            }
        }
        return meals;
    }

    @Override
    public void updateMeal(UUID mealId, List<Food> foods) throws SQLException {
        // delete all existing food items for the meal
        String deleteSql = "DELETE FROM MealLogFoods WHERE logId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(deleteSql)) {
            ps.setObject(1, mealId);
            ps.executeUpdate();
        }

        // reinsert all provided food items into junction table MealLogFoods
        foods.forEach(food -> {
            String foodSql = """
                    INSERT INTO MealLogFoods
                      (logId, foodId)
                    VALUES (?, ?)
                    ON CONFLICT DO NOTHING
                    """;
            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement foodPs = conn.prepareStatement(foodSql)) {
                foodPs.setObject(1, mealId);
                foodPs.setInt(2, food.getFoodID());
                foodPs.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
