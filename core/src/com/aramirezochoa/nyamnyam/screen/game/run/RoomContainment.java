package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.CollisionWorld;

/** Clips a small, axis-aligned movement to the room perimeter, including exclusions. */
final class RoomContainment {
    private RoomContainment() { }

    static float movementFraction(CollisionWorld world, float x, float y, float width, float height,
                                  float dx, float dy) {
        if (world.containsBounds(x + dx, y + dy, width, height)) return 1;
        float allowed = 0, blocked = 1;
        for (int i = 0; i < 16; i++) {
            float middle = (allowed + blocked) / 2;
            if (world.containsBounds(x + dx * middle, y + dy * middle, width, height)) allowed = middle;
            else blocked = middle;
        }
        return allowed;
    }
}
