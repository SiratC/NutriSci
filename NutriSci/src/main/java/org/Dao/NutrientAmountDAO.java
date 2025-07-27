package org.Dao;

import org.Entity.NutrientAmount;
import java.sql.SQLException;
import java.util.List;

public interface NutrientAmountDAO {
    void insertNutrientAmount(NutrientAmount na) throws SQLException;
    List<NutrientAmount> findByFoodId(int foodId) throws SQLException;
}
