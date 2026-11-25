package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.CollisionWorld;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Terrain clearance for JSON bubble movement, shots and contact propagation. */
public class BubbleTerrain {
    private final CollisionWorld world;

    public BubbleTerrain(CollisionWorld world) {
        this.world = world;
    }

    public boolean canOccupy(float x, float y, float width, float height) {
        int tileSize = world.getTileSize();
        if (world.isEnclosed() && (x < tileSize - BUBBLE_TERRAIN_EDGE_EPSILON || y < tileSize - BUBBLE_TERRAIN_EDGE_EPSILON
                || x + width > world.getWidth() - tileSize + BUBBLE_TERRAIN_EDGE_EPSILON
                || y + height > world.getHeight() - tileSize + BUBBLE_TERRAIN_EDGE_EPSILON)) return false;
        if (!world.containsBounds(x, y, width, height)) return false;
        int left = (int) Math.floor((x + BUBBLE_TERRAIN_EDGE_EPSILON) / tileSize);
        int right = (int) Math.floor((x + width - BUBBLE_TERRAIN_EDGE_EPSILON) / tileSize);
        int bottom = (int) Math.floor((y + BUBBLE_TERRAIN_EDGE_EPSILON) / tileSize);
        int top = (int) Math.floor((y + height - BUBBLE_TERRAIN_EDGE_EPSILON) / tileSize);
        for (int tx = left; tx <= right; tx++) {
            for (int ty = bottom; ty <= top; ty++) {
                if (world.hasTile(tx, ty)) return false;
            }
        }
        return true;
    }

    public boolean canTravel(float x, float y, float width, float height, float dx, float dy) {
        // Each sample advances at most half a tile, including the initial footprint.
        int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(dx), Math.abs(dy)) / (world.getTileSize() / 2f)));
        for (int i = 0; i <= steps; i++) {
            float fraction = i / (float) steps;
            if (!canOccupy(x + dx * fraction, y + dy * fraction, width, height)) return false;
        }
        return true;
    }

    public boolean connects(float x0, float y0, float x1, float y1) {
        int tileSize = world.getTileSize();
        int left = (int) Math.floor(Math.min(x0, x1) / tileSize);
        int right = (int) Math.floor(Math.max(x0, x1) / tileSize);
        int bottom = (int) Math.floor(Math.min(y0, y1) / tileSize);
        int top = (int) Math.floor(Math.max(y0, y1) / tileSize);
        for (int tx = left; tx <= right; tx++) {
            for (int ty = bottom; ty <= top; ty++) {
                if (world.hasTile(tx, ty) && intersectsTile(x0, y0, x1 - x0, y1 - y0,
                        tx * tileSize, ty * tileSize, tileSize)) return false;
            }
        }
        return true;
    }

    private boolean intersectsTile(float x, float y, float dx, float dy, float left, float bottom, int size) {
        float enter = 0;
        float exit = 1;
        if (dx == 0) {
            if (x <= left || x >= left + size) return false;
        } else {
            float t0 = (left - x) / dx;
            float t1 = (left + size - x) / dx;
            enter = Math.max(enter, Math.min(t0, t1));
            exit = Math.min(exit, Math.max(t0, t1));
        }
        if (dy == 0) {
            if (y <= bottom || y >= bottom + size) return false;
        } else {
            float t0 = (bottom - y) / dy;
            float t1 = (bottom + size - y) / dy;
            enter = Math.max(enter, Math.min(t0, t1));
            exit = Math.min(exit, Math.max(t0, t1));
        }
        return enter <= exit;
    }
}
