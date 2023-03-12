package com.aramirezochoa.nyamnyam;

import com.aramirezochoa.nyamnyam.activity.ActivityTransaction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher implements ActivityEngine {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Nyam Nyam!");
		config.setWindowedMode(800, 480);
		config.setResizable(false);
		new Lwjgl3Application(new CoreGame(new DesktopLauncher()), config);
	}

	@Override
	public void showBannerAd(boolean show) {
		Gdx.app.error("ADs", "Show banner ad: " + show);
	}

	@Override
	public void showInterstitial(ActivityTransaction activityTransaction) {
		Gdx.app.error("ADs", "Show interstitial");
		activityTransaction.done(true);
	}

	@Override
	public void showVideoGift(ActivityTransaction activityTransaction) {
		Gdx.app.error("Desktop", "Video gift showed");
		activityTransaction.done(true);
	}

	@Override
	public void trackScreen(String screen) {
		Gdx.app.error("Analytics", "Track screen: " + screen);
	}

	@Override
	public void trackEvent(String category, String action) {
		Gdx.app.error("Analytics", "Track event. Category: " + category + ". Action: " + action);
	}

	@Override
	public void trackEvent(String category, String action, String label) {
		Gdx.app.error("Analytics", "Track event. Category: " + category + ". Action: " + action + ". Label: " + label);
	}

	@Override
	public void showRateUs(ActivityTransaction activityTransaction) {
		Gdx.app.error("Rate us", "");
		activityTransaction.done(false);
	}
}
