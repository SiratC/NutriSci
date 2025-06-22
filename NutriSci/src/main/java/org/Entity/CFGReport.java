package org.Entity;

import org.Enums.FoodGroup;

import java.util.Map;

/**
 * Defines a report of the selected meal as it adheres to the CFG.
 */
public class CFGReport extends Report{
    private Map<FoodGroup, Double> groupPercentages;
}
