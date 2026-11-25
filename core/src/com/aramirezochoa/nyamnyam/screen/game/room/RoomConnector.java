package com.aramirezochoa.nyamnyam.screen.game.room;

/** Available authored door slot, whether or not the area connects it. */
public class RoomConnector {
    private final String slot;
    private final int moduleX;
    private final int moduleY;
    private final float x;
    private final float y;
    private final float width;
    private final float height;

    RoomConnector(RoomConnectorConfig config) {
        slot = config.slot;
        moduleX = config.moduleX;
        moduleY = config.moduleY;
        x = config.x;
        y = config.y;
        width = config.width;
        height = config.height;
    }

    public String getSlot() { return slot; }
    public int getModuleX() { return moduleX; }
    public int getModuleY() { return moduleY; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
