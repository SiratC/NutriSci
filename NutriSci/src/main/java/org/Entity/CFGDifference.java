package org.Entity;

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

    /**
     * returns the delta (%) between before and after for a specific food group.
     */
    public double getChangeForGroup(org.Enums.FoodGroup group) {

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
