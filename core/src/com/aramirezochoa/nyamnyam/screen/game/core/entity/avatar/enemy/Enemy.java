package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy;

import com.badlogic.gdx.graphics.Color;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.GameplayContext;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/**
 * Created by boheme on 14/01/15.
 */
public abstract class Enemy extends Avatar {

    private float hitFeedbackTimer;
    private boolean angry, scared;
    private float initialResistance, resistance;

    @Override
    public void init(EntityType type, float x, float y, DirectionType lookingAt, GameplayContext gameWrapper) {
        super.init(type, x, y, lookingAt, gameWrapper);
        angry = false;
        scared = false;
        initialResistance = resistance = 0;
        hitFeedbackTimer = 0;
    }

    @Override
    public void remove() {
        super.remove();
        parent.getEntityWorld().removeEnemy(this);
    }

    @Override
    protected EntityState getDefaultIdleState() {
        return getInitialState();
    }

    /**
     * @return true if the enemy is dead, otherwise (eg: boss), it'll return false
     */
    public boolean hit(boolean transformToCandy) {
        if (transformToCandy) {
            parent.addMeal(EntityFactory.candy(this));
        } else {
            getParent().getMediaManager().playSound(MediaManager.SOUND_HIT_PROJECTILE);
            getParent().getGameStatus().addScore(getEatenScore(), position.x + (dimension.x / 2), position.y + dimension.y - Constant.SCREEN_GAME_Y_OFFSET);
            getParent().addMeal(EntityFactory.fruit(position.x + dimension.x / 2, position.y, MealType.randomFruit(), getParent()));
        }
        remove();
        return true;
    }

    public void captureInBubble() {
        hitFeedbackTimer = 0;
        parent.getEntityWorld().removeEnemy(this);
        setEnabled(false);
        velocity.setZero();
        acceleration.setZero();
        grounded = false;
    }

    public void escapeBubble(float x, float y, DirectionType facing) {
        setPosition(x, y);
        setLookingAt(facing);
        velocity.setZero();
        acceleration.setZero();
        grounded = false;
        setEnabled(true);
        parent.addEnemy(this);
    }

    public float getInitialResistance() { return initialResistance; }
    public float getResistance() { return resistance; }

    public void setInitialResistance(float value) {
        initialResistance = resistance = value;
    }

    public void reduceResistance(float power) {
        resistance = Math.max(0, resistance - power);
    }

    public void restoreResistance() {
        resistance = initialResistance;
    }

    /** Temporary visual feedback hook; does not interrupt locomotion or grant invulnerability. */
    public void showHitFeedback() {
        hitFeedbackTimer = ENEMY_HIT_FEEDBACK_SECONDS;
    }

    @Override
    protected void updateState(float deltaTime) {
        hitFeedbackTimer = Math.max(0, hitFeedbackTimer - deltaTime);
        super.updateState(deltaTime);
    }

    @Override
    protected void checkCollisions() {
        // nothing
    }

    @Override
    protected boolean isLoopAnimation() {
        return currentState.getCode().equals("WALK") || currentState.getCode().equals("STAND") || currentState.getCode().equals("THINK");
    }

    public void anger(boolean anger) {
        this.angry = anger;
    }

    @Override
    protected Color getColor() {
        if (hitFeedbackTimer > 0) {
            return Color.RED;
        } else if (scared) {
            return Constant.ENEMY_SCARED_COLOR;
        } else if (angry) {
            return Constant.ENEMY_ANGRY_COLOR;
        } else {
            return super.getColor();
        }
    }

    @Override
    public float getVelocity() {
        return getWalkVelocity() * (angry ? 1.5f : 1f);
    }

    protected float getWalkVelocity() {
        return Constant.ENEMY_WALK_VELOCITY;
    }

    public void setScared(boolean scared) {
        this.scared = scared;
    }

    @Override
    protected int getEatenScore() {
        return Constant.SCORE_ENEMY;
    }

    public boolean canFire() {
        return !scared;
    }

    public boolean isEdible() {
        return isEnabled() && scared;
    }

    public boolean isBoss() {
        return false;
    }

    @Override
    public void notifyWall() {
        super.notifyWall();
        lookingAt = lookingAt.flip();
    }
}
