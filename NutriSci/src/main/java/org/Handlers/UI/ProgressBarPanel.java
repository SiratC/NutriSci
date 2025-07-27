package org.Handlers.UI;

import org.Enums.NutrientType;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ProgressBarPanel extends JPanel {

    private final Map<NutrientType, Double> progressMap;

    public ProgressBarPanel(Map<NutrientType, Double> progressMap) {

        this.progressMap = progressMap;
        setLayout(new GridLayout(progressMap.size(), 1, 5, 5));
        setBackground(Color.WHITE);
        buildBars();
    }

    private void buildBars() {

        for (Map.Entry<NutrientType, Double> entry : progressMap.entrySet()) {
            String label = entry.getKey().name();
            double value = Math.min(entry.getValue(), 100.0);

            JPanel barPanel = new JPanel(new BorderLayout());
            JLabel nameLabel = new JLabel(label + ": " + String.format("%.1f", value) + "%", JLabel.LEFT);

            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue((int) value);
            bar.setStringPainted(true);
            bar.setForeground(getColorForPercentage(value));

            barPanel.add(nameLabel, BorderLayout.WEST);
            barPanel.add(bar, BorderLayout.CENTER);

            add(barPanel);
        }
    }

    private Color getColorForPercentage(double pct) {

        if (pct < 50) return Color.RED;
        if (pct < 85) return Color.ORANGE;

        return new Color(0, 180, 0);
    }
}
