package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.BombProjectile;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.Projectile;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.main.MainAvatarStateFactory;

/**
 * Created by leboheme on 11/03/15.
 */
public class TflyAvatar extends MainAvatar {

    @Override
    protected boolean isLoopAnimation() {
        return currentState.getCode().equals("WALK")
                || currentState.getCode().equals("STAND")
                || currentState.getCode().equals("FALL");
    }

    @Override
    public void left() {
        if (canPerformAction()) {
            velocity.x = -(Constant.AVATAR_WALK_VELOCITY * (getParent().getGameStatus().isMovementBoost() ? Constant.MAIN_AVATAR_BOOST_MOVEMENT : 1f));
            lookingAt = DirectionType.LEFT;
        }
    }

    @Override
    public void right() {
        if (canPerformAction()) {
            velocity.x = (Constant.AVATAR_WALK_VELOCITY * (getParent().getGameStatus().isMovementBoost() ? Constant.MAIN_AVATAR_BOOST_MOVEMENT : 1f));
            lookingAt = DirectionType.RIGHT;
        }
    }

    @Override
    public void fire() {
        if (canPerformAction()) {
            if (fireEnabled) {
                currentState = MainAvatarStateFactory.fire(this);
            }
        }
    }

    @Override
    protected void createNormalFire() {
        getParent().getMediaManager().playSound(MediaManager.SOUND_SHOT);
        Projectile bomb = EntityFactory.bomb(this, getLookingAt());
        if (DirectionType.RIGHT.equals(getLookingAt())) {
            bomb.velocity.x = Constant.TFLY_BOMB_VELOCITY.x + velocity.x;
        } else {
            bomb.velocity.x = -Constant.TFLY_BOMB_VELOCITY.x + velocity.x;
        }
        bomb.velocity.y = velocity.y == 0 ? Constant.TFLY_BOMB_VELOCITY.y : Constant.TFLY_BOMB_VELOCITY.y + 50;
        ((BombProjectile) bomb).setExplosionTimer(getParent().getGameStatus().isFireVelocityBoost() ? Constant.TFLY_EXPLOSION_TIMER * 2 : Constant.TFLY_EXPLOSION_TIMER);
        ((BombProjectile) bomb).setBigExplosion(getParent().getGameStatus().isFireDistanceBoost());
        getParent().addProjectile(bomb);
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
                    createJump();
                    secondJump = true;
                }
            }
        }
    }
}
