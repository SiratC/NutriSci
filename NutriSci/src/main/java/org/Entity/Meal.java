package org.Entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
     * Adds a food item to this meal.
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
        return Collections.unmodifiableList(items);
    }

    /**
     * Returns the total calories.
     * @return amount of calories
     */
    public double getCaloriesTotal() {
        return items.stream().mapToDouble(Food::getCalories).sum();
    }

    /**
     * Returns the number of items.
     * @return item count
     */
    public int getItemCount() {
        return items.size();
    }

    @Override
    public String toString() {
        return "Meal on " + date + " | Items: " + getItemCount() + " | Calories: " + getCaloriesTotal();
    }

    /**
     * Builder class to simplify creation of meals.
     */
    public static class Builder {
        private LocalDate date;
        private final List<Food> items = new ArrayList<>();

        /**
         * Specfies a Meal with an existing date.
         * @param date date specified
         * @return Builder
         */
        public Builder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        /**
         * Adds food to the built Meal.
         * @param food food item
         * @return Builder
         */
        public Builder add(Food food) {
            items.add(food);
            return this;
        }

        /**
         * Returns a built meal.
         * @return Meal instance
         */
        public Meal build() {
            Meal meal = new Meal(date);
            for (Food food : items) meal.addItem(food);
            return meal;
        }
    }
}

