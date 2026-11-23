package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.EnemyType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityStateFactory;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** JSON defeat rewards and common-food pickup. No legacy food score or extra lives. */
public class RunDropService {
    private final RunRuntime runtime;
    private final RandomXS128 random = new RandomXS128();
    private final Array<DefeatedBodyEffect> bodies = new Array<DefeatedBodyEffect>();
    private final Array<MealType> fruits = new Array<MealType>();
    private final Array<MealType> vegetables = new Array<MealType>();

    public RunDropService(RunRuntime runtime) {
        this.runtime = runtime;
        for (MealType type : MealType.values()) {
            if (type.isFruit()) fruits.add(type);
            if (type.isVerdure()) vegetables.add(type);
        }
    }

    public void onDefeat(Enemy enemy) {
        MainAvatar player = runtime.getMainAvatar();
        float direction = player == null || enemy.position.x >= player.position.x ? 1 : -1;
        bodies.add(new DefeatedBodyEffect(enemy, direction));
        EnemyDropConfig.Profile profile = EnemyDropConfig.resolve((EnemyType) enemy.getType());
        float roll = random.nextFloat();
        int count = roll < profile.noDropChance ? 0 : roll < profile.noDropChance + profile.oneDropChance ? 1 : 2;
        for (int i = 0; i < count; i++) {
            Array<MealType> family = random.nextFloat() < profile.fruitChance ? fruits : vegetables;
            MealType type = family.get(random.nextInt(family.size));
            Meal meal = type.isFruit() ? EntityFactory.fruit(enemy.position.x, enemy.position.y, type, runtime)
                    : EntityFactory.verdure(enemy.position.x, enemy.position.y, type, runtime);
            // Bubbles and food share a footprint; center larger enemy sprites without changing the spawn area.
            meal.setPosition(enemy.position.x + (enemy.dimension.x - meal.dimension.x) / 2,
                    enemy.position.y + (enemy.dimension.y - meal.dimension.y) / 2);
            prepareFood(meal, true);
            meal.velocity.set((i == 0 ? direction : -direction) * DROP_HORIZONTAL_SPEED,
                    DROP_UPWARD_SPEED * (i == 0 ? 1 : DROP_SECOND_UPWARD_SPEED_MULTIPLIER));
            runtime.addMeal(meal);
        }
    }

    public void prepareFood(Meal meal, boolean launched) {
        DroppedMealState state = EntityStateFactory.state(DroppedMealState.class);
        state.configure(meal, runtime.getCollisionWorld(), launched);
        meal.setCurrentState(state);
    }

    public void update(float deltaTime) {
        for (int i = bodies.size - 1; i >= 0; i--) if (bodies.get(i).update(deltaTime)) bodies.removeIndex(i);
        MainAvatar player = runtime.getMainAvatar();
        if (player == null || !player.canPerformAction()) return;
        for (Meal meal : runtime.getEntityWorld().getFood()) {
            if (!meal.isEnabled() || !(meal.getType().isFruit() || meal.getType().isVerdure())
                    || !meal.box.overlaps(player.box)) continue;
            runtime.getSalivationReserve().restore(meal.getType().isFruit() ? FRUIT_SALIVATION_RESTORE : VERDURE_SALIVATION_RESTORE);
            runtime.getMediaManager().playSound(MediaManager.SOUND_NYAM_MEAL);
            meal.setRenderRotation(0);
            meal.setEnabled(false);
            meal.setCurrentState(EntityStateFactory.explode(meal, false));
        }
    }

    public void render(Batch batch) {
        if (bodies.size == 0) return;
        batch.begin();
        for (DefeatedBodyEffect body : bodies) body.render(batch);
        batch.setColor(1, 1, 1, 1);
        batch.end();
    }

    public void clear() { bodies.clear(); }
}
