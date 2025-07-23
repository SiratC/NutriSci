package org.Handlers.Database;

import org.Entity.NutrientName;
import org.Dao.NutrientNameDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation database class for nutrient names.
 */
public class DatabaseNutrientNameDao implements NutrientNameDAO {
    @Override
    public void insertNutrientName(NutrientName nn) throws SQLException {
        String sql =
                "INSERT INTO NutrientName " +
                        "(nutrientNameId, nutrientName, unit)" +
                        "VALUES (?, ?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nn.getNutrientNameId());
            ps.setString(2, nn.getNutrientName());
            ps.setString(3, nn.getUnit());
            ps.executeUpdate();
        }
    }

    @Override
    public NutrientName findById(int nutrientNameId) throws SQLException {
        String sql = "SELECT * FROM NutrientName WHERE nutrientNameId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nutrientNameId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new NutrientName(
                            rs.getInt("nutrientNameId"),
                            rs.getString("nutrientName"),
                            rs.getString("unit")
                    );
                }
            }
        }
        return null;
    }
}
