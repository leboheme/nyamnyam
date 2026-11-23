package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.EnemyType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityStateFactory;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.MathUtils;
import com.aramirezochoa.nyamnyam.screen.game.core.CollisionWorld;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** JSON defeat rewards and common-food pickup. No legacy food score or extra lives. */
public class RunDropService {
    private final RunRuntime runtime;
    private final Array<ToothPickup> teeth = new Array<ToothPickup>();
    private final ShapeRenderer toothRenderer = new ShapeRenderer();
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
        if (random.nextFloat() < profile.toothDropChance) {
            boolean caries = random.nextFloat() < profile.toothCariesChance;
            boolean half = random.nextFloat() < profile.toothHalfChance;
            PickupType type = caries ? (half ? PickupType.TOOTH_CARIES_HALF : PickupType.TOOTH_CARIES)
                    : (half ? PickupType.TOOTH_NORMAL_HALF : PickupType.TOOTH_NORMAL);
            teeth.add(new ToothPickup(type, enemy.position.x + (enemy.dimension.x - TOOTH_PICKUP_SIZE) / 2,
                    enemy.position.y, direction, runtime.getCollisionWorld()));
        }
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
        for (ToothPickup pickup : teeth) pickup.update(deltaTime);
        MainAvatar player = runtime.getMainAvatar();
        if (player == null || !player.canPerformAction() || runtime.getToothHealth().isDefeated()) return;
        for (int i = teeth.size - 1; i >= 0; i--) {
            ToothPickup pickup = teeth.get(i);
            if (!pickup.canCollect() || !pickup.box.overlaps(player.box)) continue;
            boolean collected = pickup.type.caries ? runtime.getToothHealth().addCaries(pickup.type.halves)
                    : runtime.getToothHealth().restoreNormal(pickup.type.halves);
            if (collected) {
                teeth.removeIndex(i);
                runtime.getMediaManager().playSound(MediaManager.SOUND_NYAM_MEAL);
            }
        }
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

    public int spawnPickup(int id) {
        PickupType type = PickupType.fromId(id);
        MainAvatar player = runtime.getMainAvatar();
        if (type == null || player == null || runtime.getToothHealth().isDefeated()) return -1;
        CollisionWorld world = runtime.getCollisionWorld();
        float direction = player.getLookingAt() == DirectionType.LEFT ? -1 : 1;
        float x = MathUtils.clamp(player.position.x + player.dimension.x / 2 - TOOTH_PICKUP_SIZE / 2,
                world.getTileSize(), world.getWidth() - world.getTileSize() - TOOTH_PICKUP_SIZE);
        float y = MathUtils.clamp(player.position.y, world.getTileSize(),
                world.getHeight() - world.getTileSize() - TOOTH_PICKUP_SIZE);
        teeth.add(new ToothPickup(type, x, y, direction, world));
        return id;
    }

    public void renderTeeth(Matrix4 projection) {
        if (teeth.size == 0) return;
        toothRenderer.setProjectionMatrix(projection);
        toothRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (ToothPickup pickup : teeth) pickup.render(toothRenderer);
        toothRenderer.end();
    }

    public void clear() { bodies.clear(); teeth.clear(); }
    public void dispose() { clear(); toothRenderer.dispose(); }
}
