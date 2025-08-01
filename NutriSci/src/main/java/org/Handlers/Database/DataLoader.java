package org.Handlers.Database;

import com.opencsv.CSVReader;
import org.Entity.FoodName;
import org.Entity.NutrientName;
import org.Entity.NutrientAmount;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.Dao.FoodNameDAO;
import org.Dao.NutrientAmountDAO;
import org.Dao.NutrientNameDAO;

public class DataLoader {
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void loadAll() throws Exception {
        loadFoodNames("food_filtered.csv");
        loadNutrientNames("nutrient_name_filtered.csv");
        loadNutrientAmounts("nutrient_amount_filtered.csv");
    }

    private void loadFoodNames(String resource) throws Exception {
        FoodNameDAO foodDao = new DatabaseFoodNameDAO();

        // debug
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        if (stream == null) {
            throw new RuntimeException("CSV file not found: " + resource);
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(resource)))) {
            reader.readNext(); // skip header
            String[] line;
            while ((line = reader.readNext()) != null) {
                int id = Integer.parseInt(line[0]);
                String desc = line[1];
                int caloriesPer100g = Integer.parseInt(line[2]);

                System.out.println("Inserting food: " + id + " - " + desc); // debug
                foodDao.insertFoodName(new FoodName(id, desc, caloriesPer100g));
            }
        }

    }

    private void loadNutrientNames(String resource) throws Exception {
        NutrientNameDAO nnDao = new DatabaseNutrientNameDao();

        // debug
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);

        if (stream == null) {
            throw new RuntimeException("CSV file not found: " + resource);
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(resource)))) {
            reader.readNext(); // skip header
            String[] line;
            while ((line = reader.readNext()) != null) {
                int id = Integer.parseInt(line[0]);
                String name = line[1];
                String unit = line[2];
                nnDao.insertNutrientName(
                        new NutrientName(id, name, unit));
            }
        }
    }

    private void loadNutrientAmounts(String resource) throws Exception {
        NutrientAmountDAO nutrDao = new DatabaseNutrientAmountDAO();

        // debug
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);

        if (stream == null) {

            throw new RuntimeException("CSV file not found: " + resource);
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(resource)))) {
            reader.readNext(); // skip header
            String[] line;
            while ((line = reader.readNext()) != null) {
                int foodId = Integer.parseInt(line[0]);
                int nutrientNameId = Integer.parseInt(line[1]);
                BigDecimal value = new BigDecimal(line[2]);

                nutrDao.insertNutrientAmount(new NutrientAmount(
                        foodId, nutrientNameId, value));
            }
        }
    }
}
