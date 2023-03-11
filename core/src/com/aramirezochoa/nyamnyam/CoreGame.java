package com.aramirezochoa.nyamnyam;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.aramirezochoa.nyamnyam.activity.ActivityManager;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.ScreenManager;
import com.aramirezochoa.nyamnyam.store.StoreManager;

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
        Gdx.input.setCatchBackKey(true);
//        // Loading main manager, others in LoadingState
        ActivityManager.INSTANCE.init(application);

        DataManager.INSTANCE.init();
//        StoreManager.INSTANCE.init();

        MediaManager.init();
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