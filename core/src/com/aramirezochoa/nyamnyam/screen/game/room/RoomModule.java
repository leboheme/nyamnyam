package com.aramirezochoa.nyamnyam.screen.game.room;

/** One occupied logical cell in a room template. */
public class RoomModule {
    private final int x;
    private final int y;

    RoomModule(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
