package com.aramirezochoa.nyamnyam.activity;

import com.aramirezochoa.nyamnyam.ActivityEngine;
import com.aramirezochoa.nyamnyam.DataManager;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatarType;

/**
 * Created by leboheme on 15/02/15.
 */
public enum ActivityManager {
    INSTANCE;

    public static final String SCREEN_MENU = "MENU";
    public static final String SCREEN_HELP = "HELP";
    public static final String SCREEN_GAME = "GAME";
    public static final String SCREEN_STORE = "STORE";

    private static final String CATEGORY_MENU = "MENU";
    private static final String ACTION_CONTINUE = "CONTINUE";
    private static final String ACTION_NEW_GAME = "NEW_GAME";
    private static final String ACTION_DAILY_GIFT_OK = "DAILY_GIFT_OK";
    private static final String ACTION_DAILY_GIFT_KO = "DAILY_GIFT_KO";
    private static final String ACTION_REWARD = "REWARD";
    private static final String ACTION_PLAY_CHARACTER = "PLAY_";

    private static final String CATEGORY_GAME = "GAME";
    private static final String ACTION_DEAD = "DEAD";
    private static final String ACTION_SUCCESS = "SUCCESS";

    private ActivityEngine activityEngine;

    public void init(ActivityEngine ActivityEngine) {
        this.activityEngine = ActivityEngine;
    }

    public void showBannerAd(boolean show) {
        activityEngine.showBannerAd(show);
    }

    public void showInterstitial(ActivityTransaction activityTransaction) {
        activityEngine.showInterstitial(activityTransaction);
    }

    public void showVideoGift(ActivityTransaction activityTransaction) {
        activityEngine.showVideoGift(activityTransaction);
    }

    public void trackScreen(String screen) {
        activityEngine.trackScreen(screen);
    }

    public void trackDailyGift(boolean success) {
        activityEngine.trackEvent(CATEGORY_MENU, success ? ACTION_DAILY_GIFT_OK : ACTION_DAILY_GIFT_KO);
    }

    public void trackReward() {
        activityEngine.trackEvent(CATEGORY_MENU, ACTION_REWARD);
    }

    public void trackContinue() {
        activityEngine.trackEvent(CATEGORY_MENU, ACTION_CONTINUE + DataManager.INSTANCE.getCurrentCharacter().name());
    }

    public void trackNewGame() {
        activityEngine.trackEvent(CATEGORY_MENU, ACTION_NEW_GAME + DataManager.INSTANCE.getCurrentCharacter().name());
    }

    public void trackDead(int level) {
        activityEngine.trackEvent(CATEGORY_GAME, ACTION_DEAD, String.valueOf(level));
    }

    public void trackSuccess(int level, int lives, int score) {
        activityEngine.trackEvent(CATEGORY_GAME, ACTION_SUCCESS, level + "_" + lives + "_" + score);
    }

    public void trackPlayCharacter(MainAvatarType mainAvatarType) {
        activityEngine.trackEvent(CATEGORY_MENU, ACTION_PLAY_CHARACTER + mainAvatarType.name());
    }

    public void showRateUs(ActivityTransaction activityTransaction) {
        activityEngine.showRateUs(activityTransaction);
    }
}