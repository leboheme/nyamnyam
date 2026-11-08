package com.aramirezochoa.nyamnyam.client;

import com.aramirezochoa.nyamnyam.CoreGame;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import org.teavm.jso.JSBody;

public final class HtmlGameApplicationListener implements ApplicationListener {
    private static final float MIN_FRAME_TIME = 1f / 60f;
    private static final float FRAME_TIME_TOLERANCE = 0.001f;

    private final CoreGame delegate;
    private float accumulatedTime;
    private boolean ready;

    public HtmlGameApplicationListener(CoreGame delegate) {
        this.delegate = delegate;
    }

    @Override
    public void create() {
        execute(delegate::create);
    }

    @Override
    public void resize(int width, int height) {
        execute(() -> delegate.resize(width, height));
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        if (delta < MIN_FRAME_TIME - FRAME_TIME_TOLERANCE) {
            accumulatedTime += delta;
            if (accumulatedTime < MIN_FRAME_TIME)
                return;
            accumulatedTime -= MIN_FRAME_TIME;
            delta = MIN_FRAME_TIME;
        } else {
            accumulatedTime = 0f;
        }
        final float renderDelta = delta;
        execute(() -> {
            delegate.render(renderDelta);
            if (!ready) {
                ready = true;
                dispatchReady();
            }
        });
    }

    @Override
    public void pause() {
        execute(delegate::pause);
    }

    @Override
    public void resume() {
        execute(delegate::resume);
    }

    @Override
    public void dispose() {
        execute(delegate::dispose);
    }

    private void execute(Runnable action) {
        try {
            action.run();
        } catch (Throwable error) {
            dispatchError();
            throw error;
        }
    }

    @JSBody(script = "window.dispatchEvent(new Event('nyam-game-ready'));")
    private static native void dispatchReady();

    @JSBody(script = "window.dispatchEvent(new Event('nyam-game-error'));")
    private static native void dispatchError();
}
