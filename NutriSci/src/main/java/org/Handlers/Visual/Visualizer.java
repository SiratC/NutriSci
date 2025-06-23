package org.Handlers.Visual;



import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import java.util.Map;
/**
 * Manages the creation of visualizations of statistics such as graphs and charts.
 */
public class Visualizer {

    /**
     * Builds a ChartPanel of a Pie Chart of the given data.
     * @param data  labels → values
     * @param title chart title
     * @return the Chart representing the data values
     */
    public static ChartPanel createPieChartPanel(Map<String, Double> data, String title) {

        DefaultPieDataset dataset = new DefaultPieDataset();

        data.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                title,
                dataset,
                true,
                true,
                false
        );
        return new ChartPanel(chart);
    }
}