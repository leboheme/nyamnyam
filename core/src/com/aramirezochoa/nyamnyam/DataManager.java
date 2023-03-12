package com.aramirezochoa.nyamnyam;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatarType;
import com.aramirezochoa.nyamnyam.screen.game.status.GameStatus;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by boheme on 9/02/15.
 */
public enum DataManager {
    INSTANCE;

    public static final String COMMON_DATA = "commonData";
    private static final String NYAM_DATA = "nyamData";
    private static final String GLUTY_DATA = "glutyData";
    private static final String TFLY_DATA = "tflyData";
    private static final String LIVE_CHECK = "lltc";
    private static final String DAILY_GIFT = "ldg";
    private static final String LEVEL = "level";
    private static final String LIVES = "lives";
    private static final String SCORE = "score";
    private static final String BOOST_MOVEMENT = "mvb";
    private static final String BOOST_FIRE_DISTANCE = "fdb";
    private static final String BOOST_FIRE_LAPSE = "flb";
    private static final String BOOST_FIRE_VELOCITY = "fvb";
    private static final String CURRENT_CHARACTER = "lcc";
    private static final String RATE_US = "ru";

    private GameStatus gameStatus;
    private boolean firstTime = false;
    private long nextFreeLiveTimer;
    private long lastDailyGift;
    private MainAvatarType currentCharacter = MainAvatarType.NYAM;
    private boolean externalGift;
    private boolean showRateUs;

    public void updateFreeLive() {
        if (gameStatus.getLives() < 3) {
            if (nextFreeLiveTimer < getTime()) {
                gameStatus.increaseLive();
                saveStatus(gameStatus);
                nextFreeLiveTimer += Constant.GAME_FREE_LIVE_TIMER;
                saveNextFreeLiveTimer();
            }
        }
    }

    public void loadNewGame() {
        gameStatus = new GameStatus(1, Constant.GAME_NEW_GAME_LIVES, 0);
        saveGameStatus(false);
    }

    public void init() {
        loadCurrentCharacter();
        loadCurrentGame(getDataCode());
        loadRateUs();
    }

    private void loadRateUs() {
        if (!firstTime) {
            Preferences prefs = Gdx.app.getPreferences(COMMON_DATA);
            if (!prefs.contains(RATE_US)) {
                prefs.putInteger(RATE_US, 5); // 5 tries???
                prefs.flush();
            }
            int rateUsCounter = prefs.getInteger(RATE_US);
            if (rateUsCounter > 0) {
                showRateUs = MathUtils.randomBoolean(0.25f);
            } else {
                showRateUs = false;
            }
        }
    }

    public String getDataCode() {
        switch (currentCharacter) {
            case NYAM:
                return NYAM_DATA;
            case GLUTY:
                return GLUTY_DATA;
            case TFLY:
                return TFLY_DATA;
        }
        return "";
    }

    private void loadCurrentGame(String container) {
        Preferences prefs = Gdx.app.getPreferences(container);
        if (isEmpty(prefs, LEVEL) || isEmpty(prefs, LIVES) || isEmpty(prefs, SCORE)) {
            loadNewGame();
            firstTime = true;
        } else {
            gameStatus = new GameStatus(prefs.getInteger(LEVEL), prefs.getInteger(LIVES), prefs.getInteger(SCORE));
            gameStatus.setMovementBoost(loadBoost(prefs, BOOST_MOVEMENT));
            gameStatus.setFireDistanceBoost(loadBoost(prefs, BOOST_FIRE_DISTANCE));
            gameStatus.setFireLapseBoost(loadBoost(prefs, BOOST_FIRE_LAPSE));
            gameStatus.setFireVelocityBoost(loadBoost(prefs, BOOST_FIRE_VELOCITY));

            if (!prefs.contains(LIVE_CHECK)) {
                nextFreeLiveTimer = getTime() + Constant.GAME_FREE_LIVE_TIMER;
                saveNextFreeLiveTimer();
            }

            nextFreeLiveTimer = prefs.getLong(LIVE_CHECK);
            checkDailyGift();
        }
    }

    private int loadBoost(Preferences prefs, String boostKey) {
        try {
            return prefs.getInteger(boostKey);
        } catch (Exception e) {
            boolean active = prefs.getBoolean(boostKey);
            prefs.remove(boostKey);
            prefs.putInteger(boostKey, active ? Constant.MAIN_AVATAR_BOOST_LIVES : 0);
            prefs.flush();
            return active ? Constant.MAIN_AVATAR_BOOST_LIVES : 0;
        }
    }

