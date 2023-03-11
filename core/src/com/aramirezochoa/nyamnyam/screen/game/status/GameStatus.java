package com.aramirezochoa.nyamnyam.screen.game.status;

import com.badlogic.gdx.utils.Array;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.DataManager;
import com.aramirezochoa.nyamnyam.activity.ActivityManager;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.ScreenManager;
import com.aramirezochoa.nyamnyam.screen.game.GameState;

/**
 * Created by boheme on 14/01/15.
 */
public class GameStatus {

    private volatile GameState gameState = GameState.IN_GAME;
    private int level;
    private volatile int lives = 3;
    private volatile int score = 0;
    private volatile int lastExtraLiveCheck;

    private Array<GameStatusListener> listeners = new Array<GameStatusListener>(2);
    private int movementBoost, fireLapseBoost, fireDistanceBoost, fireVelocityBoost;

    public GameStatus(int level, int lives, int score) {
        this.level = level;
        this.lives = lives;
        this.score = score;

        lastExtraLiveCheck = ((score / Constant.SCORE_INTERVAL_EXTRA_LIFE) + 1) * Constant.SCORE_INTERVAL_EXTRA_LIFE;
    }

    public synchronized int getLives() {
        return lives;
    }

    public synchronized void setLives(int lives) {
        this.lives = lives;
    }

    public synchronized int getScore() {
        return score;
    }

    public synchronized void addScore(int scoreToIncrease, float x, float y) {
        score += scoreToIncrease;
        if (score >= lastExtraLiveCheck) {
            increaseLive();
            lastExtraLiveCheck += Constant.SCORE_INTERVAL_EXTRA_LIFE;
        }
        for (GameStatusListener listener : listeners) {
            listener.notifyScore(score, scoreToIncrease, x, y);
        }
    }

    public synchronized void increaseLive() {
        lives++;
        for (GameStatusListener listener : listeners) {
            listener.notifyLives(lives, 0, 0);
        }
        ScreenManager.INSTANCE.getCurrentScreen().getMediaManager().playSound(MediaManager.SOUND_LIVE);
    }

    public synchronized void decreaseLive() {
        lives--;
        movementBoost = --movementBoost < 0 ? 0 : movementBoost;
        fireDistanceBoost = --fireDistanceBoost < 0 ? 0 : fireDistanceBoost;
        fireLapseBoost = --fireLapseBoost < 0 ? 0 : fireLapseBoost;
        fireVelocityBoost = --fireVelocityBoost < 0 ? 0 : fireVelocityBoost;
        DataManager.INSTANCE.saveGameStatus(true);
        for (GameStatusListener listener : listeners) {
            listener.notifyLives(lives, 0, 0);
        }
        ActivityManager.INSTANCE.trackDead(level);
    }

    public void addListener(GameStatusListener listener) {
        listeners.add(listener);
    }

    public synchronized GameState getGameState() {
        return gameState;
    }

    public synchronized void setGameState(GameState gameState) {
        this.gameState = gameState;
        if (GameState.WIN.equals(gameState)) {
            level++;
            DataManager.INSTANCE.saveGameStatus(false);
            ActivityManager.INSTANCE.trackSuccess(level, lives, score);
        }
        for (GameStatusListener listener : listeners) {
            listener.notifyState(gameState);
        }
    }

    public boolean isPaused() {
        return gameState.isPaused();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void enableMovementBoost() {
        setMovementBoost(Constant.MAIN_AVATAR_BOOST_LIVES);
    }

    public void setMovementBoost(int movementBoost) {
        this.movementBoost = movementBoost;
    }

    public void enableFireLapseBoost() {
        setFireLapseBoost(Constant.MAIN_AVATAR_BOOST_LIVES);
    }

    public void setFireLapseBoost(int fireLapseBoost) {
        this.fireLapseBoost = fireLapseBoost;
    }

    public void enableFireDistanceBoost() {
        setFireDistanceBoost(Constant.MAIN_AVATAR_BOOST_LIVES);
    }

    public void setFireDistanceBoost(int fireDistanceBoost) {
        this.fireDistanceBoost = fireDistanceBoost;
    }

    public void enableFireVelocityBoost() {
        setFireVelocityBoost(Constant.MAIN_AVATAR_BOOST_LIVES);
    }

    public void setFireVelocityBoost(int fireVelocityBoost) {
        this.fireVelocityBoost = fireVelocityBoost;
    }

    public boolean isMovementBoost() {
        return movementBoost > 0;
    }

    public boolean isFireLapseBoost() {
        return fireLapseBoost > 0;
    }

    public boolean isFireDistanceBoost() {
        return fireDistanceBoost > 0;
    }

    public boolean isFireVelocityBoost() {
        return fireVelocityBoost > 0;
    }

    public boolean isFinished() {
        return level > Constant.GAME_LEVELS;
    }

    public int getMovementBoost() {
        return movementBoost;
    }

    public int getFireDistanceBoost() {
        return fireDistanceBoost;
    }

    public int getFireLapseBoost() {
        return fireLapseBoost;
    }

    public int getFireVelocityBoost() {
        return fireVelocityBoost;
    }
}
