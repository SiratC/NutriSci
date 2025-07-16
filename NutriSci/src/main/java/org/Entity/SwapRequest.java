package org.Entity;

import org.Enums.CFGVersion;
import org.Enums.NutrientType;

public class SwapRequest {
    private final Profile user;
    private final DateRange range;
    private final NutrientType targetNutrient;
    private final CFGVersion cfgVersion;

    public SwapRequest(Profile user, DateRange range, NutrientType targetNutrient, CFGVersion cfgVersion) {
        this.user = user;
        this.range = range;
        this.targetNutrient = targetNutrient;
        this.cfgVersion = cfgVersion;
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
}
