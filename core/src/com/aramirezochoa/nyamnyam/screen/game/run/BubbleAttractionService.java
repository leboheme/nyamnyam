package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.room.RoomAttractor;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;
import com.badlogic.gdx.math.Vector2;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Calculates the base velocity that draws a bubble toward its nearest room focus. */
public class BubbleAttractionService {
    private final RoomInstance room;

    public BubbleAttractionService(RoomInstance room) {
        this.room = room;
    }

    public float getBaseSpeed() { return BUBBLE_ATTRACTION_SPEED; }

    public void setVelocity(long entityId, float x, float y, Vector2 velocity) {
        RoomAttractor nearest = null;
        float targetX = 0;
        float targetY = 0;
        float nearestDistance2 = Float.MAX_VALUE;
        for (int i = 0; i < room.getAttractors().size; i++) {
            RoomAttractor attractor = room.getAttractors().get(i);
            long seed = mix(entityId ^ (0x9E3779B97F4A7C15L * (i + 1)));
            float offsetX = unit(seed) * attractor.getAreaWidth() - attractor.getAreaWidth() / 2f;
            float offsetY = unit(mix(seed)) * attractor.getAreaHeight() - attractor.getAreaHeight() / 2f;
            float candidateX = attractor.getX() + offsetX;
            float candidateY = attractor.getY() + offsetY;
            float dx = candidateX - x;
            float dy = candidateY - y;
            float distance2 = dx * dx + dy * dy;
            if (distance2 < nearestDistance2) {
                nearest = attractor;
                targetX = candidateX;
                targetY = candidateY;
                nearestDistance2 = distance2;
            }
        }
        if (nearest == null || nearestDistance2 == 0) {
            velocity.setZero();
            return;
        }
        velocity.set(targetX - x, targetY - y).nor().scl(BUBBLE_ATTRACTION_SPEED);
    }

    private static long mix(long value) {
        value = (value ^ (value >>> 30)) * 0xBF58476D1CE4E5B9L;
        value = (value ^ (value >>> 27)) * 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }

    private static float unit(long value) {
        return (value >>> 40) / (float) (1L << 24);
    }
}
