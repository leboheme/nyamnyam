package com.aramirezochoa.nyamnyam.screen.game.room;

import com.badlogic.gdx.utils.Array;

/** Selected runtime content arrangement for a room. */
public class RoomLayout {
    private final String name;
    private final Array<RoomEnemySpawn> enemies = new Array<RoomEnemySpawn>();
    private final Array<RoomMealSpawn> meals = new Array<RoomMealSpawn>();

    public RoomLayout(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addEnemy(RoomEnemySpawn enemy) {
        enemies.add(enemy);
    }

    public Array<RoomEnemySpawn> getEnemies() {
        return enemies;
    }

    public void addMeal(RoomMealSpawn meal) {
        meals.add(meal);
    }

    public Array<RoomMealSpawn> getMeals() {
        return meals;
    }
}
