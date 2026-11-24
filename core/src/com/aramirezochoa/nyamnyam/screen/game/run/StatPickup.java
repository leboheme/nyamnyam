package com.aramirezochoa.nyamnyam.screen.game.run;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.aramirezochoa.nyamnyam.screen.game.core.CollisionWorld;
import com.aramirezochoa.nyamnyam.screen.game.gui.ToothShape;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Debug-only stat upgrade with a distinct geometric placeholder and shared drop flight. */
public final class StatPickup implements RunPickup {
    private static final Color POWER_COLOR = Color.valueOf("ff795e");
    private static final Color CADENCE_COLOR = Color.valueOf("f4d35e");
    private static final Color SALIVATION_COLOR = Color.valueOf("64d8e5");

    private final PickupType type;
    private final Rectangle box = new Rectangle();
    private final Vector2 position, velocity;
    private final DropFlight flight;
    private float pickupDelay = DROP_PICKUP_DELAY_SECONDS;

    public StatPickup(PickupType type, float x, float y, float direction, CollisionWorld world) {
        if (type == null || !type.isStatUpgrade()) throw new IllegalArgumentException("Stat pickup type is required");
        this.type = type;
        position = new Vector2(x, y);
        velocity = new Vector2(direction * DROP_HORIZONTAL_SPEED, DROP_UPWARD_SPEED);
        flight = new DropFlight(position, velocity, STAT_PICKUP_SIZE, STAT_PICKUP_SIZE, world, true);
        box.set(x, y, STAT_PICKUP_SIZE, STAT_PICKUP_SIZE);
    }

    @Override public Rectangle getBox() { return box; }
    @Override public boolean canCollect() { return pickupDelay <= 0; }

    @Override
    public void update(float deltaTime) {
        pickupDelay -= deltaTime;
        flight.update(deltaTime);
        box.setPosition(position.x, position.y);
    }

    @Override
    public boolean apply(RunRuntime runtime) {
        runtime.getRunStats().addModifier(type.stat, type.modifier);
        return true;
    }

    @Override
    public void render(ShapeRenderer shapes) {
        float size = STAT_PICKUP_SIZE;
        float centerX = position.x + size / 2, centerY = position.y + size / 2;
        float rotation = flight.getRotation();
        ToothShape.draw(shapes, position.x, position.y, size, 0, false, rotation);
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
