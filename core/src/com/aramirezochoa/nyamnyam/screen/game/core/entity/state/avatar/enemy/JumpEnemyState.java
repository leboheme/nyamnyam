package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;

/**
 * Created by boheme on 28/01/15.
 */
public class JumpEnemyState extends EnemyState {

    private boolean jumped = false;
    private boolean move;

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateEntity(avatar, deltaTime, false);
        if (move) {
            if (DirectionType.RIGHT.equals(avatar.getLookingAt())) {
                avatar.velocity.x = getEnemy().getVelocity();
            } else {
                avatar.velocity.x = -getEnemy().getVelocity();
            }
        }
        if (!jumped && avatar.grounded) {
            avatar.velocity.y = avatar.getJumpVelocity();
            jumped = true;
            return false;
        }
        if (jumped && avatar.grounded) {
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        jumped = false;
    }

    public void setMove(boolean move) {
        this.move = move;
    }
}
