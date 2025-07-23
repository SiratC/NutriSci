package org.Dao;

import org.Entity.NutrientName;
import java.sql.SQLException;

/**
 * An interface defining the name of a nutrient of the food item.
 */
public interface NutrientNameDAO {

    /**
     * Sets the nutrient name.
     *
     * @param name name of nutrient
     * @throws SQLException if database connection fails
     */
    void insertNutrientName(NutrientName name) throws SQLException;

    /**
     * Finds the nutrient by the ID.
     *
     * @param nutrientNameId nutrient's ID
     * @return the name of nutrient
     * @throws SQLException if database connection fails
     */
    NutrientName findById(int nutrientNameId) throws SQLException;
}
