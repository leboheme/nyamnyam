package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.pet;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.AvatarState;

/**
 * Created by boheme on 2/02/15.
 */
public class RoundAvatarState extends AvatarState {

    private static final String ROUND = "ROUND";
    private Avatar center;

    private float offsetX, offsetY, sign;
    private boolean axisX;

    @Override
    public void init() {
        super.init();
        offsetX = -Constant.TFLY_MAX_OFFSET;
        offsetY = -Constant.TFLY_MAX_OFFSET;
        axisX = false;
        sign = 1;
    }

    @Override
    public boolean update(float deltaTime) {
        if (axisX) {
            offsetX += sign * Constant.TFLY_MOVE;
            if (offsetX < -Constant.TFLY_MAX_OFFSET) {
                axisX = false;
                offsetX = -Constant.TFLY_MAX_OFFSET;
                sign = 1;
            } else if (offsetX > Constant.TFLY_MAX_OFFSET) {
                axisX = false;
                offsetX = Constant.TFLY_MAX_OFFSET;
                sign = -1;
            }
        } else {
            offsetY += sign * Constant.TFLY_MOVE;
            if (offsetY < -Constant.TFLY_MAX_OFFSET) {
                axisX = true;
                offsetY = -Constant.TFLY_MAX_OFFSET;
            } else if (offsetY > Constant.TFLY_MAX_OFFSET) {
                axisX = true;
                offsetY = Constant.TFLY_MAX_OFFSET;
            }
        }
        avatar.position.x = center.position.x + offsetX;
        avatar.position.y = center.position.y + offsetY;
        return false;
    }

    @Override
    public String getCode() {
        return ROUND;
    }

    @Override
    public void reset() {

    }

    public void setCenter(Avatar center) {
        this.center = center;
    }
}
