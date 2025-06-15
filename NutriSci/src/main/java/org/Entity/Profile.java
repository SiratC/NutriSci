package org.Entity;

import org.Enums.Sex;

import java.time.LocalDate;
import java.util.UUID;

public class Profile {
    private UUID userID;
    private Sex sex;
    private LocalDate dob;
    private double height;
    private double weight;
    private String units;

    public Profile(UUID userID, Sex sex, LocalDate dob, double height, double weight, String units) {
        this.userID = userID;
        this.sex = sex;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.units = units;
    }
    public UUID getUserID() {
        return this.userID;
    }

    public Sex getGender(){
        return this.sex;
    }

    public LocalDate getDob(){
        return this.dob;
    }

    public double getHeight(){
        return this.height;
    }

    public double getWeight(){
        return this.weight;
    }
    public String getUnits(){
        return this.units;
    }

}
