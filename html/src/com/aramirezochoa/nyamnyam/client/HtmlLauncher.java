package com.aramirezochoa.nyamnyam.client;

import com.aramirezochoa.nyamnyam.ActivityEngine;
import com.aramirezochoa.nyamnyam.CoreGame;
import com.aramirezochoa.nyamnyam.activity.ActivityTransaction;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import org.teavm.jso.JSBody;

public final class HtmlLauncher implements ActivityEngine {
    private HtmlLauncher() {
    }

    public static void main(String[] args) {
        installUncaughtErrorLogger();
        migrateLegacyPreferences();
        WebApplicationConfiguration configuration = new WebApplicationConfiguration("canvas");
        configuration.width = 640;
        configuration.height = 360;
        configuration.usePhysicalPixels = true;
        configuration.storagePrefix = "nyamnyam";
        new WebApplication(
            new HtmlGameApplicationListener(new CoreGame(new HtmlLauncher())),
            new HtmlPreloadApplicationListener(),
            configuration
        );
    }

    @JSBody(script = "window.addEventListener('error', function (event) { var error = event.error; var details = []; var javaErrors = []; var depth = 0; while (error && depth < 8) { details.push((error.name || 'Error') + ': ' + (error.message || String(error))); if (error.stack) details.push(error.stack); var symbols = Object.getOwnPropertySymbols(error); for (var i = 0; i < symbols.length; i++) { if (String(symbols[i]) === 'Symbol(javaException)') javaErrors.push(error[symbols[i]]); } error = error.cause; depth++; } console.error('Nyam Nyam uncaught error:', details.join('\\nCaused by: '), javaErrors); });")
    private static native void installUncaughtErrorLogger();

    @JSBody(script = "try { var storage = window.localStorage; var names = ['commonData', 'nyamData', 'glutyData', 'tflyData']; for (var nameIndex = 0; nameIndex < names.length; nameIndex++) { var legacyPrefix = names[nameIndex] + ':'; var legacyKeys = []; for (var index = 0; index < storage.length; index++) { var key = storage.key(index); if (key && key.indexOf(legacyPrefix) === 0) legacyKeys.push(key); } for (var keyIndex = 0; keyIndex < legacyKeys.length; keyIndex++) { var legacyKey = legacyKeys[keyIndex]; var currentKey = 'pref:nyamnyam:' + legacyKey; if (storage.getItem(currentKey) === null) storage.setItem(currentKey, storage.getItem(legacyKey)); } } } catch (error) {}")
    private static native void migrateLegacyPreferences();

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
