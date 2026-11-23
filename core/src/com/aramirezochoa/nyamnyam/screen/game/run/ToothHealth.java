package com.aramirezochoa.nyamnyam.screen.game.run;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Run health measured in half teeth. Normal containers survive damage; caries do not. */
public class ToothHealth {
    private int containers, normalHalves, cariesHalves;

    public ToothHealth() { reset(); }
    public int getContainers() { return containers; }
    public int getNormalHalves() { return normalHalves; }
    public int getCariesHalves() { return cariesHalves; }
    public boolean isDefeated() { return normalHalves + cariesHalves == 0; }

    public void reset() {
        containers = STARTING_TOOTH_CONTAINERS;
        normalHalves = containers * 2;
        cariesHalves = 0;
    }

    public void damage(int halves) {
        int consumed = Math.min(cariesHalves, halves);
        cariesHalves -= consumed;
        normalHalves = Math.max(0, normalHalves - (halves - consumed));
    }

    /** Returns false when full, so the pickup remains in the room. Excess healing is discarded. */
    public boolean restoreNormal(int halves) {
        if (isDefeated() || normalHalves == containers * 2) return false;
        normalHalves = Math.min(containers * 2, normalHalves + halves);
        return true;
    }

    public boolean addCaries(int halves) {
        int capacity = (MAX_TOOTH_SPACES - containers) * 2;
        if (isDefeated() || cariesHalves == capacity) return false;
        cariesHalves = Math.min(capacity, cariesHalves + halves);
        return true;
    }
}
