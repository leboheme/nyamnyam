package com.aramirezochoa.nyamnyam.screen.game.room;

/** Bubble attraction focus in world coordinates. */
public class RoomAttractor {
    private final float x;
    private final float y;
    private final float areaWidth;
    private final float areaHeight;

    public RoomAttractor(float x, float y, float areaWidth, float areaHeight) {
        this.x = x;
        this.y = y;
        this.areaWidth = areaWidth;
        this.areaHeight = areaHeight;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getAreaWidth() { return areaWidth; }
    public float getAreaHeight() { return areaHeight; }
}
