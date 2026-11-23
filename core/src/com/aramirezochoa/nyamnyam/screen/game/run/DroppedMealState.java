package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.CollisionWorld;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Exaggerated spinning arc: room-edge bounce, then land on a surface crossed while falling. */
public class DroppedMealState extends EntityState {
    private Meal meal;
    private CollisionWorld world;
    private float pickupDelay, rotation;
    private boolean landed;

    public void configure(Meal meal, CollisionWorld world, boolean launched) {
        this.meal = meal;
        this.world = world;
        pickupDelay = launched ? DROP_PICKUP_DELAY_SECONDS : 0;
        landed = !launched;
        rotation = 0;
        meal.setRenderRotation(0);
        meal.setEnabled(!launched);
        meal.acceleration.setZero();
    }

    @Override public boolean update(float deltaTime) {
        if (pickupDelay > 0) {
            pickupDelay -= deltaTime;
            if (pickupDelay <= 0) meal.setEnabled(true);
        }
        if (landed) return false;
        meal.velocity.y -= DROP_GRAVITY * deltaTime;
        int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(meal.velocity.x), Math.abs(meal.velocity.y))
                * deltaTime / DROP_MAX_MOVEMENT_STEP));
        float stepTime = deltaTime / steps;
        for (int i = 0; i < steps && !landed; i++) advance(stepTime);
        if (!landed) {
            rotation -= Math.signum(meal.velocity.x) * DROP_SPIN_DEGREES_PER_SECOND * deltaTime;
            meal.setRenderRotation(rotation);
        }
        return false;
    }

    private void advance(float deltaTime) {
        int tile = world.getTileSize();
        float x = meal.position.x + meal.velocity.x * deltaTime;
        float y = meal.position.y + meal.velocity.y * deltaTime;
        float left = tile, right = world.getWidth() - tile - meal.dimension.x;
        if (x < left) {
            x = left;
            meal.velocity.x = Math.abs(meal.velocity.x) * DROP_ROOM_RESTITUTION;
        } else if (x > right) {
            x = right;
            meal.velocity.x = -Math.abs(meal.velocity.x) * DROP_ROOM_RESTITUTION;
        }
        // Ignore interior walls/ceilings during the arc; only the room's outer ceiling bounds it.
        float ceiling = world.getHeight() - tile - meal.dimension.y;
        if (y > ceiling) {
            y = ceiling;
            meal.velocity.y = -Math.abs(meal.velocity.y) * DROP_ROOM_RESTITUTION;
        }
        if (meal.velocity.y < 0) {
            float floor = Float.NEGATIVE_INFINITY;
            if (y <= tile) floor = tile;
            int firstRow = Math.max(0, (int) Math.floor(y / tile));
            int lastRow = (int) Math.floor(meal.position.y / tile);
            int firstColumn = (int) Math.floor((x + BUBBLE_TERRAIN_EDGE_EPSILON) / tile);
            int lastColumn = (int) Math.floor((x + meal.dimension.x - BUBBLE_TERRAIN_EDGE_EPSILON) / tile);
            for (int row = firstRow; row <= lastRow; row++) {
                float top = (row + 1) * tile;
                if (top > meal.position.y || top < y) continue;
                for (int column = firstColumn; column <= lastColumn; column++) {
                    if (world.hasTile(column, row)) floor = Math.max(floor, top);
                }
            }
            if (floor != Float.NEGATIVE_INFINITY) {
                y = floor;
                meal.velocity.setZero();
                meal.setRenderRotation(0);
                landed = true;
            }
        }
        meal.setPosition(x, y);
    }

    @Override public String getCode() { return "STAND"; }
    @Override public void reset() {
        meal = null;
        world = null;
        pickupDelay = rotation = 0;
        landed = false;
    }
}
