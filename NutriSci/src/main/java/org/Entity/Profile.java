package org.Entity;

import org.Enums.Sex;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Handles user profiles alongside personal settings.
 * <p>Holds ID, sex, date of birth, height, weight, and units selected by user.</p>
 */
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

    /**
     * Creates a user profile with given attributes.
     *
     * @param userID the ID of the user
     * @param name the name of the user
     * @param sex the sex of the user
     * @param dob the date of birth of the user
     * @param height the height of the user
     * @param weight the weight of the user
     * @param units the chosen units of measurement
     * @param createdAt the time of creation
     * @param modifiedAt the time of modification
     */
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


    /**
     * Returns the user's ID.
     *
     * @return user identification
     */
    public UUID getUserID() {
        return userID;
    }

    /**
     * Sets the user's ID.
     *
     * @param userID user identification
     */
    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    /**
     * Returns the name of the user.
     *
     * @return user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name user's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the gender of the user.
     *
     * @return user's sex
     */
    public Sex getSex() {
        return sex;
    }

    /**
     * Sets the gender of the user.
     * @param sex user's sex
     */
    public void setSex(Sex sex) {
        this.sex = sex;
    }

    /**
     * Returns the date of birth of the user.
     *
     * @return user's date of birth
     */
    public LocalDate getDob() {
        return dob;
    }

    /**
     * Sets the date of birth for the user.
     *
     * @param dob date of birth
     */
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    /**
     * Returns the height of the user.
     *
     * @return user's height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the height of the user.
     *
     * @param height user's height
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Returns weight of the user.
     *
     * @return user's weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight of the user.
     *
     * @param weight user's weight
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Returns the units of measurements chosen by the user.
     *
     * @return units of measurements
     */
    public String getUnits() {
        return units;
    }

    /**
     * Sets the units of measurements.
     *
     * @param units units of measurement
     */
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     * Returns the time of creation for the profile.
     *
     * @return time of profile created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the time of creation for the profile.
     *
     * @param createdAt time of profile created
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the time of modification for the profile.
     *
     * @return time of profile modified
     */
    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    /**
     * Sets the time of modification for the profile
     *
     * @param modifiedAt time of profile modified
     */
    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
