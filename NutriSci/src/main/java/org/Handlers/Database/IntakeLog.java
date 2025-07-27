package org.Handlers.Database;

import org.Entity.Meal;
import org.Entity.DateRange;

import java.time.LocalDate;
import java.util.*;

public class IntakeLog {

    private final DatabaseMealLogDAO mealLogDAO = new DatabaseMealLogDAO();

    // save a meal for a specific user
    public void saveMeal(UUID userId, Meal meal) {
        try {
            mealLogDAO.insertMeal(userId, meal.getItems());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // fetch meals by date for a specific user
    public List<Meal> fetchMealsByDate(UUID userId, LocalDate date) {
        try {
            return mealLogDAO.getMealsByDate(userId, date.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // get meals in a date range for a specific user
    public List<Meal> getMealsBetween(UUID userId, DateRange range) {
        try {
            return mealLogDAO.getMealsByDateRange(userId, range.getStart().toString(), range.getEnd().toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // replace all meals for a specific user
    public void updateMeals(UUID profileId, List<Meal> updatedMeals) {
        updatedMeals.forEach(meal -> {
            try {
                mealLogDAO.updateMeal(meal.getId(), meal.getItems());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // get all meals for a specific user
    public List<Meal> getAll(UUID userId) {
        try {
            return mealLogDAO.getAllMealsByProfileId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // alias for save
    public void add(UUID userId, Meal meal) {
        saveMeal(userId, meal);
    }

    public void remove(UUID userId, Meal meal) {
        try {
            mealLogDAO.removeMeal(meal.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
