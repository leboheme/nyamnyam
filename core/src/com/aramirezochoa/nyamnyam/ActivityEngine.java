package com.aramirezochoa.nyamnyam;

import com.aramirezochoa.nyamnyam.activity.ActivityTransaction;

/**
 * Created by boheme on 16/09/14.
 */
public interface ActivityEngine {

    void showBannerAd(boolean show);

    void showInterstitial(ActivityTransaction activityTransaction);

    void showVideoGift(ActivityTransaction activityTransaction);

    void trackScreen(String screen);

    void trackEvent(String category, String action);

    void trackEvent(String category, String action, String label);

    void showRateUs(ActivityTransaction activityTransaction);
}