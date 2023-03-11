package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.projectile;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;

/**
 * Created by boheme on 15/01/15.
 */
public class HurledProjectileState extends ProjectileState {

    public static final String CODE = "HURLED";
    private float initX, distance;
    private boolean transformToCandy;

    @Override
    public void init() {
        super.init();
        getProjectile().acceleration.y = -Constant.GRAVITY.y;
        getProjectile().velocity.x = DirectionType.RIGHT.equals(getProjectile().getLookingAt()) ? getPitcher().getFireVelocity() : -getPitcher().getFireVelocity();
        distance = getPitcher().getFireDistance();
    }

    private Avatar getPitcher() {
        return getProjectile().getPitcher();
    }

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateEntity(getProjectile(), deltaTime, false);
        if ((getProjectile().velocity.x == 0 || Math.abs(getProjectile().position.x - initX) >= distance)) {
            if (transformToCandy) {
                getProjectile().transform();
            }
            return true;
        }
        return false;
    }

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public void reset() {
        getProjectile().velocity.x = 0;
        getProjectile().acceleration.y = 0;
    }

    public void setInitX(float initX) {
        this.initX = initX;
    }

    public void setTransformToCandy(boolean transformToCandy) {
        this.transformToCandy = transformToCandy;
    }
}
