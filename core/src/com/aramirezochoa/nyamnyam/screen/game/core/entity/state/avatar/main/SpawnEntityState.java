package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.main;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.AvatarState;

/**
 * Created by boheme on 15/01/15.
 */
public class SpawnEntityState extends AvatarState {

    public static final String CODE = "SPAWN";
    private float timer;

    @Override
    public void init() {
        timer = avatar.getSpawnTime();
    }

    @Override
    public boolean update(float deltaTime) {
        if (timer < 0) {
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
        timer = avatar.getSpawnTime();
    }

}
