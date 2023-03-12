package com.aramirezochoa.nyamnyam.client;

import com.aramirezochoa.nyamnyam.ActivityEngine;
import com.aramirezochoa.nyamnyam.CoreGame;
import com.aramirezochoa.nyamnyam.activity.ActivityTransaction;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.FreetypeInjector;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.inject.OnCompletion;

public class HtmlLauncher extends GwtApplication implements ActivityEngine {

    @Override
    public GwtApplicationConfiguration getConfig() {
        // Resizable application, uses available space in browser
//        return new GwtApplicationConfiguration(true);
        // Fixed size application:
        //return new GwtApplicationConfiguration(480, 320);
        GwtApplicationConfiguration config = new GwtApplicationConfiguration(800, 480, GwtApplication.isMobileDevice());
        config.padHorizontal = 0;
        config.padVertical = 0;
        config.fullscreenOrientation = GwtGraphics.OrientationLockType.LANDSCAPE;

        return config;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new CoreGame(this);
    }

    @Override
    public void onModuleLoad() {
        FreetypeInjector.inject(new OnCompletion() {
            public void run() {
                HtmlLauncher.super.onModuleLoad();
            }
        });
    }

    @Override
    public void showBannerAd(boolean show) {

    }

    @Override
    public void showInterstitial(ActivityTransaction activityTransaction) {

    }

    @Override
    public void showVideoGift(ActivityTransaction activityTransaction) {

    }

    @Override
    public void trackScreen(String screen) {

    }

    @Override
    public void trackEvent(String category, String action) {

    }

    @Override
    public void trackEvent(String category, String action, String label) {

    }

    @Override
    public void showRateUs(ActivityTransaction activityTransaction) {

    }
}