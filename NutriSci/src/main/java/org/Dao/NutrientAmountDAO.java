package org.Dao;

import org.Entity.NutrientAmount;
import java.sql.SQLException;
import java.util.List;

/**
 * An interface defining the nutrient amount of a food item.
 */
public interface NutrientAmountDAO {

    /**
     * Allows for manual insertion of nutrient amount specified.
     *
     * @param na nutrient amount
     * @throws SQLException if database connection fails
     */
    void insertNutrientAmount(NutrientAmount na) throws SQLException;

    /**
     * Returns a list of nutrient amounts depending on the food ID.
     *
     * @param foodId the food ID
     * @return list of nutrient amounts
     * @throws SQLException if database connection fails
     */
    List<NutrientAmount> findByFoodId(int foodId) throws SQLException;
}
