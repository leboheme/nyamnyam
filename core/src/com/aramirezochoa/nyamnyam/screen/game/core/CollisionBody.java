package com.aramirezochoa.nyamnyam.screen.game.core;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/** A world object that participates in logical layer collision checks. */
public interface CollisionBody {
    CollisionLayer getCollisionLayer();
    Rectangle getCollisionBounds();
    Vector2 getCollisionVelocity();
    float getCollisionRotation();
    void setCollisionRotation(float degrees);
    boolean isCollisionActive();
    void moveByCollision(float x, float y);
}
