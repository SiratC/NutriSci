package org.Dao;

import org.Entity.NutrientName;
import java.sql.SQLException;

public interface NutrientNameDAO {
    void insertNutrientName(NutrientName name) throws SQLException;
    NutrientName findById(int nutrientNameId) throws SQLException;
}
