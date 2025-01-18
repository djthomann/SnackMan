package de.hsrm.mi.swt.projekt.snackman.model.gameEntities;

import de.hsrm.mi.swt.projekt.snackman.model.level.MapGenerationConfig;

public enum FoodType {

    UNHEALTHY(MapGenerationConfig.UNHEALTHY_CALORIES),
    OKAY(MapGenerationConfig.OKAY_CALORIES),
    HEALTHY(MapGenerationConfig.HEALTHY_CALORIES),
    EGG(MapGenerationConfig.EGG_CALORIES);

    final int calories;

    FoodType(int c) {
        calories = c;
    }

    public int getCalories() {
        return calories;
    }
}
