package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.room.RoomAppearance;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomLoader;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomMaterial;

/** Creates the first fixed JSON-backed run: one floor containing one room. */
public class RunFactory {

    private final RoomLoader roomLoader;

    public RunFactory(RoomLoader roomLoader) {
        this.roomLoader = roomLoader;
    }

    public boolean populate(RunState runState, String roomPath) {
        runState.setCurrentRoom(null);
        RoomInstance room = roomLoader.loadIfPresent(roomPath);
        if (room == null) {
            return false;
        }

        room.setAppearance(RoomMaterial.FLOOR, RoomAppearance.STATIC_TILES);
        FloorState floor = new FloorState();
        floor.addRoom(room);
        runState.addFloor(floor);
        return true;
    }
}
