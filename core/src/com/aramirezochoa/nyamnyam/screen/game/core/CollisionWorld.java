package com.aramirezochoa.nyamnyam.screen.game.core;

/** Tile collision queries shared by legacy TMX and runtime room geometry. */
public interface CollisionWorld {
    int getTileSize();
    float getWidth();
    float getHeight();
    boolean hasTile(int x, int y);
    TileType getTileType(int x, int y);
    boolean isEnclosed();
}
