package org.Entity;

import org.Enums.Sex;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Profile {
    private UUID userID;
    private String name;
    private Sex sex;
    private LocalDate dob;
    private double height;
    private double weight;
    private String units;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Profile(UUID userID, String name, Sex sex, LocalDate dob, double height, double weight, String units, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.userID = userID;
        this.name = name;
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
}
