package org.Handlers.Database;

import org.Entity.Meal;
import org.Entity.DateRange;

import java.time.LocalDate;
import java.util.*;

/** In-memory storage for meals */
public class IntakeLog {

    // store meals per user ID
    private static final Map<UUID, List<Meal>> userMeals = new HashMap<>();

    /**
     * Save a meal for a specific user.
     *
     * @param meal the meal saved in storage
     * @param userId user's ID
     */
    public void saveMeal(UUID userId, Meal meal) {
        userMeals.computeIfAbsent(userId, k -> new ArrayList<>()).add(meal);
    }

    /**
     * Fetch meals by date for a specific user.
     * @param date the date of the meal
     * @param userId user's ID
     * @return meals on date
     */
    public List<Meal> fetchMealsByDate(UUID userId, LocalDate date) {
        List<Meal> result = new ArrayList<>();
        List<Meal> meals = userMeals.getOrDefault(userId, Collections.emptyList());

        for (Meal m : meals) {
            if (m.getDate().equals(date)) {
                result.add(m);
            }
        }
        return result;
    }

    /**
     * Get meals in a date range for a specific user.
     *
     * @param range the time range to find meals
     * @param userId user's ID
     * @return the list of meals
     */
    public List<Meal> getMealsBetween(UUID userId, DateRange range) {
        List<Meal> result = new ArrayList<>();
        List<Meal> meals = userMeals.getOrDefault(userId, Collections.emptyList());

        for (Meal m : meals) {
            if (!m.getDate().isBefore(range.getStart()) && !m.getDate().isAfter(range.getEnd())) {
                result.add(m);
            }
        }

        return result;
    }

    /**
     * Replace all meals for a specific user.
     *
     * @param updatedMeals the updated meals given
     * @param userId user's ID
     */
    public void updateMeals(UUID userId, List<Meal> updatedMeals) {
        userMeals.put(userId, new ArrayList<>(updatedMeals));
    }


    /**
     * Get all meals for a specific user.
     *
     * @param userId the user's ID
     * @return list of meals
     */
    public List<Meal> getAll(UUID userId) {
        return new ArrayList<>(userMeals.getOrDefault(userId, Collections.emptyList()));
    }

    /**
     * Alias for save.
     * @param userId the user's ID
     * @param meal the meal given
     */
    public void add(UUID userId, Meal meal) {
        saveMeal(userId, meal);
    }

    /**
     * Removal method.
     * @param userId user's ID
     * @param meal meal to remove
     */
    public void remove(UUID userId, Meal meal) {
        List<Meal> meals = userMeals.get(userId);
        if (meals != null) {
            meals.remove(meal);
        }
    }

}

