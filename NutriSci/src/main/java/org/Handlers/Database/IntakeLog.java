package org.Handlers.Database;

import org.Entity.Meal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import org.Entity.Meal;
import org.Entity.DateRange;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** In-memory storage for meals */
public class IntakeLog {
    private static List<Meal> meals = new ArrayList<>();

    /**
     * Save a meal to the in-memory list
     *
     * @param meal the meal saved in storage
     */
    public void saveMeal(Meal meal) {
        meals.add(meal);
    }
    /**
     * Returns meals based on a given date.
     * Original method (optional)
     * @param date the date of the meal
     * @return meals on date
     */
    public static List<Meal> fetchMealsByDate(LocalDate date) {
        List<Meal> result = new ArrayList<>();
        for (Meal m : meals) {
            if (m.getDate().equals(date)) {
                result.add(m);
            }
        }
        return result;
    }

    /**
     * ✅ NEW: Used by Swing UI to fetch meals between two dates
     *
     * @param range the time range to find meals
     * @return the list of meals
     */
    public List<Meal> getMealsBetween(DateRange range) {
        List<Meal> result = new ArrayList<>();
        for (Meal m : meals) {
            if (!m.getDate().isBefore(range.getStart()) && !m.getDate().isAfter(range.getEnd())) {
                result.add(m);
            }
        }
        return result;
    }

    /**
     * ✅ NEW: Used by Swing UI after swaps
     *
     * @param updatedMeals the updated meals given
     */
    public void updateMeals(List<Meal> updatedMeals) {
        meals = new ArrayList<>(updatedMeals);
    }
}
