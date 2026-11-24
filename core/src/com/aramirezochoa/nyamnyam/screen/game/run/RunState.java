package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;
import com.badlogic.gdx.utils.Array;

/** Mutable state shared across rooms during one game run. */
public class RunState {

    private final Array<FloorState> floors = new Array<FloorState>();
    private int currentFloorIndex;
    private final ToothHealth toothHealth = new ToothHealth();
    private final RunStats stats = new RunStats();

    public ToothHealth getToothHealth() { return toothHealth; }

    public RunStats getStats() { return stats; }

    private final SalivationReserve salivationReserve = new SalivationReserve();

    public SalivationReserve getSalivationReserve() {
        return salivationReserve;
    }

    public Array<FloorState> getFloors() {
        return floors;
    }

    public FloorState getCurrentFloor() {
        return floors.size == 0 || currentFloorIndex < 0 || currentFloorIndex >= floors.size
                ? null : floors.get(currentFloorIndex);
    }

    public void addFloor(FloorState floor) {
        if (floor == null) {
            throw new IllegalArgumentException("Floor is required");
        }
        floors.add(floor);
    }

    public RoomInstance getCurrentRoom() {
        FloorState floor = getCurrentFloor();
        return floor == null ? null : floor.getCurrentRoom();
    }

    /** Compatibility helper for the current one-room JSON prototype. */
    public void setCurrentRoom(RoomInstance currentRoom) {
        floors.clear();
        currentFloorIndex = 0;
        if (currentRoom != null) {
            FloorState floor = new FloorState();
            floor.addRoom(currentRoom);
            floors.add(floor);
        }
    }

    public void setCurrentFloorIndex(int currentFloorIndex) {
        if (currentFloorIndex < 0 || currentFloorIndex >= floors.size) {
            throw new IndexOutOfBoundsException("No floor at index " + currentFloorIndex);
        }
        this.currentFloorIndex = currentFloorIndex;
    }
}
