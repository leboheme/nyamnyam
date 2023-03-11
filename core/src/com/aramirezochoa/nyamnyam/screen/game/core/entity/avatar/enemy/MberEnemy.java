package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;

/**
 * Created by boheme on 30/01/15.
 */
public class MberEnemy extends Enemy {


    @Override
    protected EntityState getInitialState() {
        return EnemyStateFactory.climb(this);
    }

    @Override
    protected float getWalkVelocity() {
        return Constant.MBER_WALK_VELOCITY;
    }

}
