package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.projectile;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;

/**
 * Created by boheme on 27/01/15.
 */
public class ParabolicProjectileState extends ProjectileState {

    private static final String DROP = "DROP";

    @Override
    public void init() {
        super.init();
        getProjectile().velocity.x = DirectionType.RIGHT.equals(getProjectile().getLookingAt()) ? Constant.ROCK_VELOCITY.x : -Constant.ROCK_VELOCITY.x;
        getProjectile().velocity.y = Constant.ROCK_VELOCITY.y;
        getProjectile().acceleration.y = 0;
    }

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateProjectile(getProjectile(), deltaTime, getProjectile().getType().isTileCollision());
        if (getProjectile().position.y < -getProjectile().dimension.y
                || getProjectile().position.x < Constant.TILE_SIZE
                || getProjectile().position.x + getProjectile().dimension.x > Constant.SCREEN_WIDTH - Constant.TILE_SIZE) {
            return true;
        }
        return false;
    }

    @Override
    public String getCode() {
        return DROP;
    }

    @Override
    public void reset() {
        getProjectile().velocity.x = 0;
        getProjectile().velocity.y = 0;
    }

}
