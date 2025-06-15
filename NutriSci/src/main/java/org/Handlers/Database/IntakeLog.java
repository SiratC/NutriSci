package org.Handlers.Database;

import org.Entity.Meal;
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
            if (m.getDate().equals(date)) result.add(m);
        }
        return result;
    }

  

}
