package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.projectile;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;

/**
 * Created by boheme on 26/01/15.
 */
public class DropProjectileState extends ProjectileState {

    @Override
    public void init() {
        super.init();
        getProjectile().velocity.x = 0;
        getProjectile().velocity.y = getProjectile().getVelocity();
        getProjectile().acceleration.y = -Constant.GRAVITY.y;
    }

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateProjectile(getProjectile(), deltaTime, getProjectile().getType().isTileCollision());
        if (getProjectile().position.y < -getProjectile().dimension.y || getProjectile().velocity.y == 0) {
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
        getProjectile().velocity.x = 0;
        getProjectile().velocity.y = 0;
        getProjectile().acceleration.y = 0;
    }

}
