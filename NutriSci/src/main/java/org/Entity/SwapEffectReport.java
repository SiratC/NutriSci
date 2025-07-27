package org.Entity;
import org.jfree.chart.JFreeChart;

public class SwapEffectReport extends Report {

    private final JFreeChart comparisonChart;
    private final CFGDifference cfgDifference;

    public SwapEffectReport(JFreeChart comparisonChart, CFGDifference cfgDifference) {

        this.comparisonChart = comparisonChart;
        this.cfgDifference = cfgDifference;
    }

    public JFreeChart getComparisonChart() {

        return comparisonChart;
    }

    public CFGDifference getCfgDifference() {

        return cfgDifference;
    }

    // for debug purpose
    @Override
    public String toString() {

        return "SwapEffectReport[" + "generatedAt=" + generatedAt + ", cfgDifference=" + cfgDifference + ", chart=" + (comparisonChart != null ? "true" : "false") + ']';
    }
}