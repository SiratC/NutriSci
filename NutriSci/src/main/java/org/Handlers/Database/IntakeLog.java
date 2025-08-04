package org.Handlers.Database;

import org.Entity.DateRange;
import org.Entity.Meal;
import org.Handlers.Logic.SwapEngine;

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

    public List<Meal> getOriginalMealsBetween(UUID userId, DateRange range) {
        try {
            return mealLogDAO.getOriginalMealsByDateRange(userId, range.getStart().toString(),
                    range.getEnd().toString());
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

    // New method for swap-aware meal updates
    public void updateMealsFromSwap(UUID userId, SwapEngine.SwapResult swapResult) {
        try {
            List<Meal> updatedMeals = swapResult.getMeals();
            boolean swapsWereApplied = swapResult.swapsWereApplied();
            
            Set<LocalDate> affectedDates = new HashSet<>();
            for (Meal m : updatedMeals) {
                affectedDates.add(m.getDate());
            }

            for (Meal meal : updatedMeals) {
                mealLogDAO.updateMealWithSwapTracking(meal.getId(), meal.getItems(), swapsWereApplied);
            }

            if (swapsWereApplied) {
                System.out.println("[IntakeLog] Applied " + swapResult.getSwapCount() + " swaps to meals on: " + affectedDates);
            } else {
                System.out.println("[IntakeLog] No swaps applied - meals unchanged on: " + affectedDates);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Legacy method - replace meals in DB, store original food items
    public void updateMeals(UUID userId, List<Meal> updatedMeals) {
        try {
            Set<LocalDate> affectedDates = new HashSet<>();
            for (Meal m : updatedMeals) {
                affectedDates.add(m.getDate());
            }

            for (Meal meal : updatedMeals) {
                mealLogDAO.updateMeal(meal.getId(), meal.getItems());
            }

            System.out.println("[IntakeLog] Replaced meals on: " + affectedDates);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
