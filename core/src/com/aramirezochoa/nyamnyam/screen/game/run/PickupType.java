package com.aramirezochoa.nyamnyam.screen.game.run;

/** Explicit debug/content IDs: never derive them from enum order or reuse a retired ID. */
public enum PickupType {
    TOOTH_NORMAL(1, false, 2),
    TOOTH_NORMAL_HALF(2, false, 1),
    TOOTH_CARIES(3, true, 2),
    TOOTH_CARIES_HALF(4, true, 1);

    public final int id, halves;
    public final boolean caries;

    PickupType(int id, boolean caries, int halves) {
        this.id = id;
        this.caries = caries;
        this.halves = halves;
    }

    public static PickupType fromId(int id) {
        for (PickupType type : values()) if (type.id == id) return type;
        return null;
    }
}
