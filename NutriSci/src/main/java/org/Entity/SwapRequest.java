package org.Entity;

import org.Enums.NutrientType;

public class SwapRequest {
    private final Profile user;
    private final DateRange range;
    private final NutrientType targetNutrient;
    private final double intensityAmount;
    private final boolean isPercentage;

    // Optional second target nutrient fields
    private final NutrientType secondTargetNutrient;
    private final double secondIntensityAmount;
    private final boolean secondIsPercentage;

    // Single target for backward compatibility
    public SwapRequest(Profile user, DateRange range, NutrientType targetNutrient,
            double intensityAmount, boolean isPercentage) {
        this(user, range, targetNutrient, intensityAmount, isPercentage, null, 0.0, false);
    }

    // Dual targets
    public SwapRequest(Profile user, DateRange range, NutrientType targetNutrient,
            double intensityAmount, boolean isPercentage, NutrientType secondTargetNutrient,
            double secondIntensityAmount, boolean secondIsPercentage) {
        this.user = user;
        this.range = range;
        this.targetNutrient = targetNutrient;
        this.intensityAmount = intensityAmount;
        this.isPercentage = isPercentage;
        this.secondTargetNutrient = secondTargetNutrient;
        this.secondIntensityAmount = secondIntensityAmount;
        this.secondIsPercentage = secondIsPercentage;
    }

    public Profile getUser() {
        return user;
    }

    public DateRange getRange() {
        return range;
    }

    public NutrientType getTargetNutrient() {
        return targetNutrient;
    }

    public double getIntensityAmount() {
        return intensityAmount;
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    public NutrientType getSecondTargetNutrient() {
        return secondTargetNutrient;
    }

    public double getSecondIntensityAmount() {
        return secondIntensityAmount;
    }

    public boolean isSecondPercentage() {
        return secondIsPercentage;
    }

    public boolean hasSecondTarget() {
        return secondTargetNutrient != null;
    }
}
