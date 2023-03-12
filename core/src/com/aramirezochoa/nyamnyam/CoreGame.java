package com.aramirezochoa.nyamnyam;

import com.aramirezochoa.nyamnyam.activity.ActivityManager;
import com.aramirezochoa.nyamnyam.screen.ScreenManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
        Gdx.input.setCatchKey(Input.Keys.SPACE, true);
        Gdx.input.setCatchKey(Input.Keys.UP, true);
        Gdx.input.setCatchKey(Input.Keys.DOWN, true);
        Gdx.input.setCatchKey(Input.Keys.LEFT, true);
        Gdx.input.setCatchKey(Input.Keys.RIGHT, true);

        Gdx.input.setCatchBackKey(true);
//        // Loading main manager, others in LoadingState
        ActivityManager.INSTANCE.init(application);
        DataManager.INSTANCE.init();
//        StoreManager.INSTANCE.init();

        ScreenManager.INSTANCE.init(this);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
//        StoreManager.INSTANCE.dispose();
    }

}