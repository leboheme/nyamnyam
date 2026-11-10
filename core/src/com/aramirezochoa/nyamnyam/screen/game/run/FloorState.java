package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;
import com.badlogic.gdx.utils.Array;

/** Runtime rooms belonging to one floor in a run. */
public class FloorState {

    private final Array<RoomInstance> rooms = new Array<RoomInstance>();
    private int currentRoomIndex;

    public Array<RoomInstance> getRooms() {
        return rooms;
    }

    public RoomInstance getCurrentRoom() {
        return rooms.size == 0 || currentRoomIndex < 0 || currentRoomIndex >= rooms.size
                ? null : rooms.get(currentRoomIndex);
    }

    public void addRoom(RoomInstance room) {
        if (room == null) {
            throw new IllegalArgumentException("Room is required");
        }
        rooms.add(room);
    }

    public void setCurrentRoomIndex(int currentRoomIndex) {
        if (currentRoomIndex < 0 || currentRoomIndex >= rooms.size) {
            throw new IndexOutOfBoundsException("No room at index " + currentRoomIndex);
        }
        this.currentRoomIndex = currentRoomIndex;
    }
}
