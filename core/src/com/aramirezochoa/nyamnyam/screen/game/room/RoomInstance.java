package com.aramirezochoa.nyamnyam.screen.game.room;

import java.util.EnumMap;
import com.badlogic.gdx.utils.Array;
import com.aramirezochoa.nyamnyam.screen.game.core.TileType;

/** Mutable room cells with collision types cached and refreshed when a cell changes. */
public class RoomInstance {

    private final int width;
    private final int height;
    private final int tileSize;
    private final RoomMaterial[][] materials;
    private final TileType[][] collisionTypes;
    private final RoomLayout layout;
    private boolean combatCleared;
    private final Array<RoomAttractor> attractors;
    private final EnumMap<RoomMaterial, RoomAppearance> appearances = new EnumMap<RoomMaterial, RoomAppearance>(RoomMaterial.class);

    RoomInstance(int width, int height, int tileSize, RoomMaterial[][] sourceMaterials, RoomLayout layout, Array<RoomAttractor> attractors) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.layout = layout;
        this.attractors = attractors;
        materials = new RoomMaterial[height][width];
        collisionTypes = new TileType[height][width];
        for (int y = 0; y < height; y++) {
            System.arraycopy(sourceMaterials[y], 0, materials[y], 0, width);
        }
        rebuildCollisionCache();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileSize() {
        return tileSize;
    }

    /** Combat completion does not itself decide whether this room's exits are open. */
    public boolean isCombatCleared() { return combatCleared; }

    public void markCombatCleared() { combatCleared = true; }

    public RoomLayout getLayout() {
        return layout;
    }

    public Array<RoomAttractor> getAttractors() { return attractors; }

    public RoomMaterial getMaterialAt(int x, int y) {
        checkCoordinates(x, y);
        return materials[y][x];
    }

    public TileType getCollisionTypeAt(int x, int y) {
        checkCoordinates(x, y);
        return collisionTypes[y][x];
    }

    public void setAppearance(RoomMaterial material, RoomAppearance appearance) {
        if (material == null || appearance == null) {
            throw new IllegalArgumentException("Material and appearance are required");
        }
        appearances.put(material, appearance);
    }

    public RoomAppearance getAppearanceAt(int x, int y) {
        RoomMaterial material = getMaterialAt(x, y);
        return material == null ? null : appearances.get(material);
    }

    /** Use a null material to make the cell empty. */
    public void setMaterialAt(int x, int y, RoomMaterial material) {
        checkCoordinates(x, y);
        materials[y][x] = material;

        refreshCollisionAt(x, y);
        refreshCollisionAt(x - 1, y);
        refreshCollisionAt(x + 1, y);
        refreshCollisionAt(x, y + 1);
    }

    private void rebuildCollisionCache() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                refreshCollisionAt(x, y);
            }
        }
    }

    private void refreshCollisionAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }
        RoomMaterial material = materials[y][x];
        if (material == null) {
            collisionTypes[y][x] = TileType.NO_BLOCK;
            return;
        }

        collisionTypes[y][x] = material.getCollisionType(
                isOccupied(x, y - 1), isOccupied(x - 1, y), isOccupied(x + 1, y));
    }

    private boolean isOccupied(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height && materials[y][x] != null;
    }

    private void checkCoordinates(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Room cell outside bounds: " + x + ", " + y);
        }
    }
}
