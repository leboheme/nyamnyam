package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.main.MainAvatarStateFactory;

/**
 * Created by leboheme on 10/03/15.
 */
public class GlutyAvatar extends MainAvatar {

    private float fireTimer;
    private boolean firing;

    @Override
    protected boolean isLoopAnimation() {
        return currentState.getCode().equals("WALK")
                || currentState.getCode().equals("STAND")
                || currentState.getCode().equals("FALL")
                || currentState.getCode().equals("FIRE");
    }

    @Override
    public void left() {
        if (canPerformAction()) {
            if (firing && getParent().getGameStatus().isFireDistanceBoost()) {
                velocity.x = -Constant.AVATAR_WALK_VELOCITY * Constant.GLUTY_BOOST_FIRE_DISTANCE;
            } else {
                velocity.x = -(Constant.AVATAR_WALK_VELOCITY * (getParent().getGameStatus().isMovementBoost() ? Constant.MAIN_AVATAR_BOOST_MOVEMENT : 1f));
            }
            lookingAt = DirectionType.LEFT;
        }
    }

    @Override
    public void right() {
        if (canPerformAction()) {
            if (firing && getParent().getGameStatus().isFireDistanceBoost()) {
                velocity.x = Constant.AVATAR_WALK_VELOCITY * Constant.GLUTY_BOOST_FIRE_DISTANCE;
            } else {
                velocity.x = (Constant.AVATAR_WALK_VELOCITY * (getParent().getGameStatus().isMovementBoost() ? Constant.MAIN_AVATAR_BOOST_MOVEMENT : 1f));
            }
            lookingAt = DirectionType.RIGHT;
        }
    }

    @Override
    public void fire() {
        if (canPerformAction() && fireEnabled) {
            fireTimer = Constant.GLUTY_WHIRL_TIME * (getParent().getGameStatus().isFireVelocityBoost() ? 2 : 1);
            firing = true;
            currentState = MainAvatarStateFactory.fire(this, fireTimer);
        }
    }

    @Override
    protected void createNormalFire() {
        getParent().getMediaManager().playSound(MediaManager.SOUND_GLUTY_WHIRL);
    }

    @Override
    public boolean isAirSkilled() {
        return false;
    }

    @Override
    public void jump() {
        if (canPerformAction()) {
            if (velocity.y == 0) {
                createJump();
            } else if (velocity.y < 0) {
                jumpIfPossible = true;
                if (!secondJump) {
                    if (fireEnabled) {
                        createJump();
                        fire();
                        secondJump = true;
                    }
                }
            }
        }
    }

    @Override
    protected void createJump() {
        getParent().getMediaManager().playSound(MediaManager.SOUND_JUMP);
        if (firing && getParent().getGameStatus().isFireDistanceBoost()) {
            velocity.y = getJumpVelocity() * Constant.GLUTY_BOOST_FIRE_DISTANCE;
        } else {
            velocity.y = getJumpVelocity();
        }
    }

    @Override
    public float getMaxPositiveVelocity() {
        return getJumpVelocity() * Constant.GLUTY_BOOST_FIRE_DISTANCE;
    }

    @Override
    protected void updateState(float deltaTime) {
        super.updateState(deltaTime);
        if (firing) {
            fireTimer -= deltaTime;
            if (fireTimer < 0) {
                firing = false;
            }
        }
    }

    @Override
    protected void checkCollisions() {
        if (canPerformAction()) {
            for (Meal meal : getParent().food) {
                if (meal.box.overlaps(box)) checkEdible(meal);
            }
            for (int i = 0; i < getParent().enemies.size; i++) {
                if (getParent().enemies.get(i).isEnabled()
                        && box.overlaps(getParent().enemies.get(i).box)) {
                    if (garlicBoost && getParent().enemies.get(i).isEdible()) {
                        eat(getParent().enemies.get(i));
                    } else if (firing) {
                        getParent().enemies.get(i).hit(false);
                    } else {
                        hit();
                    }
                }
            }
            jumpIfPossible = false;
        }
    }

}
