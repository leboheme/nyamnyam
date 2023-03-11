package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.main;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.Entity;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.AvatarState;

/**
 * Created by boheme on 15/01/15.
 */
public class EatEntityState extends AvatarState {

    private static final String EAT = "EAT";
    private final static float STATE_TIME = 0.5f;
    private float timerState = STATE_TIME;
    private boolean alreadyDone = false;

    private Entity entity;

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateEntity(avatar, deltaTime, false);
        if (!alreadyDone) {
            entity.eaten(avatar);
            alreadyDone = true;
            return false;
        } else if (timerState < 0) {
            return true;
        }
        timerState -= deltaTime;
        return false;
    }

    @Override
    public String getCode() {
        return EAT;
    }

    @Override
    public void reset() {
        timerState = STATE_TIME;
        alreadyDone = false;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
