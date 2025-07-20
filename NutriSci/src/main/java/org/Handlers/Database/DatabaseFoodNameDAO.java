package org.Handlers.Database;

import org.Entity.FoodName;
import org.Dao.FoodNameDAO;
import java.sql.*;

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
}
