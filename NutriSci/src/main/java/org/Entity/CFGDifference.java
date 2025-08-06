package org.Entity;

import org.Enums.FoodGroup;

public class CFGDifference {

    private final FoodGroupStats beforeStats;

    private final FoodGroupStats afterStats;

    public CFGDifference(FoodGroupStats before, FoodGroupStats after) {

        this.beforeStats = before;
        this.afterStats = after;
    }

    public FoodGroupStats getBeforeStats() {

        return beforeStats;
    }

    public FoodGroupStats getAfterStats() {

        return afterStats;
    }


    public double getChangeForGroup(FoodGroup group) {
        return calculateDelta(group);
    }

    /**
     * returns the delta (%) between before and after for a specific food group.
     */
    private double calculateDelta(FoodGroup group) {
        double before = beforeStats.getPercentage(group);
        double after = afterStats.getPercentage(group);
        return after - before;
    }


    // for debug purpose
    @Override
    public String toString() {

        return "CFGDifference[" + "beforeStats=" + beforeStats + ", afterStats=" + afterStats + ']';
    }
}
