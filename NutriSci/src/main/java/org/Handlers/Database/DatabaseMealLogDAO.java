package org.Handlers.Database;

import org.Dao.MealLogDAO;
import org.Entity.Food;
import org.Entity.Meal;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseMealLogDAO implements MealLogDAO {

    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/nutrisci";
        String user = "user";
        String password = "password";
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public UUID insertMeal(UUID profileId, String type, List<Food> foods) throws SQLException {
        return insertMeal(profileId, type, foods, LocalDate.now());
    }

    public UUID insertMeal(UUID profileId, String type, List<Food> foods, LocalDate date) throws SQLException {
        String sql = """
                INSERT INTO MealLogs (id, profileId, type, createdAt)
                VALUES (?, ?, ?, ?)
                """;

        UUID mealLogId = UUID.randomUUID();

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, mealLogId);
            ps.setObject(2, profileId);
            ps.setString(3, type);
            ps.setDate(4, Date.valueOf(date));
            ps.executeUpdate();
        }

        for (Food food : foods) {
            String foodSql = """
                    INSERT INTO MealLogFoods (logId, foodId, quantity)
                    VALUES (?, ?, ?)
                    ON CONFLICT DO NOTHING
                    """;
            try (Connection conn = getConnection();
                    PreparedStatement foodPs = conn.prepareStatement(foodSql)) {
                foodPs.setObject(1, mealLogId);
                foodPs.setInt(2, food.getFoodID());
                foodPs.setDouble(3, food.getQuantity());
                foodPs.executeUpdate();
            }
        }

        return mealLogId;
    }

    @Override
    public List<Meal> getAllMealsByProfileId(UUID profileId) throws SQLException {
        String sql = "SELECT * FROM MealLogs WHERE profileId = ?";
        List<Meal> meals = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, profileId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID mealId = rs.getObject("id", UUID.class);
                Meal.Builder builder = new Meal.Builder()
                        .withDate(rs.getObject("createdAt", LocalDate.class))
                        .withId(mealId)
                        .withType(rs.getString("type"));

                getAllFoodsByMealId(mealId).forEach(builder::add);
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

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, mealId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                double quantity = rs.getDouble("quantity");
                double caloriesPer100g = rs.getDouble("caloriesPer100g");
                double calories = quantity * (caloriesPer100g / 100.0);

                foods.add(new Food(
                        rs.getInt("foodId"),
                        rs.getString("foodDescription"),
                        quantity,
                        calories));
            }
        }

        return foods;
    }

    @Override
    public List<Food> getAllOriginalFoodsByMealId(UUID mealId) throws SQLException {
        String sql = """
                SELECT f.*, ml.* FROM MealLogFoodsBeforeSwap ml
                JOIN FoodName f ON ml.foodId = f.foodId
                WHERE ml.logId = ?
                """;
        List<Food> foods = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, mealId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                double quantity = rs.getDouble("quantity");
                double caloriesPer100g = rs.getDouble("caloriesPer100g");
                double calories = quantity * (caloriesPer100g / 100.0);

                foods.add(new Food(
                        rs.getInt("foodId"),
                        rs.getString("foodDescription"),
                        quantity,
                        calories));
            }
        }

        return foods;
    }

    @Override
    public void removeMeal(UUID mealId) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM MealLogFoods WHERE logId = ?")) {
            ps.setObject(1, mealId);
            ps.executeUpdate();
        }

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM MealLogs WHERE id = ?")) {
            ps.setObject(1, mealId);
            ps.executeUpdate();
        }
    }

    @Override
    public Meal getMealById(UUID mealId) throws SQLException {
        String sql = "SELECT * FROM MealLogs WHERE id = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, mealId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Meal.Builder builder = new Meal.Builder()
                        .withDate(rs.getObject("createdAt", LocalDate.class))
                        .withId(mealId)
                        .withType(rs.getString("type"));

                getAllFoodsByMealId(mealId).forEach(builder::add);
                return builder.build();
            }
        }

        return null;
    }

    @Override
    public List<Meal> getMealsByDateRange(UUID profileId, String startDate, String endDate) throws SQLException {
        String sql = """
                SELECT * FROM MealLogs
                WHERE profileId = ? AND createdAt BETWEEN ? AND ?
                """;
        List<Meal> meals = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, profileId);
            ps.setTimestamp(2, Timestamp.valueOf(startDate + " 00:00:00"));
            ps.setTimestamp(3, Timestamp.valueOf(endDate + " 23:59:59"));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID mealId = rs.getObject("id", UUID.class);
                Meal.Builder builder = new Meal.Builder()
                        .withDate(rs.getObject("createdAt", LocalDate.class))
                        .withId(mealId)
                        .withType(rs.getString("type"));

                getAllFoodsByMealId(mealId).forEach(builder::add);
                meals.add(builder.build());
            }
        }
        return meals;
    }

    @Override
    public List<Meal> getOriginalMealsByDateRange(UUID profileId, String startDate, String endDate)
            throws SQLException {
        String sql = """
                SELECT * FROM MealLogs
                WHERE profileId = ? AND createdAt BETWEEN ? AND ?
                """;
        List<Meal> meals = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, profileId);
            ps.setTimestamp(2, Timestamp.valueOf(startDate + " 00:00:00"));
            ps.setTimestamp(3, Timestamp.valueOf(endDate + " 23:59:59"));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID mealId = rs.getObject("id", UUID.class);
                Meal.Builder builder = new Meal.Builder()
                        .withDate(rs.getObject("createdAt", LocalDate.class))
                        .withId(mealId)
                        .withType(rs.getString("type"));

                getAllOriginalFoodsByMealId(mealId).forEach(builder::add);
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

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, profileId);
            ps.setDate(2, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID mealId = rs.getObject("id", UUID.class);
                Meal.Builder builder = new Meal.Builder()
                        .withDate(rs.getObject("createdAt", LocalDate.class))
                        .withId(mealId)
                        .withType(rs.getString("type"));

                getAllFoodsByMealId(mealId).forEach(builder::add);
                meals.add(builder.build());
            }
        }
        return meals;
    }

    @Override
    public void updateMeal(UUID mealId, List<Food> foods) throws SQLException {
        // store current foods in backup table before making changes
        String clearBackupSql = "DELETE FROM MealLogFoodsBeforeSwap WHERE logId = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(clearBackupSql)) {
            ps.setObject(1, mealId);
            ps.executeUpdate();
        }

        String backupSql = """
                INSERT INTO MealLogFoodsBeforeSwap (logId, foodId, quantity)
                SELECT logId, foodId, quantity
                FROM MealLogFoods
                WHERE logId = ?
                """;
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(backupSql)) {
            ps.setObject(1, mealId);
            ps.executeUpdate();
        }

        // then update the current foods
        String deleteSql = "DELETE FROM MealLogFoods WHERE logId = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(deleteSql)) {
            ps.setObject(1, mealId);
            ps.executeUpdate();
        }

        for (Food food : foods) {
            String foodSql = """
                    INSERT INTO MealLogFoods (logId, foodId, quantity)
                    VALUES (?, ?, ?)
                    ON CONFLICT DO NOTHING
                    """;
            try (Connection conn = getConnection();
                    PreparedStatement foodPs = conn.prepareStatement(foodSql)) {
                foodPs.setObject(1, mealId);
                foodPs.setInt(2, food.getFoodID());
                foodPs.setDouble(3, food.getQuantity());
                foodPs.executeUpdate();
            }
        }
    }
}
