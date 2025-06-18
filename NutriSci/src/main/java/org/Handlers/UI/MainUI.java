package org.Handlers.UI;

import org.Entity.Exercise;
import org.Entity.Food;
import org.Entity.Meal;
import org.Entity.Profile;
import org.Enums.Sex;
import org.Handlers.Controller.ProfileManager;
import org.Handlers.Database.ExerciseLog;
import org.Handlers.Database.IntakeLog;
import org.Handlers.Logic.NutrientAnalyzer;
import org.Handlers.Visual.Visualizer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Demonstrates Deliverable 1 features */
public class MainUI {
    public static void main(String[] args) {
            // 1) Profile creation
            ProfileManager pm = new ProfileManager();
            UUID userId = UUID.randomUUID();
            Profile profile = new Profile(userId, Sex.Female, LocalDate.of(1990, 1, 1), 165, 60, "metric");
            pm.saveProfile(profile);
            System.out.println("Saved profile: " + profile.getUserID());

            // 2) Meal logging
            IntakeLog intakeLog = new IntakeLog();
            Meal breakfast = new Meal(LocalDate.now());
            breakfast.addItem(new Food("Milk", 1));
            breakfast.addItem(new Food("Bread", 2));
            intakeLog.saveMeal(breakfast);
            System.out.println("Logged meal items: " + breakfast.getItems().size());

        // Exercise logging
        ExerciseLog exerciseLog = new ExerciseLog();
        Exercise run = new Exercise(LocalDate.now(), "Running", Duration.ofMinutes(30));
        exerciseLog.saveSession(run);
        System.out.println("Logged exercise: " + run.getType());

        // Visualization
        NutrientAnalyzer analyzer = new NutrientAnalyzer();
        int items = analyzer.analyze(intakeLog.fetchMealsByDate(LocalDate.now())).getTotalItems();
        int exercises = exerciseLog.fetchSessionsByDate(LocalDate.now()).size();
        Map<String, Integer> chartData = new HashMap<>();
        chartData.put("Items Eaten", items);
        chartData.put("Exercises", exercises);
    }
}