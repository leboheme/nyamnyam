package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;

/** Mutable state shared across rooms during one game run. */
public class RunState {

    private RoomInstance currentRoom;

    public RoomInstance getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(RoomInstance currentRoom) {
        this.currentRoom = currentRoom;
    }
}
