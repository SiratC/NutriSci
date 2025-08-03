package org.Handlers.Database;

import org.Entity.FoodName;
import org.Dao.FoodNameDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFoodNameDAO implements FoodNameDAO {
    @Override
    public void insertFoodName(FoodName food) throws SQLException {
        String sql = "INSERT INTO FoodName (foodId, foodDescription) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, food.getFoodId());
            ps.setString(2, food.getFoodDescription());
            ps.executeUpdate();
        }
    }

    @Override
    public FoodName findById(int foodId) throws SQLException {
        String sql = "SELECT * FROM FoodName WHERE foodId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, foodId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new FoodName(
                            rs.getInt("foodId"),
                            rs.getString("foodDescription"),
                            rs.getInt("caloriesPer100g"));
                } else {
                    return null; // no food found with the given ID
                }
            }
        }
    }

    @Override
    public String findDescriptionById(int foodId) throws SQLException {
        String sql = "SELECT foodDescription FROM FoodName WHERE foodId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, foodId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("foodDescription") : null;
            }
        }
    }

    @Override
    public List<FoodName> getAllFoodNames() {
        List<FoodName> result = new ArrayList<>();
        String sql = "SELECT * FROM foodname";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("foodId");
                String desc = rs.getString("foodDescription");
                int caloriesPer100g = rs.getInt("caloriesPer100g");
                result.add(new FoodName(id, desc, caloriesPer100g));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
