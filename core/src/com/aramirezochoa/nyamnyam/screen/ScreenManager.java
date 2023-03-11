package com.aramirezochoa.nyamnyam.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.aramirezochoa.nyamnyam.CoreGame;
import com.aramirezochoa.nyamnyam.screen.loading.LoadingScreen;
import com.aramirezochoa.nyamnyam.screen.splash.SplashScreen;

/**
 * Created by boheme on 12/01/15.
 */
public enum ScreenManager {
    INSTANCE;

    private Game game;
    private ScreenType currentScreen;
    private ScreenType lastScreen;
    private boolean shortCut;

    public void init(CoreGame game) {
        this.game = game;
        if (shortCut) {
            changeTo(ScreenType.MENU);
        } else {
            setScreen(new SplashScreen());
        }
    }

    public void changeTo(ScreenType newScreenType) {
        if (ScreenType.EXIT.equals(newScreenType)) {
            Gdx.app.exit();
        } else {
            try {
                lastScreen = currentScreen;
                // Load assets, and loading screen will call to set next screen
                setScreen(new LoadingScreen(newScreenType.getScreen().getMediaManager()));
                currentScreen = newScreenType;
            } catch (Exception e) {
                Gdx.app.error("StateManager", "Could not set next screen " + newScreenType.name(), e);
            }
        }
    }

    public void changeToLastScreen() {
        changeTo(lastScreen);
    }

    private void setScreen(Screen screen) {
        game.setScreen(screen);
    }

    public AbstractScreen getCurrentScreen() {
        return currentScreen.getScreen();
    }

    public void loaded() {
        game.setScreen(currentScreen.getScreen());
    }

    public void setShortCut(boolean shortCut) {
        this.shortCut = shortCut;
    }
}
