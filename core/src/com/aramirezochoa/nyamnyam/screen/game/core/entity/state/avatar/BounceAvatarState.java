package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;

/**
 * Created by boheme on 26/01/15.
 */
public class BounceAvatarState extends AvatarState {

    private static final String BOUNCE = "BOUNCE";
    private boolean bouncingToTop;
    private float timer, lastTimer = 10f;

    public void setTime(float time) {
        this.timer = time;
        this.lastTimer = time;
    }

    @Override
    public void setAvatar(Avatar avatar) {
        super.setAvatar(avatar);
    }

    @Override
    public boolean update(float deltaTime) {
        avatar.acceleration.y = -Constant.GRAVITY.y;
        GameWrapperAssistant.updateEntity(avatar, deltaTime, true);
        timer -= deltaTime;

            if (DirectionType.RIGHT.equals(avatar.getLookingAt())) {
                avatar.velocity.x = avatar.getVelocity();
            } else {
                avatar.velocity.x = -avatar.getVelocity();
            }
        if (avatar.velocity.y == 0) {
            if (avatar.grounded && timer < 0) {
                return true;
            }
            if (bouncingToTop) {
                avatar.velocity.y = -avatar.getVelocity();
            } else {
                avatar.velocity.y = avatar.getVelocity();
            }
            bouncingToTop = !bouncingToTop;
        }

        return false;
    }

    @Override
    public String getCode() {
        return BOUNCE;
    }

    @Override
    public void reset() {
        bouncingToTop = false;
        timer = lastTimer;
        avatar.setLookingAt(avatar.getLookingAt().flip());
        avatar.velocity.x = 0;
        avatar.velocity.y = 0;
    }
}
