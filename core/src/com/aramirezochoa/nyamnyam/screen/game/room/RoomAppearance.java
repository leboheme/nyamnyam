package com.aramirezochoa.nyamnyam.screen.game.room;

/** Temporary fixed tile-sheet assignment used by the first JSON room renderer. */
public enum RoomAppearance {
    STATIC_TILES(112);

    private final int tileIndex;

    RoomAppearance(int tileIndex) {
        this.tileIndex = tileIndex;
    }

    public int getTileIndex() {
        return tileIndex;
    }
}
