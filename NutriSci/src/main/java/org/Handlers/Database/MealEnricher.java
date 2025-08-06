package org.Handlers.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.Dao.NutrientProfileDAO;
import org.Entity.Food;
import org.Entity.Meal;
import org.Enums.NutrientType;
import org.Handlers.Logic.NutrientTypeMapper;

public class MealEnricher {

    private final NutrientProfileDAO nutrientProfileDAO = new DatabaseNutrientProfileDAO();
    private final NutrientTypeMapper nutrientTypeMapper = new NutrientTypeMapper();

    public Meal enrich(Meal meal) {
        return meal.getId() == null
                ? enrichTransient(meal)
                : enrichPersisted(meal);
    }

    public List<Meal> enrich(List<Meal> meals) {
        return meals.stream()
                .map(this::enrich)
                .toList();
    }

    private Meal enrichTransient(Meal meal) {
        Meal.Builder b = copySkeleton(meal);
        for (Food f : meal.getItems()) {
            b.add(enrichFoodIfNeeded(f));
        }
        return b.build();
    }

    private Meal enrichPersisted(Meal meal) {
        String sql = """
                SELECT mlf.foodId, mlf.quantity, f.foodDescription,
                       na.nutrientNameId, na.nutrientValue
                FROM MealLogFoods mlf
                JOIN FoodName f ON mlf.foodId = f.foodId
                JOIN NutrientAmount na ON f.foodId = na.foodId
                WHERE mlf.logId = ?
                ORDER BY mlf.foodId, na.nutrientNameId
                """;

        Map<Integer, Map<NutrientType, Double>> byFood = new HashMap<>();
        Map<Integer, Food> foods = new HashMap<>();

        try (Connection c = DatabaseConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, meal.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int foodId = rs.getInt(1);
                    double qty = rs.getDouble(2);
                    String name = rs.getString(3);
                    NutrientType nt = nutrientTypeMapper.mapIdToNutrientType(rs.getInt(4));
                    double per100 = rs.getDouble(5);

                    foods.computeIfAbsent(foodId,
                            id -> new Food(id, name, qty, 0.0));
                    byFood.computeIfAbsent(foodId, __ -> new HashMap<>())
                            .put(nt, per100 * (qty / 100.0));
                }
            }
        } catch (SQLException e) {
            System.err.println("load nutrients failed: " + e.getMessage());
            return meal;
        }

        Meal.Builder b = copySkeleton(meal);
        foods.values().forEach(f -> {
            f.setNutrients(byFood.get(f.getFoodID()));
            try {
                f.setCalories(nutrientProfileDAO.calories(f.getFoodID(), f.getQuantity()));
            } catch (SQLException ignored) {
            }
            b.add(f);
        });
        return b.build();
    }

    private Meal.Builder copySkeleton(Meal m) {
        return new Meal.Builder()
                .withDate(m.getDate())
                .withId(m.getId())
                .withType(m.getType());
    }

    private Food enrichFoodIfNeeded(Food f) {
        if (!f.getNutrients().isEmpty())
            return f;

        try {
            Map<NutrientType, Double> n = nutrientProfileDAO.profile(f.getFoodID(), f.getQuantity());
            double cals = nutrientProfileDAO.calories(f.getFoodID(), f.getQuantity());
            Food enriched = new Food(f.getFoodID(), f.getName(), f.getQuantity(), cals);
            enriched.setNutrients(n);
            enriched.setCalories(cals);
            return enriched;
        } catch (SQLException e) {
            System.err.println("nutrient load failed for " + f.getName() + ": " + e.getMessage());
            return f;
        }
    }
}
