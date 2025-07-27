package org.Handlers.Database;

import org.Entity.Exercise;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExerciseLog {
    private List<Exercise> sessions = new ArrayList<>();

    public void saveSession(Exercise session) {
        sessions.add(session);
    }
    public List<Exercise> fetchSessionsByDate(LocalDate date) {
        List<Exercise> result = new ArrayList<>();
        for (Exercise s : sessions) {
            if (s.getDate().equals(date)) result.add(s);
        }
        return result;
    }
}