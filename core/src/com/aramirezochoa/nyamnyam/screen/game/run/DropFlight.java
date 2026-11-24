package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.CollisionWorld;
import com.badlogic.gdx.math.Vector2;
import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Shared launch/landing motion for food, tooth and stat pickups; no entity ownership. */
public class DropFlight {
    private final Vector2 position, velocity;
    private final float width, height;
    private final CollisionWorld world;
    private boolean landed;
    private float rotation;

    public DropFlight(Vector2 position, Vector2 velocity, float width, float height, CollisionWorld world, boolean launched) {
        this.position = position;
        this.velocity = velocity;
        this.width = width;
        this.height = height;
        this.world = world;
        landed = !launched;
    }

    public float getRotation() { return rotation; }

    public void update(float deltaTime) {
        if (landed) return;
        velocity.y -= DROP_GRAVITY * deltaTime;
        int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(velocity.x), Math.abs(velocity.y))
                * deltaTime / DROP_MAX_MOVEMENT_STEP));
        float stepTime = deltaTime / steps;
        for (int i = 0; i < steps && !landed; i++) advance(stepTime);
        if (!landed) rotation -= Math.signum(velocity.x) * DROP_SPIN_DEGREES_PER_SECOND * deltaTime;
    }

    private void advance(float deltaTime) {
        int tile = world.getTileSize();
        float x = position.x + velocity.x * deltaTime;
        float y = position.y + velocity.y * deltaTime;
        float left = tile, right = world.getWidth() - tile - width;
        if (x < left) {
            x = left;
            velocity.x = Math.abs(velocity.x) * DROP_ROOM_RESTITUTION;
        } else if (x > right) {
            x = right;
            velocity.x = -Math.abs(velocity.x) * DROP_ROOM_RESTITUTION;
        }
        // Ignore interior walls/ceilings during the arc; only the room's outer ceiling bounds it.
        float ceiling = world.getHeight() - tile - height;
        if (y > ceiling) {
            y = ceiling;
            velocity.y = -Math.abs(velocity.y) * DROP_ROOM_RESTITUTION;
        }
        if (velocity.y < 0) {
            float floor = Float.NEGATIVE_INFINITY;
            if (y <= tile) floor = tile;
            int firstRow = Math.max(0, (int) Math.floor(y / tile));
            int lastRow = (int) Math.floor(position.y / tile);
            int firstColumn = (int) Math.floor((x + BUBBLE_TERRAIN_EDGE_EPSILON) / tile);
            int lastColumn = (int) Math.floor((x + width - BUBBLE_TERRAIN_EDGE_EPSILON) / tile);
            for (int row = firstRow; row <= lastRow; row++) {
                float top = (row + 1) * tile;
                if (top > position.y || top < y) continue;
                for (int column = firstColumn; column <= lastColumn; column++) {
                    if (world.hasTile(column, row)) floor = Math.max(floor, top);
                }
            }
            if (floor != Float.NEGATIVE_INFINITY) {
                y = floor;
                velocity.setZero();
                rotation = 0;
                landed = true;
            }
        }
        position.set(x, y);
    }

}
