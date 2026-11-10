package com.aramirezochoa.nyamnyam.screen.game.core;

import com.aramirezochoa.nyamnyam.Constant;

/** Tracks the stage timers that trigger Hurry and Dark. */
public class StageTimers {

    private float darkTimer;
    private float hurryTimer;
    private boolean hurryAdvised;

    public void reset() {
        darkTimer = Constant.GAME_LEVEL_DARK_TIME;
        hurryTimer = Constant.GAME_LEVEL_HURRY_TIME;
        hurryAdvised = false;
    }

    public boolean updateHurry(float deltaTime, boolean bossLevel) {
        if (bossLevel) {
            return false;
        }
        if (hurryTimer < 0 && !hurryAdvised) {
            hurryAdvised = true;
            return true;
        }
        hurryTimer -= deltaTime;
        return false;
    }

    public boolean updateDark(float deltaTime, boolean bossLevel) {
        if (bossLevel) {
            return false;
        }
        if (darkTimer < 0) {
            return true;
        }
        darkTimer -= deltaTime;
        return false;
    }
}
