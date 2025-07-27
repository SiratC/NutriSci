package org.Entity;

import org.Enums.NutrientType;
import org.Enums.Sex;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Profile {
    private UUID userID;
    private String name;
    private Sex sex;
    private LocalDate dob;
    private double height; // cm or inches
    private double weight; // kg or pounds
    private String units; // "metric" or "imperial"
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String password;
    private Map<NutrientType, Double> nutrientGoals = new HashMap<>();

    public Profile(UUID userID, String name, String password, Sex sex, LocalDate dob, double height, double weight,
            String units,
            LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.sex = sex;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.units = units;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public int getAge() {
        return Period.between(dob, LocalDate.now()).getYears();
    }

    public double getBMI() {
        boolean isImperial = units != null && units.equalsIgnoreCase("imperial");
        double heightInMeters = isImperial ? height * 0.0254 : height / 100.0;
        double weightInKg = isImperial ? weight * 0.453592 : weight;
        return weightInKg / (heightInMeters * heightInMeters);
    }

    // user login
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // for progress status
    public void setNutrientGoal(NutrientType type, double value) {
        nutrientGoals.put(type, value);
    }

    public Map<NutrientType, Double> getNutrientGoals() {
        return nutrientGoals;
    }

    @Override
    public String toString() {
        return name + " (" + sex + "), Age: " + getAge() + ", BMI: " + String.format("%.1f", getBMI());
    }

}
