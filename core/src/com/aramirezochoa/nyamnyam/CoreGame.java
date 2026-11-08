package com.aramirezochoa.nyamnyam;

import com.aramirezochoa.nyamnyam.activity.ActivityManager;
import com.aramirezochoa.nyamnyam.screen.ScreenManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;

/**
 * Created by boheme on 10/02/14.
 */
public class CoreGame extends Game {

    private final ActivityEngine application;

    public CoreGame(ActivityEngine application) {
        this.application = application;
    }

    @Override
    public void create() {
//        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        Gdx.input.setCatchKey(Input.Keys.SPACE, true);
        Gdx.input.setCatchKey(Input.Keys.UP, true);
        Gdx.input.setCatchKey(Input.Keys.DOWN, true);
        Gdx.input.setCatchKey(Input.Keys.LEFT, true);
        Gdx.input.setCatchKey(Input.Keys.RIGHT, true);
        Gdx.input.setCatchKey(Input.Keys.TAB, true);

        ActivityManager.INSTANCE.init(application);
        DataManager.INSTANCE.init();
        ScreenManager.INSTANCE.init(this);
    }

    @Override
    public void render() {
        render(Gdx.graphics.getDeltaTime());
    }

    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.TAB)){
            Boolean fullScreen = Gdx.graphics.isFullscreen();
            Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
            if (fullScreen == true)
                Gdx.graphics.setWindowedMode((int) Constant.SCREEN_WIDTH, (int) Constant.SCREEN_HEIGHT);
            else
                Gdx.graphics.setFullscreenMode(currentMode);
        }
        if (screen != null)
            screen.render(delta);
    }

    @Override
    public void dispose() {
//        StoreManager.INSTANCE.dispose();
    }

}
