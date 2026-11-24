package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.CollisionWorld;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.gui.ToothShape;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Visual reward in transit. Contains spawn data, never a live entity or physics body. */
final class DropLaunch {
    final MealType mealType;
    final PickupType pickupType;
    private final TextureRegion image;
    final float size;
    float x, y;
    private float velocityX, velocityY, rotation;

    DropLaunch(MealType mealType, PickupType pickupType, TextureRegion image,
               float size, float x, float y, float vx, float vy) {
        this.mealType = mealType;
        this.pickupType = pickupType;
        this.image = image;
        this.size = size;
        this.x = x;
        this.y = y;
        velocityX = vx;
        velocityY = vy;
    }

    boolean update(float delta, CollisionWorld world) {
        velocityY -= ROOM_FREE_ENTITY_GRAVITY * delta;
        int steps = Math.max(1, (int)Math.ceil(Math.max(Math.abs(velocityX), Math.abs(velocityY))
                * delta / ROOM_PHYSICS_MAX_MOVEMENT_STEP));
        float dt = delta / steps;
        int tile = world.getTileSize();
        for (int i = 0; i < steps; i++) {
            float nx = x + velocityX * dt, ny = y + velocityY * dt;
            float right = world.getWidth() - tile - size;
            if (nx < tile || nx > right) {
                nx = Math.max(tile, Math.min(right, nx));
                velocityX = -velocityX * DROP_ROOM_RESTITUTION;
            }
            float ceiling = world.getHeight() - tile - size;
            if (ny > ceiling) {
                ny = ceiling;
                velocityY = -Math.abs(velocityY) * DROP_ROOM_RESTITUTION;
            }
            if (velocityY < 0) {
                float floor = ny <= tile ? tile : Float.NEGATIVE_INFINITY;
                for (int row = Math.max(0, (int)Math.floor(ny / tile) - 1);
                     row <= (int)Math.floor(y / tile); row++) {
                    float top = (row + 1) * tile;
                    if (top > y + 0.001f || top < ny) continue;
                    for (int col = (int)Math.floor((nx + 0.001f) / tile);
                         col <= (int)Math.floor((nx + size - 0.001f) / tile); col++) {
                        if (world.hasTile(col, row)) floor = Math.max(floor, top);
                    }
                }
                if (floor != Float.NEGATIVE_INFINITY) {
                    x = nx;
                    y = floor;
                    return true;
                }
            }
            x = nx;
            y = ny;
        }
        rotation -= Math.signum(velocityX) * DROP_SPIN_DEGREES_PER_SECOND * delta;
        return false;
    }

    void render(Batch batch) {
        if (image != null) batch.draw(image, x, y, size / 2, size / 2, size, size, 1, 1, rotation);
    }

    void render(ShapeRenderer shapes) {
        if (pickupType == null) return;
        if (pickupType.isUpgrade()) UpgradePickup.draw(shapes, pickupType, x, y, rotation);
        else ToothShape.draw(shapes, x, y, size, pickupType.halves, pickupType.caries, rotation);
    }
}
