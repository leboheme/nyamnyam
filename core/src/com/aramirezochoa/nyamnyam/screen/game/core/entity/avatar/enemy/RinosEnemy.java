package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;

/**
 * Created by boheme on 26/01/15.
 */
public class RinosEnemy extends Enemy {

    @Override
    protected EntityState getInitialState() {
        return EnemyStateFactory.loop(EnemyStateFactory.invade(this, ProjectileType.HORN));
    }

    @Override
    protected float getWalkVelocity() {
        return Constant.RINOS_WALK_VELOCITY;
    }

    @Override
    public void dampVelocity() {
        // nothing
    }
}