package com.aramirezochoa.nyamnyam.screen.game.room;

/** Available authored door slot, whether or not the area connects it. */
public class RoomConnector {
    private final String slot;
    private final int moduleX;
    private final int moduleY;
    private final float x;
    private final float y;
    private final float length;
    private final float arrivalX;
    private final float arrivalY;

    RoomConnector(RoomConnectorConfig config) {
        slot = config.slot;
        moduleX = config.moduleX;
        moduleY = config.moduleY;
        x = config.x;
        y = config.y;
        length = config.length;
        arrivalX = config.arrivalX;
        arrivalY = config.arrivalY;
    }

    public String getSlot() { return slot; }
    public int getModuleX() { return moduleX; }
    public int getModuleY() { return moduleY; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getLength() { return length; }
    public float getArrivalX() { return arrivalX; }
    public float getArrivalY() { return arrivalY; }
}
