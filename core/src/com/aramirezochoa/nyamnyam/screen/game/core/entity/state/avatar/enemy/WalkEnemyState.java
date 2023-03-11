package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;

/**
 * Created by boheme on 15/01/15.
 */
public class WalkEnemyState extends EnemyState {

    private float timerValue = 5f;
    private float timer = timerValue;

    public void setTimerValue(float timerValue) {
        this.timerValue = timerValue;
    }

    public void init() {
        super.init();
        if (DirectionType.RIGHT.equals(avatar.getLookingAt())) {
            avatar.velocity.x = getEnemy().getVelocity();
        } else {
            avatar.velocity.x = -getEnemy().getVelocity();
        }
        timer = timerValue;
    }

    public void restart() {
        super.restart();
        init();
    }

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateEntity(getEnemy(), deltaTime, false);
        if (timer < 0) {
            if (avatar.getParent().mainAvatar.velocity.y == 0 && avatar.getParent().mainAvatar.position.y > avatar.position.y) {
                avatar.setCurrentState(EnemyStateFactory.sequence(EnemyStateFactory.think(avatar, 0.5f), EnemyStateFactory.jump(getEnemy(), false)));
                return false;
            }
            return true;
        }
        if (avatar.getParent().mainAvatar.position.y == avatar.position.y) {
            if (!GameWrapperAssistant.isNextStepGrounded(getEnemy(), getEnemy().getParent().getMap())) {
                if (avatar.velocity.y == 0) {
                    avatar.setCurrentState(EnemyStateFactory.jump(getEnemy(), true));
                    return false;
                }
            }
        }
        if (avatar.velocity.y == 0) {
            if (DirectionType.RIGHT.equals(avatar.getLookingAt())) {
                avatar.velocity.x = getEnemy().getVelocity();
            } else {
                avatar.velocity.x = -getEnemy().getVelocity();
            }
            timer -= deltaTime;
        }
        return false;
    }

    @Override
    public void reset() {
        timer = timerValue;
    }

}
