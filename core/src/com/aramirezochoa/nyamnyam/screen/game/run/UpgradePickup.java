package com.aramirezochoa.nyamnyam.screen.game.run;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.aramirezochoa.nyamnyam.screen.game.gui.ToothShape;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Debug-only stat or mutation pickup with a distinct geometric placeholder. */
public final class UpgradePickup implements RunPickup {
    private static final Color POWER_COLOR = Color.valueOf("ff795e");
    private static final Color CADENCE_COLOR = Color.valueOf("f4d35e");
    private static final Color SALIVATION_COLOR = Color.valueOf("64d8e5");
    private static final Color GIANT_BUBBLE_COLOR = Color.valueOf("be8cff");
    private static final Color PROLONGED_CAPTURE_COLOR = Color.valueOf("9bdf8a");

    private final PickupType type;
    private final Rectangle box = new Rectangle();
    private final Vector2 position, velocity;
    private float rotation;

    public UpgradePickup(PickupType type, float x, float y) {
        if (type == null || !type.isUpgrade()) throw new IllegalArgumentException("Upgrade pickup type is required");
        this.type = type;
        position = new Vector2(x, y);
        velocity = new Vector2();
        box.set(x, y, STAT_PICKUP_SIZE, STAT_PICKUP_SIZE);
    }

    @Override public Rectangle getBox() { return box; }
    @Override public Vector2 getVelocity() { return velocity; }
    @Override public void setPosition(float x, float y) {
        position.set(x, y);
        box.setPosition(x, y);
    }
    @Override public float getCollisionRotation() { return rotation; }
    @Override public void setCollisionRotation(float degrees) { rotation = degrees; }
    @Override public boolean canCollect() { return true; }

    @Override
    public boolean apply(RunRuntime runtime) {
        if (type.mutation != null) return runtime.addMutation(type.mutation);
        runtime.getRunStats().addModifier(type.stat, type.modifier);
        return true;
    }

    @Override
    public void render(ShapeRenderer shapes) {
        draw(shapes, type, position.x, position.y, rotation);
    }

    public static void draw(ShapeRenderer shapes, PickupType type, float x, float y, float rotation) {
        float size = STAT_PICKUP_SIZE;
        float centerX = x + size / 2, centerY = y + size / 2;
        ToothShape.draw(shapes, x, y, size, 0, false, rotation);
        if (type.mutation == RunMutation.GIANT_BUBBLE) {
            shapes.setColor(GIANT_BUBBLE_COLOR);
            shapes.circle(centerX, centerY, 5f);
            shapes.setColor(Color.WHITE);
            shapes.circle(centerX, centerY, 2f);
            return;
        }
        if (type.mutation == RunMutation.PROLONGED_CAPTURE) {
            shapes.setColor(PROLONGED_CAPTURE_COLOR);
            shapes.circle(centerX, centerY, 5f);
            shapes.setColor(Color.WHITE);
            shapes.rect(centerX - 0.5f, centerY - 3f, 1f, 6f);
            shapes.rect(centerX, centerY, 3f, 1f);
            return;
        }
        switch (type.stat) {
            case POWER:
                shapes.setColor(POWER_COLOR);
                triangle(shapes, centerX, centerY, rotation, 0, 5, -4, -3, 4, -3);
                triangle(shapes, centerX, centerY, rotation, 0, -4, -3, 2, 3, 2);
                break;
            case CADENCE:
                shapes.setColor(CADENCE_COLOR);
                triangle(shapes, centerX, centerY, rotation, -5, 3, -1, 0, -5, -3);
                triangle(shapes, centerX, centerY, rotation, 0, 3, 4, 0, 0, -3);
                break;
            case SALIVATION:
            {
                shapes.setColor(SALIVATION_COLOR);
                float circleX = rotateX(centerX, centerY, rotation, 0, 1);
                float circleY = rotateY(centerX, centerY, rotation, 0, 1);
                shapes.circle(circleX, circleY, 3f);
                triangle(shapes, centerX, centerY, rotation, 0, 6, -3, 1, 3, 1);
                break;
            }
            default:
                break;
        }
    }

    private static void triangle(ShapeRenderer shapes, float cx, float cy, float angle,
                                 float x1, float y1, float x2, float y2, float x3, float y3) {
        shapes.triangle(rotateX(cx, cy, angle, x1, y1), rotateY(cx, cy, angle, x1, y1),
                rotateX(cx, cy, angle, x2, y2), rotateY(cx, cy, angle, x2, y2),
                rotateX(cx, cy, angle, x3, y3), rotateY(cx, cy, angle, x3, y3));
    }

    private static float rotateX(float cx, float cy, float angle, float x, float y) {
        float radians = angle * MathUtils.degreesToRadians;
        return cx + x * MathUtils.cos(radians) - y * MathUtils.sin(radians);
    }

    private static float rotateY(float cx, float cy, float angle, float x, float y) {
        float radians = angle * MathUtils.degreesToRadians;
        return cy + x * MathUtils.sin(radians) + y * MathUtils.cos(radians);
    }
}
