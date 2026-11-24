package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.CollisionWorld;
import com.aramirezochoa.nyamnyam.screen.game.gui.ToothShape;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Polygon placeholder owned by RunDropService; no textures or pooled entity references. */
public class ToothPickup implements RunPickup {
    public final PickupType type;
    public final Rectangle box = new Rectangle();
    private final Vector2 position, velocity;
    private final DropFlight flight;
    private float pickupDelay = DROP_PICKUP_DELAY_SECONDS;

    public ToothPickup(PickupType type, float x, float y, float direction, CollisionWorld world) {
        this.type = type;
        position = new Vector2(x, y);
        velocity = new Vector2(direction * DROP_HORIZONTAL_SPEED, DROP_UPWARD_SPEED);
        flight = new DropFlight(position, velocity, TOOTH_PICKUP_SIZE, TOOTH_PICKUP_SIZE, world, true);
        box.set(x, y, TOOTH_PICKUP_SIZE, TOOTH_PICKUP_SIZE);
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
        return type.caries ? runtime.getToothHealth().addCaries(type.halves)
                : runtime.getToothHealth().restoreNormal(type.halves);
    }

    @Override
    public void render(ShapeRenderer shapes) {
        ToothShape.draw(shapes, position.x, position.y, TOOTH_PICKUP_SIZE, type.halves, type.caries, flight.getRotation());
    }
}
