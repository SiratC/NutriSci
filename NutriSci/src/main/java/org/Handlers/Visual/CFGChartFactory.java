package org.Handlers.Visual;


import org.Entity.AlignmentScore;
import org.Enums.FoodGroup;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.util.Map;

public class CFGChartFactory {


    public static JFreeChart createGroupAlignmentBarChart(AlignmentScore alignmentScore) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        alignmentScore.getDetails().forEach((group, percent) ->
            dataset.addValue(percent, "Alignment %", group.name())
        );
        return ChartFactory.createBarChart(
            "Per-Group Alignment Score vs. CFG",
            "Food Group",
            "Alignment (%)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
    }


    public static JFreeChart createGroupAlignmentPieChart(AlignmentScore alignmentScore) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        for (Map.Entry<FoodGroup, Double> entry : alignmentScore.getDetails().entrySet()) {
            dataset.setValue(entry.getKey().name(), entry.getValue());
        }

        return ChartFactory.createPieChart(
            "Per-Group Alignment Score vs. CFG",
            dataset,
            true,
            true,
            false
        );
    }
}
