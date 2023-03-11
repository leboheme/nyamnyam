package com.aramirezochoa.nyamnyam.screen.game.core.entity.meal;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;

/**
 * Created by boheme on 3/02/15.
 */
public enum MealType implements EntityType {

    // Air
    AIR,
    AIR_TORNADO,
    AIR_SPIKES,

    // Boost
    BOOST_PIZZA,
    BOOST_BURGER,
    BOOST_MEAT,
    BOOST_HOTDOG,
    BOOST_LIVE,
    BOOST_CATERPILLAR,
    BOOST_EGG,
    BOOST_GARLIC,
    BOOST_CHILLI,
    BOOST_TORNADO,

    // Candy
    CANDY_BEAR,
    CANDY_CLOUD,
    CANDY_LICORICE,
    CANDY_LICORICE_SP,
    CANDY_PAPER,
    CANDY_STICK,

    // Verdures
    VERDURE_LETTUCE,
    VERDURE_CABBAGE,
    VERDURE_CARROT,
    VERDURE_BROCCOLI,

    // Fruits
    FRUIT_APPLE,
    FRUIT_BANANA,
    FRUIT_CHERRIES,
    FRUIT_ORANGE,
    FRUIT_PINEAPPLE;

    @Override
    public Vector2 getDimension() {
        return Constant.CANDY_DIMENSION;
    }

    public static MealType randomCandy() {
        switch (MathUtils.random(5)) {
            case 0:
                return MealType.CANDY_BEAR;
            case 1:
                return MealType.CANDY_CLOUD;
            case 2:
                return MealType.CANDY_LICORICE;
            case 3:
                return MealType.CANDY_LICORICE_SP;
            case 4:
                return MealType.CANDY_PAPER;
            case 5:
                return MealType.CANDY_STICK;
        }
        return MealType.CANDY_PAPER;
    }

    public boolean isAir() {
        return name().contains("AIR");
    }

    public boolean isBoost() {
        return name().contains("BOOST");
    }

    public boolean isCandy() {
        return name().contains("CANDY");
    }

    public boolean isVerdure() {
        return name().contains("VERDURE");
    }

    public boolean isFruit() {
        return name().contains("FRUIT");
    }

    public static MealType parseAir(String air) {
        for (MealType mealType : values()) {
            if (mealType.name().equals(air)) {
                return mealType;
            }
        }
        return MealType.AIR;
    }

    public static MealType parseMeal(String meal) {
        for (MealType mealType : values()) {
            if (mealType.name().equals(meal)) {
                return mealType;
            }
        }
        return randomFruit();
    }

    public static MealType randomVerdure() {
        switch (MathUtils.random(4)) {
            case 0:
                return MealType.VERDURE_LETTUCE;
            case 1:
                return MealType.VERDURE_CABBAGE;
            case 2:
                return MealType.VERDURE_CARROT;
            case 3:
                return MealType.VERDURE_BROCCOLI;
        }
        return MealType.VERDURE_LETTUCE;
    }

    public static MealType randomFruit() {
        switch (MathUtils.random(5)) {
            case 0:
                return MealType.FRUIT_APPLE;
            case 1:
                return MealType.FRUIT_BANANA;
            case 2:
                return MealType.FRUIT_CHERRIES;
            case 3:
                return MealType.FRUIT_ORANGE;
            case 4:
                return MealType.FRUIT_PINEAPPLE;
        }
        return MealType.FRUIT_PINEAPPLE;
    }

}
