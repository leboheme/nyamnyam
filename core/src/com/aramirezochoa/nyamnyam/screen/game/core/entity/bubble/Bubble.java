package com.aramirezochoa.nyamnyam.screen.game.core.entity.bubble;

import com.aramirezochoa.nyamnyam.Constant;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.Entity;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.badlogic.gdx.math.Vector2;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.GameplayContext;

/** Bubble used by the JSON room runtime, optionally carrying an enemy. */
public class Bubble extends Entity {

    private enum BubbleType implements EntityType {
        BUBBLE;
        @Override public Vector2 getDimension() { return Constant.CANDY_DIMENSION; }
    }

    private final FloatingState floatingState = new FloatingState();
    private static final float MAX_PUSH_SPEED = 200f;
    private static final float PRESSURE_TIME = 0.65f;
    private static final float BURST_IMPACT_SPEED = 280f;
    private Enemy capturedEnemy;
    private float pressure;
    private boolean burstRequested;
    private final Vector2 impulse = new Vector2();

    public void init(float x, float y, DirectionType lookingAt, GameplayContext context) {
        impulse.setZero();
        pressure = 0;
        burstRequested = false;
        super.init(BubbleType.BUBBLE, x, y, lookingAt, context);
    }

    @Override
    protected EntityState getInitialState() {
        floatingState.reset();
        return floatingState;
    }

    // Reuse the existing AIR atlas frames without identifying the entity as food.
    @Override protected String getStatusCode() { return "AIR_" + currentState.getCode(); }

    @Override protected void checkCollisions() { }
    @Override public void dampVelocity() { }
    @Override protected boolean isLoopAnimation() { return true; }

    public void push(float x, float y) {
        impulse.set(x, y).limit(MAX_PUSH_SPEED);
    }

    public boolean isBurstRequested() { return burstRequested; }

    public void relaxPressure(float deltaTime) {
        pressure = Math.max(0, pressure - deltaTime);
    }

    public void press(float deltaTime, float impactSpeed, boolean blocked) {
        // Restores this frame's relaxation and then accumulates sustained pressure.
        pressure += deltaTime * (blocked ? 5 : 2);
        if (impactSpeed >= BURST_IMPACT_SPEED || pressure >= PRESSURE_TIME) burst();
    }

    private class FloatingState extends EntityState {
        private float elapsed;

        @Override public boolean update(float deltaTime) {
            if (burstRequested) return false;
            elapsed += deltaTime;
            if (elapsed >= Constant.MEAL_RAISE_TIME_1 + Constant.MEAL_RAISE_TIME_2 + Constant.MEAL_RAISE_TIME_3) {
                expire();
                return false;
            }
            acceleration.set(0, -Constant.GRAVITY.y);
            parent.getBubbleAttractionService().setVelocity(getId(), position.x + dimension.x / 2,
                    position.y + dimension.y / 2, velocity);
            velocity.add(impulse).limit(MAX_PUSH_SPEED);
            GameWrapperAssistant.updateEntity(Bubble.this, deltaTime, true,
                    parent.getBubbleAttractionService().getBaseSpeed());
            impulse.scl(Math.max(0, 1 - 6 * deltaTime));
            return false;
        }

        @Override public String getCode() {
            return elapsed < Constant.MEAL_RAISE_TIME_1 ? "RAISE_COLD"
                    : elapsed < Constant.MEAL_RAISE_TIME_1 + Constant.MEAL_RAISE_TIME_2 ? "RAISE_WARM" : "RAISE_HOT";
        }

        @Override public void reset() { elapsed = 0; }
    }

    public boolean capture(Enemy enemy) {
        if (capturedEnemy != null || !isEnabled() || !enemy.isEnabled() || enemy.isBoss()) {
            return false;
        }
        capturedEnemy = enemy;
        enemy.captureInBubble();
        updateCapturedPosition();
        return true;
    }

    public boolean hasCapturedEnemy() {
        return capturedEnemy != null;
    }

    @Override
    protected void updateBoxes() {
        super.updateBoxes();
        updateCapturedPosition();
    }

    @Override
    public void render(Batch batch) {
        super.render(batch);
        if (capturedEnemy != null) {
            capturedEnemy.render(batch);
        }
    }

    /** Any genuine burst is propagated by the runtime, irrespective of its source. */
    public void burst() {
        if (isEnabled()) burstRequested = true;
    }

    public void resolveBurst() {
        if (!isEnabled()) return;
        setEnabled(false);
        burstRequested = false;
        Enemy enemy = capturedEnemy;
        capturedEnemy = null;
        if (enemy != null) enemy.defeatInBubble();
        setCurrentState(EntityStateFactory.explode(this, false));
    }

    private void expire() {
        setEnabled(false);
        releaseCapturedEnemy();
        die();
    }

    @Override
    public void remove() {
        parent.getEntityWorld().removeBubble(this);
        releaseCapturedEnemy();
        super.remove();
    }

    @Override
    public void reset() {
        if (capturedEnemy != null) {
            capturedEnemy.free();
            capturedEnemy = null;
        }
        super.reset();
    }

    private void releaseCapturedEnemy() {
        if (capturedEnemy != null) {
            Enemy enemy = capturedEnemy;
            capturedEnemy = null;
            enemy.escapeBubble(position.x + (dimension.x - enemy.dimension.x) / 2,
                    position.y + (dimension.y - enemy.dimension.y) / 2, getLookingAt());
        }
    }

    private void updateCapturedPosition() {
        if (capturedEnemy != null) {
            capturedEnemy.setPosition(position.x + (dimension.x - capturedEnemy.dimension.x) / 2,
                    position.y + (dimension.y - capturedEnemy.dimension.y) / 2);
        }
    }
}
