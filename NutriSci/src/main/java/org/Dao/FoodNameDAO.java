package org.Dao;

import org.Entity.FoodName;
import java.sql.SQLException;
import java.util.List;

public interface FoodNameDAO {
    void insertFoodName(FoodName food) throws SQLException;
    String findDescriptionById(int foodId) throws SQLException;
    List<FoodName> getAllFoodNames() throws SQLException; // for debug

}