package com.aramirezochoa.nyamnyam.screen.game.core;

/** Logical entity collision layers; these do not affect rendering order or tile collision. */
public enum CollisionLayer {
    NONE(0, 0, false),
    PLAYER(1, (1 << 1) | (1 << 2) | (1 << 3) | (1 << 4), false),
    ENEMY(1 << 1, 1 | (1 << 4), false),
    ANCHORED(1 << 2, 1 | (1 << 2), false),
    FREE(1 << 3, 1 | (1 << 3) | (1 << 4), true),
    BOMB(1 << 4, 1 | (1 << 1) | (1 << 3), false);

    private final int bit;
    private final int collisionMask;
    private final boolean affectedByGravity;

    CollisionLayer(int bit, int collisionMask, boolean affectedByGravity) {
        this.bit = bit;
        this.collisionMask = collisionMask;
        this.affectedByGravity = affectedByGravity;
    }

    public boolean collidesWith(CollisionLayer other) {
        return other != null && (collisionMask & other.bit) != 0
                && (other.collisionMask & bit) != 0;
    }

    public static boolean canCollide(CollisionLayer first, CollisionLayer second) {
        return first != null && first.collidesWith(second);
    }

    public boolean isAffectedByGravity() { return affectedByGravity; }

}
