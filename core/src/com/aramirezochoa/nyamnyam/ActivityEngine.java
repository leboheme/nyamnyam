package com.aramirezochoa.nyamnyam;

import com.aramirezochoa.nyamnyam.activity.ActivityManager;

/**
 * Created by boheme on 16/09/14.
 */
public interface ActivityEngine {

    void showBannerAd(boolean show);

    void showInterstitial(ActivityManager.ActivityTransaction activityTransaction);

    void showVideoGift(ActivityManager.ActivityTransaction activityTransaction);

    void trackScreen(String screen);

    void trackEvent(String category, String action);

    void trackEvent(String category, String action, String label);

    void showRateUs(ActivityManager.ActivityTransaction activityTransaction);
}