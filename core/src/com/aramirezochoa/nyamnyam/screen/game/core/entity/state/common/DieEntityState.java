package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.common;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.Entity;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;

/**
 * Created by boheme on 3/02/15.
 */
public class DieEntityState extends EntityState {

    public static final String CODE = "DIE";
    private Entity target;

    @Override
    public boolean update(float deltaTime) {
        target.die();
        return true;
    }

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public void reset() {

    }

    public void setTarget(Entity target) {
        this.target = target;
    }
}
