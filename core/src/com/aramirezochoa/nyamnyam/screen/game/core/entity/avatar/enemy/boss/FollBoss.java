package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.boss;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;

/**
 * Created by boheme on 5/02/15.
 */
public class FollBoss extends Boss {

    @Override
    protected EntityState getInitialState() {
        return EnemyStateFactory.loop(EnemyStateFactory.boss(this, Constant.BOSS_MODE_1));
    }

    @Override
    protected int getLives() {
        return Constant.BOSS_FOLL_LIVES;
    }
}
