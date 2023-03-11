package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.main.MainAvatarStateFactory;

/**
 * Created by leboheme on 10/03/15.
 */
public class NyamAvatar extends MainAvatar {

    @Override
    protected boolean isLoopAnimation() {
        return currentState.getCode().equals("WALK") || currentState.getCode().equals("STAND");
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
        getParent().addProjectile(EntityFactory.projectile(ProjectileType.BEAM, this, getLookingAt(), true, true));
    }

    @Override
    public boolean isAirSkilled() {
        return true;
    }
}
