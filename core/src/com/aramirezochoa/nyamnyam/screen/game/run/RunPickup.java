package com.aramirezochoa.nyamnyam.screen.game.run;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/** Small shared lifecycle for run-owned pickups that use DropFlight. */
public interface RunPickup {
    Rectangle getBox();
    boolean canCollect();
    void update(float deltaTime);
    boolean apply(RunRuntime runtime);
    void render(ShapeRenderer shapes);
}
