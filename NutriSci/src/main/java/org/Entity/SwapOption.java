package org.Entity;
import org.Enums.NutrientType;
import java.util.Map;

/**
 * Handles the swapping of items within a meal alongside the nutrient change due to the swap.
 */
public class SwapOption {

    private final String originalItem;

    private final String replacementItem;

    private final Map<NutrientType, Double> nutrientDelta;

    /**
     * SwapOption with specified items and nutrient change.
     * @param originalItem item to be changed
     * @param replacementItem item changed
     * @param nutrientDelta difference of nutrients between items
     */
    public SwapOption(String originalItem, String replacementItem, Map<NutrientType, Double> nutrientDelta) {

        this.originalItem = originalItem;
        this.replacementItem = replacementItem;
        this.nutrientDelta = nutrientDelta;
    }

    /**
     * Returns the original item
     * @return first item
     */
    public String getOriginalItem() {

        return originalItem;
    }

    /**
     * Returns the swap item.
     * @return replacement item
     */
    public String getReplacementItem() {

        return replacementItem;
    }

    /**
     * Returns the nutrient difference between items.
     * @return nutrient difference
     */
    public Map<NutrientType, Double> getNutrientDelta() {

        return nutrientDelta;
    }

    // for debug purpose
    @Override
    public String toString() {

        return "SwapOption[" + "original='" + originalItem  + ", replacement='" + replacementItem + ", nutrientDelta=" + nutrientDelta + ']';
    }
}