    private void checkDailyGift() {
        Preferences prefs = Gdx.app.getPreferences(COMMON_DATA);
        if (!prefs.contains(DAILY_GIFT)) {
            lastDailyGift = 0;
            saveLastDailyGift();
        }
        lastDailyGift = prefs.getLong(DAILY_GIFT);
        firstTime = false;
    }

    private boolean isEmpty(Preferences prefs, String key) {
        return !prefs.contains(key);
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void saveGameStatus(boolean decreaseLive) {
        // save status
        saveStatus(gameStatus);
        // check if lives < Constant.GAME_NEW_GAME_LIVES, then activate timer
        if (decreaseLive && gameStatus.getLives() == 2) {
            nextFreeLiveTimer = getTime() + Constant.GAME_FREE_LIVE_TIMER;
            saveNextFreeLiveTimer();
        }
    }

    private void saveNextFreeLiveTimer() {
        Preferences prefs = Gdx.app.getPreferences(getDataCode());
        prefs.putLong(LIVE_CHECK, nextFreeLiveTimer);
        prefs.flush();
    }

    public boolean isFreeLiveRunning() {
        return gameStatus.getLives() < Constant.GAME_NEW_GAME_LIVES;
    }

    private void saveStatus(GameStatus gameStatus) {
        Preferences prefs = Gdx.app.getPreferences(getDataCode());
        prefs.putInteger(LEVEL, gameStatus.getLevel());
        prefs.putInteger(LIVES, gameStatus.getLives());
        prefs.putInteger(SCORE, gameStatus.getScore());
        prefs.putInteger(BOOST_MOVEMENT, gameStatus.getMovementBoost());
        prefs.putInteger(BOOST_FIRE_DISTANCE, gameStatus.getFireDistanceBoost());
        prefs.putInteger(BOOST_FIRE_LAPSE, gameStatus.getFireLapseBoost());
        prefs.putInteger(BOOST_FIRE_VELOCITY, gameStatus.getFireVelocityBoost());
        prefs.flush();
        firstTime = false;
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    private long getTime() {
        return System.currentTimeMillis();
    }

    public long getNextFreeLiveTimerRemain() {
        return nextFreeLiveTimer - getTime();
    }

    private void saveLastDailyGift() {
        Preferences prefs = Gdx.app.getPreferences(COMMON_DATA);
        prefs.putLong(DAILY_GIFT, lastDailyGift);
        prefs.flush();
    }

    public boolean isDailyGiftAvailable() {
        if (getTime() < lastDailyGift) {
            return false;
        }
        return lastDailyGift >= (System.currentTimeMillis() - (60 * 60 * 24 * 1000));
    }

    public void dailyGiftDone() {
        lastDailyGift = getTime();
        saveLastDailyGift();
    }

    public MainAvatarType getCurrentCharacter() {
        return currentCharacter;
    }

    public void setCurrentCharacter(MainAvatarType currentCharacter) {
        this.currentCharacter = currentCharacter;
        saveCurrentCharacter();
        loadCurrentGame(getDataCode());
    }

    private void loadCurrentCharacter() {
        Preferences prefs = Gdx.app.getPreferences(COMMON_DATA);
        if (!prefs.contains(CURRENT_CHARACTER)) {
            currentCharacter = MainAvatarType.NYAM;
            prefs.putString(CURRENT_CHARACTER, currentCharacter.name());
            prefs.flush();
        } else {
            currentCharacter = MainAvatarType.valueOf(prefs.getString(CURRENT_CHARACTER));
        }
    }

    private void saveCurrentCharacter() {
        Preferences prefs = Gdx.app.getPreferences(COMMON_DATA);
        prefs.putString(CURRENT_CHARACTER, currentCharacter.name());
        prefs.flush();
    }

    public boolean isExternalGift() {
        return externalGift;
    }

    public void setExternalGift(boolean externalGift) {
        this.externalGift = externalGift;
    }

    public boolean isShowRateUs() {
        return showRateUs;
    }

    public void notifyShowRateUs(boolean result) {
        showRateUs = false;
        Preferences prefs = Gdx.app.getPreferences(COMMON_DATA);
        int rateUsCounter = prefs.getInteger(RATE_US);
        if (result) {
            rateUsCounter = 0;
        } else {
            rateUsCounter--;
        }
        prefs.putInteger(RATE_US, rateUsCounter);
        prefs.flush();
    }
}
