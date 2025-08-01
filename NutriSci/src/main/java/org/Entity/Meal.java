package org.Entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Meal {
    private final UUID id;
    private final String type;
    private final LocalDate date;
    private final List<Food> items = new ArrayList<>();

    public Meal(UUID id, LocalDate date, String type) {
        this.id = id;
        this.date = date;
        this.type = type;
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

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Meal on " + date + " | Items: " + getItemCount() + " | Calories: " + getCaloriesTotal();
    }

    public static class Builder {
        private UUID id;
        private LocalDate date;
        private String type;
        private final List<Food> items = new ArrayList<>();

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder add(Food food) {
            items.add(food);
            return this;
        }

        public Meal build() {
            Meal meal = new Meal(id, date, type);
            for (Food food : items)
                meal.addItem(food);
            return meal;
        }
    }
}
