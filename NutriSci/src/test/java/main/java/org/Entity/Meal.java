package org.Entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Meal {
    private final UUID id = UUID.randomUUID();
    private final LocalDate date;
    private final List<Food> items = new ArrayList<>();

    public Meal(LocalDate date) {
        this.date = date;
    }

    public void addItem(Food food) {
        items.add(food);
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Food> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getCaloriesTotal() {
        return items.stream().mapToDouble(Food::getCalories).sum();
    }

    public int getItemCount() {
        return items.size();
    }

    @Override
    public String toString() {
        return "Meal on " + date + " | Items: " + getItemCount() + " | Calories: " + getCaloriesTotal();
    }

    public static class Builder {
        private LocalDate date;
        private final List<Food> items = new ArrayList<>();

        public Builder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder add(Food food) {
            items.add(food);
            return this;
        }

        public Meal build() {
            Meal meal = new Meal(date);
            for (Food food : items) meal.addItem(food);
            return meal;
        }
    }
}

