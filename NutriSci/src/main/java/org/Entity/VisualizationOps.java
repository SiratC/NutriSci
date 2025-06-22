package org.Entity;

import org.Enums.ChartType;
import org.Enums.NutrientType;

import java.util.List;

/**
 * Defines visualization of charts (Pie, Bar, Line, Plate) regarding nutrients.
 */
public class VisualizationOps {
    private DateRange dateRange;
    private List<NutrientType> nutrients;
    private int topCount;
    private boolean includeOther;
    private ChartType chartType;
    private boolean beforeAfter;
}
