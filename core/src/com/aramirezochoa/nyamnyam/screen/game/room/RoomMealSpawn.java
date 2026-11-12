package com.aramirezochoa.nyamnyam.screen.game.room;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;

/** Runtime copy of a static meal entry in the room's selected layout. */
public class RoomMealSpawn {
    private final MealType type;
    private final int x;
    private final int y;
    private final boolean expiring;

    public RoomMealSpawn(MealType type, int x, int y, boolean expiring) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.expiring = expiring;
    }

    public MealType getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isExpiring() { return expiring; }
}
