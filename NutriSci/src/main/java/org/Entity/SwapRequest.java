package org.Entity;

import org.Enums.CFGVersion;
import org.Enums.NutrientType;

public class SwapRequest {
    private final Profile user;
    private final DateRange range;
    private final NutrientType targetNutrient;
    private final CFGVersion cfgVersion;
    private final double intensityAmount;
    private final boolean isPercentage;

    public SwapRequest(Profile user, DateRange range, NutrientType targetNutrient, CFGVersion cfgVersion,
            double intensityAmount, boolean isPercentage) {
        this.user = user;
        this.range = range;
        this.targetNutrient = targetNutrient;
        this.cfgVersion = cfgVersion;
        this.intensityAmount = intensityAmount;
        this.isPercentage = isPercentage;
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

    public CFGVersion getCfgVersion() {
        return cfgVersion;
    }

    public double getIntensityAmount() {
        return intensityAmount;
    }

    public boolean isPercentage() {
        return isPercentage;
    }
}
