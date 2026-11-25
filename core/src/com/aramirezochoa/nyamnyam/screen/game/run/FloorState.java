package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;
import com.badlogic.gdx.utils.Array;

/** Placed rooms and stable connections belonging to one floor in a run. */
public class FloorState {

    private final Array<PlacedRoom> rooms = new Array<PlacedRoom>();
    private final long seed;
    private int currentRoomIndex;

    public FloorState() { this(0); }

    public FloorState(long seed) { this.seed = seed; }

    public long getSeed() { return seed; }

    public Array<PlacedRoom> getRooms() {
        return rooms;
    }

    public PlacedRoom getCurrentPlacement() {
        return rooms.size == 0 || currentRoomIndex < 0 || currentRoomIndex >= rooms.size
                ? null : rooms.get(currentRoomIndex);
    }

    public int getCurrentRoomIndex() { return currentRoomIndex; }

    public RoomInstance getCurrentRoom() {
        PlacedRoom placement = getCurrentPlacement();
        return placement == null ? null : placement.getRoom();
    }

    public void addRoom(RoomInstance room) {
        addRoom(null, room, 0, 0);
    }

    public int addRoom(String templatePath, RoomInstance room, int moduleX, int moduleY) {
        if (room == null) {
            throw new IllegalArgumentException("Room is required");
        }
        rooms.add(new PlacedRoom(templatePath, room, moduleX, moduleY));
        return rooms.size - 1;
    }

    void connect(int firstIndex, String firstSlot, int secondIndex, String secondSlot) {
        if (firstIndex == secondIndex || firstIndex < 0 || secondIndex < 0
                || firstIndex >= rooms.size || secondIndex >= rooms.size) {
            throw new IllegalArgumentException("Connection needs two placed rooms");
        }
        PlacedRoom first = rooms.get(firstIndex);
        PlacedRoom second = rooms.get(secondIndex);
        if (first.getLink(firstSlot) != null || second.getLink(secondSlot) != null) {
            throw new IllegalArgumentException("A connector is already linked");
        }
        first.link(firstSlot, new RoomLink(secondIndex, secondSlot));
        second.link(secondSlot, new RoomLink(firstIndex, firstSlot));
    }

    public void setCurrentRoomIndex(int currentRoomIndex) {
        if (currentRoomIndex < 0 || currentRoomIndex >= rooms.size) {
            throw new IndexOutOfBoundsException("No room at index " + currentRoomIndex);
        }
        this.currentRoomIndex = currentRoomIndex;
    }
}
