package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.room.RoomAppearance;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomLoader;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomMaterial;

/** Creates either the existing single-room JSON run or a seeded navigation area. */
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

    /** Builds data for a navigation area without changing the active single-room game flow. */
    public void populateGenerated(RunState runState, long seed) {
        FloorState floor = createGeneratedFloor(seed);
        runState.setCurrentRoom(null);
        runState.addFloor(floor);
    }

    public FloorState createGeneratedFloor(long seed, String worldName) {
        WorldConfig world = new WorldLoader().load(worldName);
        return new AreaGenerator(roomLoader, world).generate(seed);
    }

    public FloorState createGeneratedFloor(long seed) {
        return createGeneratedFloor(seed, "world_01");
    }
}
