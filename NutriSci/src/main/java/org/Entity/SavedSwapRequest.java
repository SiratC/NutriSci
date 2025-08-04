package org.Entity;

import org.Enums.NutrientType;
import java.time.LocalDateTime;
import java.util.UUID;

public class SavedSwapRequest {
    private UUID id;
    private UUID profileId;
    private String name;
    private String description;
    private NutrientType targetNutrient;
    private double intensityAmount;
    private boolean isPercentage;
    
    // Optional second target nutrient fields
    private NutrientType secondTargetNutrient;
    private double secondIntensityAmount;
    private boolean secondIsPercentage;
    
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;

    // Default constructor
    public SavedSwapRequest() {}

    // Constructor for single target swap
    public SavedSwapRequest(UUID profileId, String name, String description, 
            NutrientType targetNutrient, double intensityAmount, boolean isPercentage) {
        this.profileId = profileId;
        this.name = name;
        this.description = description;
        this.targetNutrient = targetNutrient;
        this.intensityAmount = intensityAmount;
        this.isPercentage = isPercentage;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor for dual target swap
    public SavedSwapRequest(UUID profileId, String name, String description,
            NutrientType targetNutrient, double intensityAmount, boolean isPercentage,
            NutrientType secondTargetNutrient, double secondIntensityAmount, boolean secondIsPercentage) {
        this(profileId, name, description, targetNutrient, intensityAmount, isPercentage);
        this.secondTargetNutrient = secondTargetNutrient;
        this.secondIntensityAmount = secondIntensityAmount;
        this.secondIsPercentage = secondIsPercentage;
    }

    // Convert to SwapRequest for execution
    public SwapRequest toSwapRequest(Profile user, DateRange range) {
        if (hasSecondTarget()) {
            return new SwapRequest(user, range, targetNutrient, intensityAmount, isPercentage,
                    secondTargetNutrient, secondIntensityAmount, secondIsPercentage);
        } else {
            return new SwapRequest(user, range, targetNutrient, intensityAmount, isPercentage);
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public NutrientType getTargetNutrient() {
        return targetNutrient;
    }

    public void setTargetNutrient(NutrientType targetNutrient) {
        this.targetNutrient = targetNutrient;
    }

    public double getIntensityAmount() {
        return intensityAmount;
    }

    public void setIntensityAmount(double intensityAmount) {
        this.intensityAmount = intensityAmount;
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    public void setPercentage(boolean percentage) {
        isPercentage = percentage;
    }

    public NutrientType getSecondTargetNutrient() {
        return secondTargetNutrient;
    }

    public void setSecondTargetNutrient(NutrientType secondTargetNutrient) {
        this.secondTargetNutrient = secondTargetNutrient;
    }

    public double getSecondIntensityAmount() {
        return secondIntensityAmount;
    }

    public void setSecondIntensityAmount(double secondIntensityAmount) {
        this.secondIntensityAmount = secondIntensityAmount;
    }

    public boolean isSecondPercentage() {
        return secondIsPercentage;
    }

    public void setSecondPercentage(boolean secondPercentage) {
        secondIsPercentage = secondPercentage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public boolean hasSecondTarget() {
        return secondTargetNutrient != null;
    }

    public String getTargetDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(targetNutrient);
        if (isPercentage) {
            sb.append(" +").append(String.format("%.1f", intensityAmount * 100)).append("%");
        } else {
            sb.append(" +").append(String.format("%.1f", intensityAmount)).append("g");
        }
        
        if (hasSecondTarget()) {
            sb.append(", ").append(secondTargetNutrient);
            if (secondIsPercentage) {
                sb.append(" +").append(String.format("%.1f", secondIntensityAmount * 100)).append("%");
            } else {
                sb.append(" +").append(String.format("%.1f", secondIntensityAmount)).append("g");
            }
        }
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("SavedSwapRequest{name='%s', targets='%s', created=%s}", 
                name, getTargetDescription(), createdAt);
    }
}