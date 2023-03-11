package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.common;

import com.badlogic.gdx.utils.Array;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;

/**
 * Created by boheme on 15/01/15.
 */
public class SequenceEntityState extends EntityState {

    Array<EntityState> states = new Array(4);
    private int index;

    public void addState(EntityState state) {
        states.add(state);
    }

    @Override
    public void init() {
        super.init();
        states.get(0).init();
    }

    @Override
    public boolean update(float deltaTime) {
        if (index >= states.size) return true;

        if (states.get(index).update(deltaTime)) {
            if (states == null) return true;
            index++;
            if (index >= states.size) return true;
            states.get(index).init();
        }
        return false;
    }

    @Override
    public String getCode() {
        return states.get(index).getCode();
    }

    @Override
    public void free() {
        for (EntityState state : states) {
            state.free();
        }
        super.free();
    }

    @Override
    public void reset() {
        states.clear();
        restart();
    }

    public void restart() {
        index = 0;
        for (EntityState state : states) {
            state.restart();
        }
    }

}
