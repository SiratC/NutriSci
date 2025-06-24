package org.Handlers.Database;

import org.Entity.Exercise;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles storage of information on exercises.
 */
public class ExerciseLog {
    private List<Exercise> sessions = new ArrayList<>();

    /**
     * Adds a session to the exercise log.
     *
     * @param session the exercise to be added
     */
    public void saveSession(Exercise session) {
        sessions.add(session);
    }

    /**
     * Returns exercises based on a given date.
     * @param date the date of the exercise
     * @return exercises on date
     */
    public List<Exercise> fetchSessionsByDate(LocalDate date) {
        List<Exercise> result = new ArrayList<>();
        for (Exercise s : sessions) {
            if (s.getDate().equals(date)) result.add(s);
        }
        return result;
    }
}