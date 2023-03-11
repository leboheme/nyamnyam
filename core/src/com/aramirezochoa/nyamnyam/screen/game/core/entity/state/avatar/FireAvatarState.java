package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;

/**
 * Created by boheme on 15/01/15.
 */
public class FireAvatarState extends AvatarState {

    private static final String FIRE = "FIRE";
    public static final float DEFAULT_TIME = 0.25f;

    private boolean alreadyFired = false;
    private float lastTimer, timer;

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateEntity(avatar, deltaTime, false);

        if (!alreadyFired) {
            avatar.createFire();
            alreadyFired = true;
        } else {
            if (timer < 0) {
                return true;
            }
            timer -= deltaTime;
        }
        return false;
    }

    @Override
    public String getCode() {
        return FIRE;
    }

    @Override
    public void reset() {
        alreadyFired = false;
        timer = lastTimer;
    }

    public void setTime(float time) {
        this.lastTimer = time;
        this.timer = time;
    }
}
