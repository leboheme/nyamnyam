package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.boss;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;

/**
 * Created by boheme on 5/02/15.
 */
public class BoofBoss extends Boss {

    @Override
    protected EntityState getInitialState() {
        return EnemyStateFactory.loop(EnemyStateFactory.boss(this, Constant.BOSS_MODE_0));
    }

    @Override
    protected int getLives() {
        return Constant.BOSS_BOOF_LIVES;
    }
}
