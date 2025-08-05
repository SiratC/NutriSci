package org.Entity;

import java.time.LocalDate;
import java.time.Period;

public class HealthMetrics {
    private double height;
    private double weight;
    private String units;
    private LocalDate dob;

    public HealthMetrics(double height, double weight, String units, LocalDate dob) {
        this.height = height;
        this.weight = weight;
        this.units = units;
        this.dob = dob;
    }


    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    // Existing methods
    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public String getUnits() {
        return units;
    }

    public LocalDate getDob() {
        return dob;
    }

    public double calculateBMI() {
        boolean isImperial = units != null && units.equalsIgnoreCase("imperial");
        double heightInMeters = isImperial ? height * 0.0254 : height / 100.0;
        double weightInKg = isImperial ? weight * 0.453592 : weight;
        return weightInKg / (heightInMeters * heightInMeters);
    }

    public int getAge() {
        return Period.between(dob, LocalDate.now()).getYears();
    }
}


