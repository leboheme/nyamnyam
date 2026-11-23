package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.EntityWorld;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;

/** Records resolved room combat independently of UI, rewards and future room access rules. */
public class RoomCompletionService {
    private final RoomInstance room;
    private final EntityWorld entityWorld;

    public RoomCompletionService(RoomInstance room, EntityWorld entityWorld) {
        this.room = room;
        this.entityWorld = entityWorld;
    }

    public void update() {
        if (!room.isCombatCleared() && !entityWorld.hasUnresolvedEnemies()) {
            room.markCombatCleared();
        }
    }
}
