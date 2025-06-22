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

public class IntakeLog {
    private static List<Meal> meals = new ArrayList<>();


    public void saveMeal(Meal meal) { 
        meals.add(meal);
    }


    public static List<Meal> fetchMealsByDate(LocalDate date) {
        List<Meal> result = new ArrayList<>();
        for (Meal m : meals) {
            if (m.getDate().equals(date)) {
                result.add(m);
            }
        }
        return result;
    }

    public List<Meal> getMealsBetween(DateRange range) {
        List<Meal> result = new ArrayList<>();
        for (Meal m : meals) {
            if (!m.getDate().isBefore(range.getStart()) && !m.getDate().isAfter(range.getEnd())) {
                result.add(m);
            }
        }
        return result;
    }


    public void updateMeals(List<Meal> updatedMeals) {
        meals = new ArrayList<>(updatedMeals);
    }
}
