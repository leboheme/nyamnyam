package com.aramirezochoa.nyamnyam.screen.game.room;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.EnemyType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;

/** Enemy position uses interior tile coordinates with the origin at the bottom-left. */
public class EnemySpawnConfig {
    public EnemyType type;
    public int x;
    public int y;
    public DirectionType facing = DirectionType.RIGHT;

    public EnemySpawnConfig() {
    }
}
