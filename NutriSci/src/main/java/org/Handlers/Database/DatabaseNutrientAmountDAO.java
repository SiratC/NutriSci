package org.Handlers.Database;

import org.Entity.NutrientAmount;
import org.Dao.NutrientAmountDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseNutrientAmountDAO implements NutrientAmountDAO {
    @Override
    public void insertNutrientAmount(NutrientAmount na) throws SQLException {
        String sql = """
            INSERT INTO NutrientAmount
              (foodId, nutrientNameId, nutrientValue)
            VALUES (?, ?, ?) ON CONFLICT DO NOTHING
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, na.getFoodId());
            ps.setInt(2, na.getNutrientNameId());
            ps.setBigDecimal(3, na.getNutrientValue());
            ps.executeUpdate();
        }
    }

    @Override
    public List<NutrientAmount> findByFoodId(int foodId) throws SQLException {
        String sql = "SELECT * FROM NutrientAmount WHERE foodId = ?";
        List<NutrientAmount> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, foodId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new NutrientAmount(
                            rs.getInt("foodId"),
                            rs.getInt("nutrientNameId"),
                            rs.getBigDecimal("nutrientValue")
                    ));
                }
            }
        }
        return list;
    }
}
