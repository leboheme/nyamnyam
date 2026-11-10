package com.aramirezochoa.nyamnyam.screen.game.core;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/** Collision-grid adapter for the legacy TMX maps. */
public class TmxCollisionWorld implements CollisionWorld {

    private final TiledMapTileLayer ground;
    private final TiledMapTileLayer collision;

    public TmxCollisionWorld(TiledMap map) {
        ground = (TiledMapTileLayer) map.getLayers().get("ground");
        collision = (TiledMapTileLayer) map.getLayers().get("collision");
    }

    @Override public int getTileSize() { return ground.getTileWidth(); }
    @Override public float getWidth() { return ground.getWidth() * ground.getTileWidth(); }
    @Override public float getHeight() { return ground.getHeight() * ground.getTileHeight(); }

    @Override
    public boolean hasTile(int x, int y) {
        return collision.getCell(x, y) != null;
    }

    @Override
    public TileType getTileType(int x, int y) {
        return TileType.getTileType(collision.getCell(x, y));
    }

    @Override public boolean isEnclosed() { return false; }
}
