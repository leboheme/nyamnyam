package com.aramirezochoa.nyamnyam.screen.game.room;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;

/** Static meal position uses interior tile coordinates with the origin at the bottom-left. */
public class MealSpawnConfig {
    public MealType type;
    public int x;
    public int y;
    public boolean expiring = true;

    public MealSpawnConfig() {
    }
}
