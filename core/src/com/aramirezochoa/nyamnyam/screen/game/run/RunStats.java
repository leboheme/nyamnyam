package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.Constant;

import java.util.EnumMap;
import java.util.Map;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.ATTACK_POWER;
import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.SALIVATION_REGENERATION_PER_SECOND;

/** Additive percentage modifiers for the small set of run stats in the prototype. */
public final class RunStats {
    public enum Stat {
        POWER,
        CADENCE,
        SALIVATION
    }

    private final Map<Stat, Float> modifiers = new EnumMap<Stat, Float>(Stat.class);

    /** Adds a fraction of the base value; for example, 0.25f adds 25%. */
    public void addModifier(Stat stat, float fraction) {
        if (stat == null || Float.isNaN(fraction) || Float.isInfinite(fraction)) {
            throw new IllegalArgumentException("A finite stat modifier is required");
        }
        float combined = getModifier(stat) + fraction;
        if (stat != Stat.SALIVATION && combined <= -1f) {
            throw new IllegalArgumentException("Power and Cadence must remain positive");
        }
        modifiers.put(stat, combined);
    }

    public float getModifier(Stat stat) {
        Float modifier = modifiers.get(stat);
        return modifier == null ? 0f : modifier;
    }

    /** Applies the accumulated modifier once to its base; values cannot become negative. */
    public float getValue(Stat stat, float baseValue) {
        return Math.max(0f, baseValue * (1f + getModifier(stat)));
    }

    public float getPower() {
        return getValue(Stat.POWER, ATTACK_POWER);
    }

    public float getCadence() {
        return getValue(Stat.CADENCE, 1f / Constant.MAIN_AVATAR_FIRE_TIME_LAPSE);
    }

    public float getSalivation() {
        return getValue(Stat.SALIVATION, SALIVATION_REGENERATION_PER_SECOND);
    }

    public void reset() {
        modifiers.clear();
    }
}
