package com.aramirezochoa.nyamnyam.screen.game.core;

/** Tile collision queries shared by legacy TMX and runtime room geometry. */
public interface CollisionWorld {
    int getTileSize();
    float getWidth();
    float getHeight();
    boolean hasTile(int x, int y);
    TileType getTileType(int x, int y);
    boolean isEnclosed();

    /** Containment is separate from terrain tiles; legacy TMX keeps its existing edge rules. */
    default boolean containsBounds(float x, float y, float width, float height) { return true; }
}
