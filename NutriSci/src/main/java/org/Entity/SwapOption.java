package org.Entity;
import org.Enums.NutrientType;
import java.util.Map;

public class SwapOption {

    private final String originalItem;

    private final String replacementItem;

    private final Map<NutrientType, Double> nutrientDelta;

    public SwapOption(String originalItem, String replacementItem, Map<NutrientType, Double> nutrientDelta) {

        this.originalItem = originalItem;
        this.replacementItem = replacementItem;
        this.nutrientDelta = nutrientDelta;
    }

    public String getOriginalItem() {

        return originalItem;
    }

    public String getReplacementItem() {

        return replacementItem;
    }

    public Map<NutrientType, Double> getNutrientDelta() {

        return nutrientDelta;
    }

    // for debug purpose
    @Override
    public String toString() {

        return "SwapOption[" + "original='" + originalItem  + ", replacement='" + replacementItem + ", nutrientDelta=" + nutrientDelta + ']';
    }
}
