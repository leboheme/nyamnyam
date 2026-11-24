package com.aramirezochoa.nyamnyam.screen.game.run;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.aramirezochoa.nyamnyam.screen.game.core.CollisionBody;
import com.aramirezochoa.nyamnyam.screen.game.core.CollisionLayer;

/** Small shared lifecycle for run-owned pickups simulated by RunPhysicsSystem. */
public interface RunPickup extends CollisionBody {
    Rectangle getBox();
    Vector2 getVelocity();
    void setPosition(float x, float y);
    boolean canCollect();
    boolean apply(RunRuntime runtime);
    void render(ShapeRenderer shapes);

    @Override default CollisionLayer getCollisionLayer() { return CollisionLayer.FREE; }
    @Override default Rectangle getCollisionBounds() { return getBox(); }
    @Override default Vector2 getCollisionVelocity() { return getVelocity(); }
    @Override default boolean isCollisionActive() { return true; }
    @Override default float getCollisionRotation() { return 0; }
    @Override default void setCollisionRotation(float degrees) { }
    @Override default void moveByCollision(float x, float y) {
        setPosition(getBox().x + x, getBox().y + y);
    }
}
