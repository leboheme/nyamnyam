package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.EnemyType;
import java.util.EnumMap;

/** JSON drop profiles. All probabilities are fractions in [0, 1], not percentages. */
public final class EnemyDropConfig {
    // Remaining probability gives two pieces: defaults are 25% zero, 50% one, 25% two.
    public static final Profile DEFAULT = new Profile(0.25f, 0.5f, 0.5f,
            PrototypeBalance.TOOTH_DROP_CHANCE, PrototypeBalance.TOOTH_DROP_CARIES_CHANCE, PrototypeBalance.TOOTH_DROP_HALF_CHANCE);
    // Add entries here to override a species without changing combat or pickup code.
    // Example in a static initializer: BY_ENEMY.put(EnemyType.BOOF, new Profile(0.25f, 0.5f, 0.5f, 0.2f, 0.5f, 0.5f));
    private static final EnumMap<EnemyType, Profile> BY_ENEMY = new EnumMap<EnemyType, Profile>(EnemyType.class);

    private EnemyDropConfig() { }

    public static Profile resolve(EnemyType type) {
        Profile profile = BY_ENEMY.get(type);
        return profile == null ? DEFAULT : profile;
    }

    public static final class Profile {
        public final float noDropChance, oneDropChance, fruitChance;
        // Independent extra tooth roll: chance to drop, conditional caries chance, conditional half chance.
        public final float toothDropChance, toothCariesChance, toothHalfChance;

        public Profile(float noDropChance, float oneDropChance, float fruitChance,
                       float toothDropChance, float toothCariesChance, float toothHalfChance) {
            if (!(noDropChance >= 0 && oneDropChance >= 0 && noDropChance + oneDropChance <= 1
                    && fruitChance >= 0 && fruitChance <= 1 && toothDropChance >= 0 && toothDropChance <= 1
                    && toothCariesChance >= 0 && toothCariesChance <= 1 && toothHalfChance >= 0 && toothHalfChance <= 1)) {
                throw new IllegalArgumentException("Invalid drop profile probabilities");
            }
            this.noDropChance = noDropChance;
            this.oneDropChance = oneDropChance;
            this.fruitChance = fruitChance;
            this.toothDropChance = toothDropChance;
            this.toothCariesChance = toothCariesChance;
            this.toothHalfChance = toothHalfChance;
        }
    }
}
