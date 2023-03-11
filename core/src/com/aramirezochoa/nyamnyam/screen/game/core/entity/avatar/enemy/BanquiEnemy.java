package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;

/**
 * Created by boheme on 30/01/15.
 */
public class BanquiEnemy extends Enemy {

    @Override
    protected EntityState getInitialState() {
        return EnemyStateFactory.loop(EntityStateFactory.sequence(
                EnemyStateFactory.think(this, 0.25f),
                EnemyStateFactory.jump(this, true)
        ));
    }

    @Override
    protected float getWalkVelocity() {
        return Constant.BANQUI_WALK_VELOCITY;
    }

    @Override
    public float getJumpVelocity() {
        return Constant.BANQUI_JUMP_VELOCITY;
    }

}
