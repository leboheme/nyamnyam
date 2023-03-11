package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.projectile;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;

/**
 * Created by boheme on 5/02/15.
 */
public class BossProjectileState extends ProjectileState {

    @Override
    public void init() {
        super.init();
        getProjectile().acceleration.y = -Constant.GRAVITY.y;
    }

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateProjectile(getProjectile(), deltaTime, false);
        if (getProjectile().position.y < -getProjectile().dimension.y
                || getProjectile().position.y > Constant.SCREEN_HEIGHT + Constant.SCREEN_GAME_Y_OFFSET + getProjectile().dimension.y
                || getProjectile().position.x + getProjectile().dimension.x < 0
                || getProjectile().position.x > Constant.SCREEN_WIDTH) {
            return true;
        }
        return false;
    }

    @Override
    public String getCode() {
        return getProjectile().getType().name();
    }

    @Override
    public void reset() {
        getProjectile().acceleration.y = 0;
        getProjectile().velocity.x = 0;
        getProjectile().velocity.y = 0;
    }
}
