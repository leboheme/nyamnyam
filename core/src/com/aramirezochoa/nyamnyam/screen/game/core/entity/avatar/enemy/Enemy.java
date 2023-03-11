package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy;

import com.badlogic.gdx.graphics.Color;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;

/**
 * Created by boheme on 14/01/15.
 */
public abstract class Enemy extends Avatar {

    private boolean angry, scared;

    @Override
    public void init(EntityType type, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper) {
        super.init(type, x, y, lookingAt, gameWrapper);
        angry = false;
        scared = false;
    }

    @Override
    public void remove() {
        super.remove();
        parent.enemies.removeValue(this, true);
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
        if (scared) {
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
