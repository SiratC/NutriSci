package org.Dao;

import org.Entity.FoodName;
import java.sql.SQLException;
import java.util.List;

/**
 * An interface defining the saved data of a food item.
 */
public interface FoodNameDAO {

    /**
     * Allows for the changing of a food item's name using existing food item.
     *
     * @param food the food item
     * @throws SQLException if database connection fails
     */
    void insertFoodName(FoodName food) throws SQLException;

    /**
     * Uses food ID to find a food item's description.
     *
     * @param foodId the food id
     * @return description of food
     * @throws SQLException if database connection fails
     */
    String findDescriptionById(int foodId) throws SQLException;

    /**
     * Returns a list of all food items available.
     *
     * @return food item list
     * @throws SQLException if database connection fails
     */
    List<FoodName> getAllFoodNames() throws SQLException; // for debug

}