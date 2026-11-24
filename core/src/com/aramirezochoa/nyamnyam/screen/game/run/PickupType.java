package com.aramirezochoa.nyamnyam.screen.game.run;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;
import com.aramirezochoa.nyamnyam.i18n.LanguageManager;

/** Explicit debug/content IDs: never derive them from enum order or reuse a retired ID. */
public enum PickupType {
    TOOTH_NORMAL(1, false, 2, null, 0f),
    TOOTH_NORMAL_HALF(2, false, 1, null, 0f),
    TOOTH_CARIES(3, true, 2, null, 0f),
    TOOTH_CARIES_HALF(4, true, 1, null, 0f),
    POWER_UP(5, false, 0, RunStats.Stat.POWER, POWER_PICKUP_MODIFIER),
    CADENCE_UP(6, false, 0, RunStats.Stat.CADENCE, CADENCE_PICKUP_MODIFIER),
    SALIVATION_UP(7, false, 0, RunStats.Stat.SALIVATION, SALIVATION_PICKUP_MODIFIER),
    GIANT_BUBBLE(8, RunMutation.GIANT_BUBBLE, "pickupGiantBubble"),
    PROLONGED_CAPTURE(9, RunMutation.PROLONGED_CAPTURE, "pickupProlongedCapture");

    public final int id, halves;
    public final boolean caries;
    public final RunStats.Stat stat;
    public final float modifier;
    public final RunMutation mutation;
    private final String displayNameKey;

    PickupType(int id, boolean caries, int halves, RunStats.Stat stat, float modifier) {
        this.id = id;
        this.caries = caries;
        this.halves = halves;
        this.stat = stat;
        this.modifier = modifier;
        this.mutation = null;
        this.displayNameKey = null;
    }

    PickupType(int id, RunMutation mutation, String displayNameKey) {
        this.id = id;
        this.caries = false;
        this.halves = 0;
        this.stat = null;
        this.modifier = 0f;
        this.mutation = mutation;
        this.displayNameKey = displayNameKey;
    }

    public boolean isUpgrade() { return stat != null || mutation != null; }
    public String getDisplayName() {
        return displayNameKey == null ? name() : LanguageManager.INSTANCE.getString(displayNameKey);
    }

    public static PickupType fromId(int id) {
        for (PickupType type : values()) if (type.id == id) return type;
        return null;
    }
}
