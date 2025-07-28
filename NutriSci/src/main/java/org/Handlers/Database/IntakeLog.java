package org.Handlers.Database;

import org.Entity.DateRange;
import org.Entity.Meal;

import java.time.LocalDate;
import java.util.*;

public class IntakeLog {

    private final DatabaseMealLogDAO mealLogDAO = new DatabaseMealLogDAO();

    public void saveMeal(UUID userId, Meal meal) {
        try {
            mealLogDAO.insertMeal(userId, meal.getType(), meal.getItems(), meal.getDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public List<Meal> getAll(UUID userId) {
        try {
            return mealLogDAO.getAllMealsByProfileId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Meal> fetchMealsByDate(UUID userId, LocalDate date) {
        try {
            return mealLogDAO.getMealsByDate(userId, date.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Meal> getMealsBetween(UUID userId, DateRange range) {
        try {
            return mealLogDAO.getMealsByDateRange(userId, range.getStart().toString(), range.getEnd().toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Replace meals in DB: delete meals on same date, then insert swapped meals
    public void updateMeals(UUID userId, List<Meal> updatedMeals) {
        try {
            Set<LocalDate> affectedDates = new HashSet<>();
            for (Meal m : updatedMeals) {
                affectedDates.add(m.getDate());
            }

            for (LocalDate date : affectedDates) {
                List<Meal> oldMeals = mealLogDAO.getMealsByDate(userId, date.toString());
                for (Meal m : oldMeals) {
                    mealLogDAO.removeMeal(m.getId());
                }
            }

            for (Meal meal : updatedMeals) {
                mealLogDAO.insertMeal(userId, meal.getType(), meal.getItems(), meal.getDate());
            }

            System.out.println("[IntakeLog] Replaced meals on: " + affectedDates);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
