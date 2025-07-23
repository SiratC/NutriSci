package org.Entity;

/**
 * Compares FoodGroupStats before and after swap operations.
 */
public class CFGDifference {

    private final FoodGroupStats beforeStats;

    private final FoodGroupStats afterStats;

    /**
     * Initializes the class with provided food stats.
     *
     * @param before the food group statistics before the swap.
     * @param after the food group statistics after the swap.
     */
    public CFGDifference(FoodGroupStats before, FoodGroupStats after) {

        this.beforeStats = before;
        this.afterStats = after;
    }

    /**
     * Returns the before stats.
     * @return stats before swap
     */
    public FoodGroupStats getBeforeStats() {

        return beforeStats;
    }

    /**
     * Returns the after stats
     * @return stats after swap
     */
    public FoodGroupStats getAfterStats() {

        return afterStats;
    }

    /**
     * Returns the delta (%) between before and after for a specific food group.
     * @param group the food group
     * @return the difference between after and before
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
