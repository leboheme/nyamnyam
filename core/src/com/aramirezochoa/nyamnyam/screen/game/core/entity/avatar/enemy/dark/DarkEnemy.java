package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.dark;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;

/**
 * Created by leboheme on 3/03/15.
 */
public class DarkEnemy extends Enemy {

    @Override
    protected EntityState getInitialState() {
        return EnemyStateFactory.sequence(EnemyStateFactory.spawn(this), EnemyStateFactory.loop(EnemyStateFactory.follow(this)));
    }

    @Override
    public boolean isLoopAnimation() {
        return super.isLoopAnimation() || currentState.getCode().equals("FLY");
    }

    @Override
    public float getSpawnTime() {
        return Constant.DARK_TIME_SPAWN;
    }

    @Override
    public void remove() {
        super.remove();
        parent.dark = null;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
