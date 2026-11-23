package com.aramirezochoa.nyamnyam.screen.game.run;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Nyam's run resource; provisional values for the JSON prototype. */
public class SalivationReserve {

    private float current = SALIVATION_CAPACITY;

    public float getCurrent() { return current; }
    public float getCapacity() { return SALIVATION_CAPACITY; }

    public boolean canSpend() {
        return current >= SALIVATION_BUBBLE_COST;
    }

    public boolean trySpend() {
        if (!canSpend()) return false;
        current -= SALIVATION_BUBBLE_COST;
        return true;
    }

    public void update(float deltaTime) {
        current = Math.min(SALIVATION_CAPACITY, current + SALIVATION_REGENERATION_PER_SECOND * deltaTime);
    }

    public void restore(float amount) {
        current = Math.min(SALIVATION_CAPACITY, current + amount);
    }

    public void reset() {
        current = SALIVATION_CAPACITY;
    }
}
