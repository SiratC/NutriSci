package org.Entity;

import org.Enums.Sex;
import org.Enums.NutrientType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ProfileData {
    private UUID userID;
    private String name;
    private String password;
    private Sex sex;
    private LocalDate dob;
    private double height;
    private double weight;
    private String units;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Map<NutrientType, Double> nutrientGoals;


    public ProfileData() {}

    public ProfileData(UUID userID, String name, String password, Sex sex, LocalDate dob,
                       double height, double weight, String units,
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


    public UUID getUserID() { return userID; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public Sex getSex() { return sex; }
    public LocalDate getDob() { return dob; }
    public double getHeight() { return height; }
    public double getWeight() { return weight; }
    public String getUnits() { return units; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public Map<NutrientType, Double> getNutrientGoals() { return nutrientGoals; }


    public void setUserID(UUID userID) { this.userID = userID; }
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setSex(Sex sex) { this.sex = sex; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public void setHeight(double height) { this.height = height; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setUnits(String units) { this.units = units; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }
    public void setNutrientGoals(Map<NutrientType, Double> nutrientGoals) {
        this.nutrientGoals = nutrientGoals;
    }
}
