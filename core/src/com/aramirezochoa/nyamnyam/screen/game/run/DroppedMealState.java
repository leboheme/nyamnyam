package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.CollisionWorld;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Food wrapper for the same ballistic movement used by tooth pickups. */
public class DroppedMealState extends EntityState {
    private Meal meal;
    private DropFlight flight;
    private float pickupDelay;

    public void configure(Meal meal, CollisionWorld world, boolean launched) {
        this.meal = meal;
        flight = new DropFlight(meal.position, meal.velocity, meal.dimension.x, meal.dimension.y, world, launched);
        pickupDelay = launched ? DROP_PICKUP_DELAY_SECONDS : 0;
        meal.setRenderRotation(0);
        meal.setEnabled(!launched);
        meal.acceleration.setZero();
    }

    @Override public boolean update(float deltaTime) {
        if (pickupDelay > 0) {
            pickupDelay -= deltaTime;
            if (pickupDelay <= 0) meal.setEnabled(true);
        }
        flight.update(deltaTime);
        meal.setPosition(meal.position.x, meal.position.y);
        meal.setRenderRotation(flight.getRotation());
        return false;
    }

    @Override public String getCode() { return "STAND"; }
    @Override public void reset() {
        meal = null;
        flight = null;
        pickupDelay = 0;
    }
}
