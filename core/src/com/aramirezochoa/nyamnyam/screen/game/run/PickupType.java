package com.aramirezochoa.nyamnyam.screen.game.run;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Explicit debug/content IDs: never derive them from enum order or reuse a retired ID. */
public enum PickupType {
    TOOTH_NORMAL(1, false, 2, null, 0f),
    TOOTH_NORMAL_HALF(2, false, 1, null, 0f),
    TOOTH_CARIES(3, true, 2, null, 0f),
    TOOTH_CARIES_HALF(4, true, 1, null, 0f),
    POWER_UP(5, false, 0, RunStats.Stat.POWER, POWER_PICKUP_MODIFIER),
    CADENCE_UP(6, false, 0, RunStats.Stat.CADENCE, CADENCE_PICKUP_MODIFIER),
    SALIVATION_UP(7, false, 0, RunStats.Stat.SALIVATION, SALIVATION_PICKUP_MODIFIER);

    public final int id, halves;
    public final boolean caries;
    public final RunStats.Stat stat;
    public final float modifier;

    PickupType(int id, boolean caries, int halves, RunStats.Stat stat, float modifier) {
        this.id = id;
        this.caries = caries;
        this.halves = halves;
        this.stat = stat;
        this.modifier = modifier;
    }

    public boolean isStatUpgrade() { return stat != null; }

    public static PickupType fromId(int id) {
        for (PickupType type : values()) if (type.id == id) return type;
        return null;
    }
}
