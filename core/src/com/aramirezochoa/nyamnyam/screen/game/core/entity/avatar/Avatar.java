package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.Entity;

/**
 * Created by boheme on 13/01/15.
 */
public abstract class Avatar extends Entity {


    public float getVelocity() {
        return 0;
    }

    public void dieStuff() {
        // for mainAvatar
    }

    @Override
    public void dampVelocity() {
        velocity.x *= Constant.DAMPING;
    }

    public float getFireDistance() {
        return 0;
    }

    public float getFireVelocity() {
        return 0;
    }

    public void createFire() {
        // nothing default
    }

    public float getJumpVelocity() {
        return Constant.AVATAR_JUMP_VELOCITY;
    }

    public float getSpawnTime() {
        return 0;
    }
}
