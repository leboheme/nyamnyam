package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.RoomCollisionWorld;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomConnector;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;
import com.badlogic.gdx.math.Vector2;

/** Finds clear player positions using room geometry, without authored arrival points. */
public final class RoomEntryResolver {
    private static final float EDGE_INSET = 1f;

    private RoomEntryResolver() { }

    public static Vector2 atConnector(RoomInstance room, RoomConnector connector, float width, float height) {
        RoomCollisionWorld world = new RoomCollisionWorld(room);
        float tile = room.getTileSize();
        float left = connector.getX() + tile;
        float bottom = connector.getY() + tile;
        char side = connector.getSlot().charAt(0);
        if (side == 'W' || side == 'E') {
            float x = side == 'W' ? left + EDGE_INSET : left - width - EDGE_INSET;
            for (float offset : new float[]{0, tile, 2 * tile, -tile}) {
                float y = bottom + offset;
                if (clear(world, x, y, width, height) && supported(world, x, y, width)) {
                    return new Vector2(x, y);
                }
            }
        } else {
            float x = left + (connector.getWidth() - width) / 2f;
            float y = side == 'N' ? bottom : bottom + connector.getHeight() - height;
            if (clear(world, x, y, width, height)) return new Vector2(x, y);
        }
        throw new IllegalStateException("No clear entry at connector " + connector.getSlot());
    }

    public static Vector2 initial(RoomInstance room, float width, float height) {
        RoomCollisionWorld world = new RoomCollisionWorld(room);
        float tile = room.getTileSize();
        Vector2 best = null;
        float bestScore = Float.POSITIVE_INFINITY;
        float targetX = world.getWidth() / 2f - width / 2f;
        float targetY = Math.min(world.getHeight() / 4f, 6 * tile);
        for (float y = tile; y + height <= world.getHeight() - tile; y += tile / 2f) {
            for (float x = tile; x + width <= world.getWidth() - tile; x += tile / 2f) {
                if (!clear(world, x, y, width, height) || !supported(world, x, y, width)) continue;
                float score = Math.abs(x - targetX) + Math.abs(y - targetY);
                if (score < bestScore) {
                    bestScore = score;
                    best = new Vector2(x, y);
                }
            }
        }
        if (best == null) throw new IllegalStateException("No clear initial spawn in modular room");
        return best;
    }

    private static boolean clear(RoomCollisionWorld world, float x, float y, float width, float height) {
        if (!world.containsBounds(x, y, width, height)) return false;
        int tile = world.getTileSize();
        int firstX = (int) Math.floor((x + EDGE_INSET / 2f) / tile);
        int lastX = (int) Math.floor((x + width - EDGE_INSET / 2f) / tile);
        int firstY = (int) Math.floor((y + EDGE_INSET / 2f) / tile);
        int lastY = (int) Math.floor((y + height - EDGE_INSET / 2f) / tile);
        for (int row = firstY; row <= lastY; row++) {
            for (int col = firstX; col <= lastX; col++) {
                if (world.hasTile(col, row)) return false;
            }
        }
        return true;
    }

    private static boolean supported(RoomCollisionWorld world, float x, float y, float width) {
        int tile = world.getTileSize();
        int underX = (int) Math.floor((x + width / 2f) / tile);
        int underY = (int) Math.floor((y - EDGE_INSET / 2f) / tile);
        return world.hasTile(underX, underY) || y == tile;
    }
}
