package com.aramirezochoa.nyamnyam.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.aramirezochoa.nyamnyam.media.MediaManager;

/**
 * Created by boheme on 12/01/15.
 */
public abstract class AbstractScreen implements Screen {

    private final MediaManager mediaManager;

    protected AbstractScreen(MediaManager mediaManager) {
        this.mediaManager = mediaManager;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        getMediaManager().unloadAssets();
    }

    public MediaManager getMediaManager() {
        return mediaManager;
    }

    protected boolean isBackJustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK);
    }
}
