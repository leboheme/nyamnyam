package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.badlogic.gdx.utils.Array;

/** Saved room residents. Entries contain values, never pooled entities or runtime resources. */
final class RoomState {
    interface Entry {
        void restore(RunRuntime runtime);
    }

    private final Array<Entry> residents = new Array<Entry>();

    void add(Entry resident) { residents.add(resident); }

    void restore(RunRuntime runtime) {
        for (Entry resident : residents) resident.restore(runtime);
    }

    static final class Food implements Entry {
        final MealType type;
        final float x, y, velocityX, velocityY, rotation;

        Food(MealType type, float x, float y, float velocityX, float velocityY, float rotation) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.rotation = rotation;
        }

        @Override public void restore(RunRuntime runtime) { runtime.getDropService().restoreFood(this); }
    }

    static final class Launch implements Entry {
        final MealType mealType;
        final PickupType pickupType;
        final float size, x, y, velocityX, velocityY, rotation;

        Launch(MealType mealType, PickupType pickupType, float size, float x, float y,
               float velocityX, float velocityY, float rotation) {
            this.mealType = mealType;
            this.pickupType = pickupType;
            this.size = size;
            this.x = x;
            this.y = y;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.rotation = rotation;
        }

        @Override public void restore(RunRuntime runtime) { runtime.getDropService().restoreLaunch(this); }
    }

    static final class Tooth implements Entry {
        final PickupType type;
        final float x, y, velocityX, velocityY, rotation;

        Tooth(PickupType type, float x, float y, float velocityX, float velocityY, float rotation) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.rotation = rotation;
        }

        @Override public void restore(RunRuntime runtime) { runtime.getDropService().restoreTooth(this); }
    }
}
