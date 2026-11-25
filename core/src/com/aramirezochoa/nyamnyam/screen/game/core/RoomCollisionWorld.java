package com.aramirezochoa.nyamnyam.screen.game.core;

import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;

/** Collision-grid adapter for a room, with a one-tile implicit wall margin. */
public class RoomCollisionWorld implements CollisionWorld {
    private static final float EDGE_EPSILON = 0.01f;

    private final RoomInstance room;

    public RoomCollisionWorld(RoomInstance room) {
        this.room = room;
    }

    @Override public int getTileSize() { return room.getTileSize(); }
    @Override public float getWidth() { return (room.getWidth() + 2) * room.getTileSize(); }
    @Override public float getHeight() { return (room.getHeight() + 2) * room.getTileSize(); }

    @Override
    public boolean hasTile(int x, int y) {
        return x > 0 && x <= room.getWidth() && y > 0 && y <= room.getHeight()
                && room.getMaterialAt(x - 1, room.getHeight() - y) != null;
    }

    @Override
    public TileType getTileType(int x, int y) {
        if (x <= 0 || x > room.getWidth() || y <= 0 || y > room.getHeight()) {
            return TileType.NO_BLOCK;
        }
        return room.getCollisionTypeAt(x - 1, room.getHeight() - y);
    }

    @Override public boolean isEnclosed() { return true; }

    @Override
    public boolean containsBounds(float x, float y, float width, float height) {
        int tile = getTileSize();
        if (x < tile || y < tile || x + width > getWidth() - tile
                || y + height > getHeight() - tile) return false;
        int firstX = (int) Math.floor((x + EDGE_EPSILON) / tile) - 1;
        int lastX = (int) Math.floor((x + width - EDGE_EPSILON) / tile) - 1;
        int firstY = room.getHeight() - (int) Math.floor((y + height - EDGE_EPSILON) / tile);
        int lastY = room.getHeight() - (int) Math.floor((y + EDGE_EPSILON) / tile);
        for (int row = firstY; row <= lastY; row++) {
            for (int col = firstX; col <= lastX; col++) {
                if (!room.isPlayableAt(col, row)) return false;
            }
        }
        return true;
    }
}
