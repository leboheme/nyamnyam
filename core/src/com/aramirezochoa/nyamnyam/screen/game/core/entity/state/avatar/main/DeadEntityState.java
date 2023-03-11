package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.main;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.AvatarState;

/**
 * Created by boheme on 15/01/15.
 */
public class DeadEntityState extends AvatarState {

    public static final String CODE = "DEAD";
    private float timer = Constant.MAIN_AVATAR_TIME_DEAD;
    private boolean deadNotified;

    @Override
    public boolean update(float deltaTime) {
        if (!deadNotified) {
            avatar.dieStuff();
            deadNotified = true;
        }
        if (timer < 0) {
            avatar.die();
            return true;
        }
        timer -= deltaTime;
        return false;
    }

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public void reset() {
        timer = Constant.MAIN_AVATAR_TIME_DEAD;
        deadNotified = false;
    }

}
