package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;

/**
 * Created by boheme on 27/01/15.
 */
public class LitoEnemy extends Enemy {

    @Override
    protected EntityState getInitialState() {
        return EnemyStateFactory.loop(EnemyStateFactory.invade(this, ProjectileType.ROCK));
    }

    @Override
    protected float getWalkVelocity() {
        return Constant.LITO_WALK_VELOCITY;
    }

    @Override
    public void dampVelocity() {
        // nothing
    }

}
