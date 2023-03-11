package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;

/**
 * Created by boheme on 15/01/15.
 */
public class ThinkAvatarState extends AvatarState {

    private static final String THINK = "THINK";
    private float timerThinking = 1f;
    private float timer = timerThinking;

    public void setTimer(float time) {
        timerThinking = time;
        timer = time;
    }

    @Override
    public boolean update(float deltaTime) {
        avatar.velocity.x = 0;
        GameWrapperAssistant.updateEntity(avatar, deltaTime, false);
        if (timer < 0) {
            return true;
        }
        timer -= deltaTime;
        return false;
    }

    @Override
    public String getCode() {
        return THINK;
    }

    @Override
    public void reset() {
        timer = timerThinking;
    }
}
