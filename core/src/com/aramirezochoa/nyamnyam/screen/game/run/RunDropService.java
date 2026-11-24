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

/** JSON defeat rewards and run-owned pickup lifecycle. No legacy food score or extra lives. */
public class RunDropService {
    private final RunRuntime runtime;
    private final Array<DropLaunch> launches = new Array<DropLaunch>();
    private final Array<RunPickup> pickups = new Array<RunPickup>();
    private final ShapeRenderer pickupRenderer = new ShapeRenderer();
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
            launchPickup(type, enemy.position.x + (enemy.dimension.x - TOOTH_PICKUP_SIZE) / 2,
                    enemy.position.y, direction);
        }
        float roll = random.nextFloat();
        int count = roll < profile.noDropChance ? 0 : roll < profile.noDropChance + profile.oneDropChance ? 1 : 2;
        for (int i = 0; i < count; i++) {
            Array<MealType> family = random.nextFloat() < profile.fruitChance ? fruits : vegetables;
            MealType type = family.get(random.nextInt(family.size));
            float size = type.getDimension().x;
            launches.add(new DropLaunch(type, null, runtime.getTextureAtlas().findRegion(type.name()), size,
                    enemy.position.x + (enemy.dimension.x - size) / 2,
                    enemy.position.y + (enemy.dimension.y - size) / 2,
                    (i == 0 ? direction : -direction) * DROP_HORIZONTAL_SPEED,
                    DROP_UPWARD_SPEED * (i == 0 ? 1 : DROP_SECOND_UPWARD_SPEED_MULTIPLIER)));
        }
    }

    public void prepareFood(Meal meal) {
        DroppedMealState state = EntityStateFactory.state(DroppedMealState.class);
        meal.setCurrentState(state);
    }

    Array<RunPickup> getPickups() { return pickups; }

    public void update(float deltaTime) {
        for (int i = bodies.size - 1; i >= 0; i--) if (bodies.get(i).update(deltaTime)) bodies.removeIndex(i);
        for (int i = launches.size - 1; i >= 0; i--) {
            DropLaunch launch = launches.get(i);
            if (launch.update(deltaTime, runtime.getCollisionWorld())) {
                materialize(launch);
                launches.removeIndex(i);
            }
        }
        MainAvatar player = runtime.getMainAvatar();
        if (player == null || runtime.getToothHealth().isDefeated()) return;
        for (int i = pickups.size - 1; i >= 0; i--) {
            RunPickup pickup = pickups.get(i);
            if (!pickup.canCollect() || !pickup.getBox().overlaps(player.box)) continue;
            if (pickup.apply(runtime)) {
                pickups.removeIndex(i);
                runtime.getMediaManager().playSound(MediaManager.SOUND_NYAM_MEAL);
            }
        }
        for (Meal meal : runtime.getEntityWorld().getFood()) {
            if (!meal.isEnabled() || !(meal.getType().isFruit() || meal.getType().isVerdure())
                    || !meal.box.overlaps(player.box)) continue;
            float restore = meal.getType().isFruit() ? FRUIT_SALIVATION_RESTORE : VERDURE_SALIVATION_RESTORE;
            if (!runtime.getSalivationReserve().canRestore(restore)) {
                continue;
            }
            runtime.getSalivationReserve().restore(restore);
            runtime.getMediaManager().playSound(MediaManager.SOUND_NYAM_MEAL);
            meal.setRenderRotation(0);
            meal.setEnabled(false);
            meal.setCurrentState(EntityStateFactory.explode(meal, false));
        }
    }

    public void render(Batch batch) {
        if (bodies.size == 0 && launches.size == 0) return;
        batch.begin();
        for (DefeatedBodyEffect body : bodies) body.render(batch);
        for (DropLaunch launch : launches) launch.render(batch);
        batch.setColor(1, 1, 1, 1);
        batch.end();
    }

    public int spawnPickup(int id) {
        PickupType type = PickupType.fromId(id);
        MainAvatar player = runtime.getMainAvatar();
        if (type == null || player == null || runtime.getToothHealth().isDefeated()) return -1;
        CollisionWorld world = runtime.getCollisionWorld();
        float size = type.isUpgrade() ? STAT_PICKUP_SIZE : TOOTH_PICKUP_SIZE;
        float direction = player.getLookingAt() == DirectionType.LEFT ? -1 : 1;
        float x = MathUtils.clamp(player.position.x + player.dimension.x / 2 - size / 2,
                world.getTileSize(), world.getWidth() - world.getTileSize() - size);
        float y = MathUtils.clamp(player.position.y, world.getTileSize(),
                world.getHeight() - world.getTileSize() - size);
        launchPickup(type, x, y, direction);
        return id;
    }

    public void renderPickups(Matrix4 projection) {
        if (pickups.size == 0 && launches.size == 0) return;
        pickupRenderer.setProjectionMatrix(projection);
        pickupRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (RunPickup pickup : pickups) pickup.render(pickupRenderer);
        for (DropLaunch launch : launches) launch.render(pickupRenderer);
        pickupRenderer.end();
    }

    private void launchPickup(PickupType type, float x, float y, float direction) {
        float size = type.isUpgrade() ? STAT_PICKUP_SIZE : TOOTH_PICKUP_SIZE;
        launches.add(new DropLaunch(null, type, null, size, x, y,
                direction * DROP_HORIZONTAL_SPEED, DROP_UPWARD_SPEED));
    }

    private void materialize(DropLaunch launch) {
        if (launch.pickupType != null) {
            pickups.add(launch.pickupType.isUpgrade()
                    ? new UpgradePickup(launch.pickupType, launch.x, launch.y)
                    : new ToothPickup(launch.pickupType, launch.x, launch.y));
        } else {
            Meal meal = launch.mealType.isFruit()
                    ? EntityFactory.fruit(launch.x, launch.y, launch.mealType, runtime)
                    : EntityFactory.verdure(launch.x, launch.y, launch.mealType, runtime);
            prepareFood(meal);
            runtime.addMeal(meal);
        }
    }

    public void clear() { launches.clear(); bodies.clear(); pickups.clear(); }
    public void dispose() { clear(); pickupRenderer.dispose(); }
}
