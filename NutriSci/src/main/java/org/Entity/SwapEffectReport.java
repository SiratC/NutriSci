package org.Entity;
import org.jfree.chart.JFreeChart;

/**
  * A concrete implementation of Report that handles the nutrient swap between meals.
  */
public class SwapEffectReport extends Report {

    private final JFreeChart comparisonChart;
    private final CFGDifference cfgDifference;

    /**
     * A SwapEffectReport with data of a given chart and cfg differences.
     * @param comparisonChart chart of swap
     * @param cfgDifference cfg difference of swap
     */
    public SwapEffectReport(JFreeChart comparisonChart, CFGDifference cfgDifference) {

        this.comparisonChart = comparisonChart;
        this.cfgDifference = cfgDifference;
    }

    /**
     * Returns chart of swap.
     * @return chart
     */
    public JFreeChart getComparisonChart() {

        return comparisonChart;
    }

    /**
     * Returns CFG difference of swap.
     * @return cfg difference
     */
    public CFGDifference getCfgDifference() {

        return cfgDifference;
    }

    // for debug purpose
    @Override
    public String toString() {

        return "SwapEffectReport[" + "generatedAt=" + generatedAt + ", cfgDifference=" + cfgDifference + ", chart=" + (comparisonChart != null ? "true" : "false") + ']';
    }
}