package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy;

import com.badlogic.gdx.math.MathUtils;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;

/**
 * Created by boheme on 27/01/15.
 */
public class FollEnemy extends Enemy {

    @Override
    protected EntityState getInitialState() {
        return EnemyStateFactory.loop(EntityStateFactory.sequence(
                EnemyStateFactory.walk(this, 1.5f + MathUtils.random(2)),
                EnemyStateFactory.think(this, 0.5f + MathUtils.random(0.5f)),
                EnemyStateFactory.fire(this)
        ));
    }

    @Override
    protected boolean isLoopAnimation() {
        return super.isLoopAnimation() || currentState.getCode().equals("FALL");
    }

    public void createFire() {
        if (canFire()) {
            getParent().addProjectile(EntityFactory.projectile(ProjectileType.ENERGY, this, getLookingAt(), false, false));
        }
    }
}
