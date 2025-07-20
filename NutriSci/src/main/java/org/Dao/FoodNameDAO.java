package org.Dao;

import org.Entity.FoodName;
import java.sql.SQLException;

public interface FoodNameDAO {
    void insertFoodName(FoodName food) throws SQLException;
    String findDescriptionById(int foodId) throws SQLException;
}