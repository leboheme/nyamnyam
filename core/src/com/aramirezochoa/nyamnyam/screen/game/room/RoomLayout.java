package com.aramirezochoa.nyamnyam.screen.game.room;

import com.badlogic.gdx.utils.Array;

/** Selected runtime content arrangement for a room. */
public class RoomLayout {
    private final Array<RoomEnemySpawn> enemies = new Array<RoomEnemySpawn>();

    public void addEnemy(RoomEnemySpawn enemy) {
        enemies.add(enemy);
    }

    public Array<RoomEnemySpawn> getEnemies() {
        return enemies;
    }
}
