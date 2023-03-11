package com.aramirezochoa.nyamnyam.screen.game;

/**
 * Created by leboheme on 22/01/15.
 */
public enum GameState {
    IN_GAME {
        @Override
        public boolean isPaused() {
            return false;
        }
    },
    PAUSE,
    WIN,
    LOSE;

    public boolean isPaused() {
        return true;
    }
}
