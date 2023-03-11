package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;

/**
 * Created by boheme on 26/01/15.
 */
public class PhantEnemy extends Enemy {

    @Override
    protected EntityState getInitialState() {
        return EnemyStateFactory.loop(EnemyStateFactory.bounce(this, 10));
    }

    @Override
    public void dampVelocity() {
        // nothing
    }

    @Override
    protected boolean isLoopAnimation() {
        return true;
    }
}
