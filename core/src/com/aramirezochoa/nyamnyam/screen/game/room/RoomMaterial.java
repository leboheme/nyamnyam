package com.aramirezochoa.nyamnyam.screen.game.room;

import com.aramirezochoa.nyamnyam.screen.game.core.TileType;

/** Semantic cell materials used by authored room data. */
public enum RoomMaterial {
    FLOOR {
        @Override
        public TileType getCollisionType(boolean covered, boolean left, boolean right) {
            if (covered) {
                return TileType.NO_BLOCK;
            }
            if (left && right) {
                return TileType.TOP;
            }
            if (right) {
                return TileType.TOP_LEFT;
            }
            if (left) {
                return TileType.TOP_RIGHT;
            }
            return TileType.TOP_BOTH;
        }
    };

    public abstract TileType getCollisionType(boolean covered, boolean left, boolean right);
}
