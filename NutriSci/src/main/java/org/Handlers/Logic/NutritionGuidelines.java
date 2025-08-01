package org.Handlers.Logic;
import org.Enums.CFGVersion;
import org.Enums.NutrientType;
import java.util.EnumMap;
import java.util.Map;

public class NutritionGuidelines {

    private final Map<NutrientType, Double> targets;

    private NutritionGuidelines(Map<NutrientType, Double> targets) {

        this.targets = targets;
    }

    public static NutritionGuidelines forVersion(CFGVersion version) {

        Map<NutrientType, Double> map = new EnumMap<>(NutrientType.class);

        if (version == CFGVersion.V2007) {

            map.put(NutrientType.Protein, 20.0);
            map.put(NutrientType.Carbohydrate, 55.0);
            map.put(NutrientType.Fat, 25.0);
            map.put(NutrientType.Fiber, 5.0);

        }

        else if (version == CFGVersion.V2019) {

            map.put(NutrientType.Protein, 25.0);
            map.put(NutrientType.Carbohydrate, 45.0);
            map.put(NutrientType.Fat, 30.0);
            map.put(NutrientType.Fiber, 10.0);

        }

        return new NutritionGuidelines(map);
    }

    public double getTargetPercentage(NutrientType type) {

        return targets.getOrDefault(type, 0.0);
    }
}
