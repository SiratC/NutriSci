package org.Entity;

import org.Enums.CFGVersion;
import org.Enums.NutrientType;

/**
 * Manages the swap of food items between meals and the time window needed for the swap.
 */
public class SwapRequest {
    private final Profile user;
    private final DateRange range;
    private final NutrientType targetNutrient;
    private final CFGVersion cfgVersion;

    /**
     * Creates a swap request based on given profile, time range, the targetted nutrient and the specified CFG.
     *
     * @param user the user profile
     * @param range the time range specified
     * @param targetNutrient the nutrient specified
     * @param cfgVersion the CFG version used
     */
    public SwapRequest(Profile user, DateRange range, NutrientType targetNutrient, CFGVersion cfgVersion) {
        this.user = user;
        this.range = range;
        this.targetNutrient = targetNutrient;
        this.cfgVersion = cfgVersion;
    }

    /**
     * Returns the user profile in the swap.
     *
     * @return user profile
     */
    public Profile getUser() {
        return user;
    }

    /**
     * Returns the date range used in the swap.
     *
     * @return date range
     */
    public DateRange getRange() {
        return range;
    }

    /**
     * Returns the target nutrient of the swap.
     *
     * @return nutrient targeted
     */
    public NutrientType getTargetNutrient() {
        return targetNutrient;
    }

    /**
     * Returns the CFG version used in the swap.
     *
     * @return CFG version
     */
    public CFGVersion getCfgVersion() {
        return cfgVersion;
    }
}
