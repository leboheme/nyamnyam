package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.CollisionWorld;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Owns the defeated enemy while it flies without normal entity contacts. */
final class DefeatedEnemyFlight {
    final Enemy enemy;
    private final TextureRegion frame;
    private final CollisionWorld world;
    private float velocityX, velocityY = DEFEAT_BODY_UPWARD_SPEED, rotation;
    private float fadeElapsed;
    private boolean fading;

    DefeatedEnemyFlight(Enemy enemy, float direction, CollisionWorld world) {
        this.enemy = enemy;
        this.world = world;
        frame = enemy.copyCurrentFrame();
        if (enemy.getLookingAt() == DirectionType.LEFT) frame.flip(true, false);
        velocityX = direction * DEFEAT_BODY_HORIZONTAL_SPEED;
        enemy.beginDefeatedFlight();
    }

    /** Returns true only when the enemy's body lands on room terrain. */
    boolean update(float deltaTime) {
        velocityY -= DEFEAT_BODY_GRAVITY * deltaTime;
        int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(velocityX), Math.abs(velocityY))
                * deltaTime / ROOM_PHYSICS_MAX_MOVEMENT_STEP));
        float dt = deltaTime / steps;
        float x = enemy.position.x, y = enemy.position.y;
        float width = enemy.dimension.x, height = enemy.dimension.y;
        int tile = world.getTileSize();
        for (int i = 0; i < steps; i++) {
            float nextX = x + velocityX * dt;
            float right = world.getWidth() - tile - width;
            if (nextX < tile || nextX > right) {
                nextX = Math.max(tile, Math.min(right, nextX));
                velocityX = -velocityX * DROP_ROOM_RESTITUTION;
            }
            float nextY = y + velocityY * dt;
            float ceiling = world.getHeight() - tile - height;
            if (nextY > ceiling) {
                nextY = ceiling;
                velocityY = -Math.abs(velocityY) * DROP_ROOM_RESTITUTION;
            }
            if (velocityY < 0) {
                float floor = nextY <= tile ? tile : Float.NEGATIVE_INFINITY;
                for (int row = Math.max(0, (int) Math.floor(nextY / tile) - 1);
                     row <= (int) Math.floor(y / tile); row++) {
                    float top = (row + 1) * tile;
                    if (top > y + 0.001f || top < nextY) continue;
                    for (int col = (int) Math.floor((nextX + 0.001f) / tile);
                         col <= (int) Math.floor((nextX + width - 0.001f) / tile); col++) {
                        if (world.hasTile(col, row)) floor = Math.max(floor, top);
                    }
                }
                if (floor != Float.NEGATIVE_INFINITY) {
                    enemy.setPosition(nextX, floor);
                    return true;
                }
            }
            x = nextX;
            y = nextY;
        }
        rotation -= Math.signum(velocityX) * DEFEAT_BODY_SPIN_DEGREES_PER_SECOND * deltaTime;
        enemy.setPosition(x, y);
        enemy.setRenderRotation(rotation);
        return false;
    }

    void startDeathFade() {
        fading = true;
        velocityX = 0;
        velocityY = 0;
        rotation = 0;
        enemy.setRenderRotation(0);
    }

    boolean isFading() { return fading; }

    boolean updateDeathFade(float deltaTime) {
        fadeElapsed += deltaTime;
        return fadeElapsed >= DEFEAT_BODY_FADE_SECONDS;
    }

    void render(Batch batch) {
        if (fading) batch.setColor(1, 1, 1, Math.max(0, 1 - fadeElapsed / DEFEAT_BODY_FADE_SECONDS));
        float width = frame.getRegionWidth(), height = frame.getRegionHeight();
        float drawX = enemy.position.x - (width - enemy.dimension.x) / 2;
        batch.draw(frame, drawX, enemy.position.y, width / 2, height / 2,
                width, height, 1, 1, rotation);
        if (fading) batch.setColor(1, 1, 1, 1);
    }
}
