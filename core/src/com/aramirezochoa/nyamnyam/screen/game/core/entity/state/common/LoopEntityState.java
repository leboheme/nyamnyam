package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.common;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;

/**
 * Created by boheme on 15/01/15.
 */
public class LoopEntityState extends EntityState {

    private EntityState state;

    public void setState(EntityState state) {
        this.state = state;
    }

    @Override
    public void init() {
        super.init();
        state.init();
    }

    @Override
    public boolean update(float deltaTime) {
        if (state.update(deltaTime)) {
            state.restart();
            state.init();
        }
        return false;
    }

    @Override
    public String getCode() {
        return state.getCode();
    }

    @Override
    public void free() {
        super.free();
        state.free();
    }

    @Override
    public void reset() {

    }

}
