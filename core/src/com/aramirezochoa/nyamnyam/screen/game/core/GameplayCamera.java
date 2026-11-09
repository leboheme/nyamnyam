package com.aramirezochoa.nyamnyam.screen.game.core;

import com.aramirezochoa.nyamnyam.Constant;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** Owns the gameplay viewport and keeps the camera inside the current room bounds. */
public class GameplayCamera {

    private final OrthographicCamera camera;
    private final FitViewport viewport;

    public GameplayCamera() {
        camera = new OrthographicCamera(Constant.GAMEPLAY_VIEWPORT_WIDTH, Constant.GAMEPLAY_VIEWPORT_HEIGHT);
        viewport = new FitViewport(Constant.GAMEPLAY_VIEWPORT_WIDTH, Constant.GAMEPLAY_VIEWPORT_HEIGHT, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        camera.setToOrtho(false, Constant.GAMEPLAY_VIEWPORT_WIDTH, Constant.GAMEPLAY_VIEWPORT_HEIGHT);
        camera.update();
    }

    public void follow(float targetX, float targetY, float worldWidth, float worldHeight) {
        float halfWidth = camera.viewportWidth / 2;
        float halfHeight = camera.viewportHeight / 2;
        camera.position.set(
                worldWidth <= camera.viewportWidth ? worldWidth / 2 : MathUtils.clamp(targetX, halfWidth, worldWidth - halfWidth),
                worldHeight <= camera.viewportHeight ? worldHeight / 2 : MathUtils.clamp(targetY, halfHeight, worldHeight - halfHeight),
                0);
        camera.update();
    }

    public void apply() {
        viewport.apply();
        camera.update();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
