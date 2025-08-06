package org.Entity;

import org.Enums.NutrientType;
import org.Enums.Sex;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class Profile {
    private UUID userID;
    private String name;
    private Sex sex;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String password;


    private HealthMetrics healthMetrics;
    private NutrientGoalManager goalManager;


    private LocalDate dob;
    private double height;
    private double weight;
    private String units;
    private Map<NutrientType, Double> nutrientGoals;

    public Profile(UUID userID, String name, String password, Sex sex, LocalDate dob, double height, double weight,
                   String units, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.sex = sex;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;

        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.units = units;

        this.healthMetrics = new HealthMetrics(height, weight, units, dob);
        this.goalManager = new NutrientGoalManager();
    }

    // Constructor using ProfileData (for parameter object)
    public Profile(ProfileData data) {
        this.userID = data.getUserID();
        this.name = data.getName();
        this.password = data.getPassword();
        this.sex = data.getSex();
        this.dob = data.getDob();
        this.height = data.getHeight();
        this.weight = data.getWeight();
        this.units = data.getUnits();
        this.createdAt = data.getCreatedAt();
        this.modifiedAt = data.getModifiedAt();
        this.nutrientGoals = data.getNutrientGoals();

        this.healthMetrics = new HealthMetrics(height, weight, units, dob);
        this.goalManager = new NutrientGoalManager();
        if (nutrientGoals != null) {
            this.goalManager.setGoals(nutrientGoals);
        }
    }

    // Basic info
    public UUID getUserID() { return userID; }
    public void setUserID(UUID userID) { this.userID = userID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Sex getSex() { return sex; }
    public void setSex(Sex sex) { this.sex = sex; }

    // Auth
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Metadata
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }

    // Health metrics
    public double getBMI() { return healthMetrics.calculateBMI(); }
    public double calculateBMI() { return healthMetrics.calculateBMI(); }
    public int getAge() { return healthMetrics.getAge(); }

    public HealthMetrics getHealthMetrics() { return healthMetrics; }
    public void setHealthMetrics(HealthMetrics metrics) { this.healthMetrics = metrics; }

    public LocalDate getDob() { return healthMetrics.getDob(); }
    public double getHeight() { return healthMetrics.getHeight(); }
    public double getWeight() { return healthMetrics.getWeight(); }
    public String getUnits() { return healthMetrics.getUnits(); }

    public void setDob(LocalDate dob) {
        this.dob = dob;
        this.healthMetrics.setDob(dob);
    }

    public void setHeight(double height) {
        this.height = height;
        this.healthMetrics.setHeight(height);
    }

    public void setWeight(double weight) {
        this.weight = weight;
        this.healthMetrics.setWeight(weight);
    }

//    public void setUnits(String units) {
//        this.units = units;
//        this.healthMetrics.setUnits(units);
//    }

    // Nutrient goals
    public void setNutrientGoal(NutrientType type, double value) {
        goalManager.setGoal(type, value);
        if (nutrientGoals != null) {
            nutrientGoals.put(type, value);
        }
    }

    public Map<NutrientType, Double> getNutrientGoals() {
        return goalManager.getGoals();
    }

    public void setNutrientGoals(Map<NutrientType, Double> goals) {
        this.nutrientGoals = goals;
        this.goalManager.setGoals(goals);
    }

    public NutrientGoalManager getGoalManager() {
        return goalManager;
    }

    public void setGoalManager(NutrientGoalManager goalManager) {
        this.goalManager = goalManager;
    }

  // fallback BMI
    public double getBMI_Legacy() {
        boolean isImperial = units != null && units.equalsIgnoreCase("imperial");
        double heightInMeters = isImperial ? height * 0.0254 : height / 100.0;
        double weightInKg = isImperial ? weight * 0.453592 : weight;
        return weightInKg / (heightInMeters * heightInMeters);
    }

    @Override
    public String toString() {
        return name + " (" + sex + "), Age: " + getAge() + ", BMI: " + String.format("%.1f", getBMI());
    }
}
