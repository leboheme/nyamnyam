package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.common;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.Entity;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;

/**
 * Created by boheme on 15/01/15.
 */
public class IdleEntityState extends EntityState {

    private static final String JUMP = "JUMP";
    private static final String FALL = "FALL";
    private static final String WALK = "WALK";
    private static final String STAND = "STAND";

    protected Entity entity;
    private static final float NO_TIMER = -1;
    private float timer = NO_TIMER;

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateEntity(entity, deltaTime, false);

        if (timer != NO_TIMER) {
            if (timer < 0) {
                return true;
            }
            timer -= deltaTime;
        }
        return false;
    }

    @Override
    public String getCode() {
        if (entity.velocity.y > 0) {
            return JUMP;
        } else if (entity.velocity.y < 0) {
            return FALL;
        } else if (entity.velocity.x != 0) {
            return WALK;
        } else {
            return STAND;
        }
    }

    @Override
    public void reset() {
        timer = NO_TIMER;
    }

    public void setTime(float time) {
        this.timer = time;
    }
}
