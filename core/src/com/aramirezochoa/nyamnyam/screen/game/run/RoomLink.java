package com.aramirezochoa.nyamnyam.screen.game.run;

/** The other end of one active, bidirectional room connection. */
public class RoomLink {
    private final int roomIndex;
    private final String slot;

    RoomLink(int roomIndex, String slot) {
        this.roomIndex = roomIndex;
        this.slot = slot;
    }

    public int getRoomIndex() { return roomIndex; }

    public String getSlot() { return slot; }
}
