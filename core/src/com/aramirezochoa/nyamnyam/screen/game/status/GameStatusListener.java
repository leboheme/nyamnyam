package com.aramirezochoa.nyamnyam.screen.game.status;

import com.aramirezochoa.nyamnyam.screen.game.GameState;

/**
 * Created by boheme on 22/01/15.
 */
public interface GameStatusListener {

    void notifyState(GameState state);

    void notifyScore(int score, int increased, float x, float y);

    void notifyLives(int lives, float x, float y);

}
