package org.Handlers.Database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import org.Dao.NutrientProfileDAO;
import org.Entity.FoodName;
import org.Enums.NutrientType;
import org.Handlers.Logic.NutrientTypeMapper;

public class DatabaseNutrientProfileDAO implements NutrientProfileDAO {
    private final DatabaseFoodNameDAO foodNameDAO = new DatabaseFoodNameDAO();
    private final NutrientTypeMapper nutrientTypeMapper = new NutrientTypeMapper();

    @Override
    public Map<NutrientType, Double> profile(int foodId, double quantity) throws SQLException {
        double scale = quantity / 100.0;
        String sql = """
                SELECT na.nutrientNameId, na.nutrientValue
                FROM NutrientAmount na
                WHERE na.foodId = ?
                """;

        Map<NutrientType, Double> result = new HashMap<>();

        try (Connection c = DatabaseConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, foodId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NutrientType nt = nutrientTypeMapper.mapIdToNutrientType(rs.getInt(1));
                    if (nt != null) {
                        result.put(nt, rs.getDouble(2) * scale);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public double calories(int foodId, double quantity) throws SQLException {
        FoodName fn = foodNameDAO.findById(foodId);
        return fn == null ? 0 : fn.getCaloriesPer100g() * (quantity / 100.0);
    }
}