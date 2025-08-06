package org.Dao;

import org.Enums.NutrientType;

import java.sql.SQLException;
import java.util.Map;

public interface NutrientProfileDAO {
    Map<NutrientType, Double> profile(int foodId, double quantity) throws SQLException;

    double calories(int foodId, double quantity) throws SQLException;
}
