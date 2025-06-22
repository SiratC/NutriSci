package org.Entity;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Handles the exercises chosen by the user.
 * <p>It holds the type of exercise, length, and time period. </p>
 */
public class Exercise {
    private UUID id = UUID.randomUUID();
    private LocalDate date;
    private String type;
    private Duration duration;

    /**
     * Defines an exercise based on given day, type and time allocated.
     *
     * @param date the day the exercise took place.
     * @param type the type of exercise done by the user.
     * @param duration the amount of time the exercise took to complete.
     */
    public Exercise(LocalDate date, String type, Duration duration) {
        this.date = date;
        this.type = type;
        this.duration = duration;
    }

    /**
     * Returns the date of exercise.
     *
     * @return date of exercise
     */
    public LocalDate getDate(){
        return this.date;
    }

    /**
     * Returns the type of exercise.
     *
     * @return type of exercise
     */
    public String getType(){
        return this.type;
    }

    /**
     * Returns the length of time for the exercise.
     *
     * @return time committed to the exercise
     */
    public Duration getDuration(){
        return this.duration;
    }
}