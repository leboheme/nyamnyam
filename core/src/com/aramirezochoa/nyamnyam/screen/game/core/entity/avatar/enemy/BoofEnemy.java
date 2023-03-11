package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy;

import com.badlogic.gdx.math.MathUtils;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;

/**
 * Created by boheme on 14/01/15.
 */
public class BoofEnemy extends Enemy {

    @Override
    protected EntityState getInitialState() {
        return EnemyStateFactory.loop(EntityStateFactory.sequence(
                EnemyStateFactory.walk(this, 1.5f + MathUtils.random(2)),
                EnemyStateFactory.think(this, 0.5f + MathUtils.random(0.5f))
        ));
    }
}
