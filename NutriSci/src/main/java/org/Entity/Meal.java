package org.Entity;

import java.time.LocalDate;
import java.util.ArrayList;
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
        return items;
    }
}
