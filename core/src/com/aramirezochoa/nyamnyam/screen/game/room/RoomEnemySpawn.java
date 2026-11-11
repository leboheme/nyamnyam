package com.aramirezochoa.nyamnyam.screen.game.room;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.EnemyType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;

/** Runtime copy of an enemy entry in the room's selected layout. */
public class RoomEnemySpawn {
    private final EnemyType type;
    private final int x;
    private final int y;
    private final DirectionType facing;

    public RoomEnemySpawn(EnemyType type, int x, int y, DirectionType facing) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.facing = facing;
    }

    public EnemyType getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }
    public DirectionType getFacing() { return facing; }
}
