package org.Entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Defines a meal based on a list of Food items inputted by the user.
 */
public class Meal {
    private final UUID id = UUID.randomUUID();
    private final LocalDate date;
    private final List<Food> items = new ArrayList<>();

    /**
     * Creates a meal list with an existing date.
     *
     * @param date the date of the meal
     */
    public Meal(LocalDate date) {
        this.date = date;
    }

    /**
     * Add a food to this meal.
     *
     * @param food the food item added to the list
     */
    public void addItem(Food food) {
        items.add(food);
    }

    /**
     * Returns the ID of a meal.
     *
     * @return id of the meal
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the time of the meal.
     *
     * @return date of meal
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the meal as a list.
     *
     * @return meal items
     */
    public List<Food> getItems() {
        return items;
    }
}