package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.EntityWorld;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;

/** Records resolved room combat independently of UI, rewards and future room access rules. */
public class RoomCompletionService {
    private final RoomInstance room;
    private final EntityWorld entityWorld;
    private final RunCombatService combatService;

    public RoomCompletionService(RoomInstance room, EntityWorld entityWorld, RunCombatService combatService) {
        this.room = room;
        this.entityWorld = entityWorld;
        this.combatService = combatService;
    }

    public void update() {
        if (!room.isCombatCleared() && !entityWorld.hasUnresolvedEnemies()
                && !combatService.hasUnresolvedDefeatedEnemies()) {
            room.markCombatCleared();
        }
    }
}
