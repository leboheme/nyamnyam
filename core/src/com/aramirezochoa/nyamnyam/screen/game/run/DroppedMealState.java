package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;

/** Persistent room food animation; movement belongs to RunPhysicsSystem. */
public class DroppedMealState extends EntityState {
    @Override public boolean update(float deltaTime) { return false; }
    @Override public String getCode() { return "STAND"; }
    @Override public void reset() { }
}
