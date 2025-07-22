package org.Handlers.Database;

import org.Entity.Meal;
import org.Entity.DateRange;

import java.time.LocalDate;
import java.util.*;

public class IntakeLog {

    // store meals per user ID
    private static final Map<UUID, List<Meal>> userMeals = new HashMap<>();

    // save a meal for a specific user
    public void saveMeal(UUID userId, Meal meal) {
        userMeals.computeIfAbsent(userId, k -> new ArrayList<>()).add(meal);
    }

    // fetch meals by date for a specific user
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

    // get meals in a date range for a specific user
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

    // replace all meals for a specific user
    public void updateMeals(UUID userId, List<Meal> updatedMeals) {
        userMeals.put(userId, new ArrayList<>(updatedMeals));
    }

    //get all meals for a specific user
    public List<Meal> getAll(UUID userId) {
        return new ArrayList<>(userMeals.getOrDefault(userId, Collections.emptyList()));
    }

    // alias for save
    public void add(UUID userId, Meal meal) {
        saveMeal(userId, meal);
    }

    public void remove(UUID userId, Meal meal) {
        List<Meal> meals = userMeals.get(userId);
        if (meals != null) {
            meals.remove(meal);
        }
    }

}

