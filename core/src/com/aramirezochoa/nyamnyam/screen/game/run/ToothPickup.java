package com.aramirezochoa.nyamnyam.screen.game.run;

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
    private float rotation;

    public ToothPickup(PickupType type, float x, float y) {
        this.type = type;
        position = new Vector2(x, y);
        velocity = new Vector2();
        box.set(x, y, TOOTH_PICKUP_SIZE, TOOTH_PICKUP_SIZE);
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
        return type.caries ? runtime.getToothHealth().addCaries(type.halves)
                : runtime.getToothHealth().restoreNormal(type.halves);
    }

    @Override
    public void render(ShapeRenderer shapes) {
        ToothShape.draw(shapes, position.x, position.y, TOOTH_PICKUP_SIZE, type.halves, type.caries, rotation);
    }
}
