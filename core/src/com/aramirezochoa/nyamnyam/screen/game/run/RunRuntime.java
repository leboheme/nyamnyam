package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.DataManager;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.CollisionWorld;
import com.aramirezochoa.nyamnyam.screen.game.core.EntityWorld;
import com.aramirezochoa.nyamnyam.screen.game.core.GameplayCamera;
import com.aramirezochoa.nyamnyam.screen.game.core.GameplayContext;
import com.aramirezochoa.nyamnyam.screen.game.core.RoomCollisionWorld;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.Projectile;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.common.RaiseType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.main.MainAvatarStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomEnemySpawn;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomMealSpawn;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomRenderer;
import com.aramirezochoa.nyamnyam.screen.game.status.GameStatus;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

/** Runtime systems for the active run. */
public class RunRuntime implements GameplayContext {

    private final RunState runState;
    private final GameStatus gameStatus;
    private final MediaManager mediaManager;
    private final TextureAtlas textureAtlas;
    private final EntityWorld entityWorld = new EntityWorld();
    private final RoomCompletionService roomCompletionService;
    private final GameplayCamera camera = new GameplayCamera();
    private final RoomRenderer roomRenderer;
    private final SpriteBatch entityBatch = new SpriteBatch();
    private final Array<Constant.Coordinates> leftGuide = new Array<Constant.Coordinates>();
    private final Array<Constant.Coordinates> rightGuide = new Array<Constant.Coordinates>();
    private final CollisionWorld collisionWorld;
    private final RunPhysicsSystem physicsSystem;
    private final BubbleAttractionService bubbleAttractionService;
    private final BubbleContactSystem bubbleContactSystem;
    private final BubbleTerrain bubbleTerrain;
    private final RunCombatService combatService;
    private final RunDropService dropService;
    private final RunSurvivalService survivalService;
    private long nextEntityId;

    public RunRuntime(MediaManager mediaManager, RunState runState, GameStatus gameStatus) {
        this.runState = runState;
        this.gameStatus = gameStatus;
        this.mediaManager = mediaManager;
        textureAtlas = mediaManager.get("data/game/game.atlas");
        roomRenderer = new RoomRenderer(mediaManager.get("data/stages/tiles.png"));
        collisionWorld = new RoomCollisionWorld(runState.getCurrentRoom());
        physicsSystem = new RunPhysicsSystem(collisionWorld);
        roomCompletionService = new RoomCompletionService(runState.getCurrentRoom(), entityWorld);
        bubbleTerrain = new BubbleTerrain(collisionWorld);
        bubbleContactSystem = new BubbleContactSystem(bubbleTerrain);
        combatService = new RunCombatService(this);
        dropService = new RunDropService(this);
        survivalService = new RunSurvivalService(this, runState.getToothHealth());
        bubbleAttractionService = new BubbleAttractionService(runState.getCurrentRoom());
        loadMainAvatar();
        loadEnemies();
        loadMeals();
    }

    public void update(float deltaTime) {
        if (runState.getToothHealth().isDefeated()) {
            entityWorld.getMainAvatar().update(deltaTime);
            dropService.update(deltaTime);
            physicsSystem.update(entityWorld, dropService.getPickups(), deltaTime, false);
            survivalService.updateDefeat(deltaTime);
            return;
        }
        runState.getSalivationReserve().update(deltaTime, runState.getStats());
        MainAvatar player = entityWorld.getMainAvatar();
        if (player != null) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) player.left();
            if (Gdx.input.isKeyPressed(Input.Keys.D)) player.right();
            if (Gdx.input.isKeyPressed(Input.Keys.W)) player.jump();
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) player.fire();
        }
        boolean jumpRequested = player != null && player.consumeJumpRequest();
        float intendedX = player == null ? 0 : player.velocity.x;
        float intendedY = player == null ? 0 : player.velocity.y;
        entityWorld.update(deltaTime);
        if (runState.getToothHealth().isDefeated()) return;
        dropService.update(deltaTime);
        physicsSystem.update(entityWorld, dropService.getPickups(), deltaTime, true);
        bubbleContactSystem.update(entityWorld, deltaTime, jumpRequested, intendedX, intendedY);
        roomCompletionService.update();
    }

    public void render(float deltaTime) {
        RoomInstance room = runState.getCurrentRoom();
        if (room == null) {
            return;
        }
        float tileSize = room.getTileSize();
        float worldWidth = (room.getWidth() + 2) * tileSize;
        float worldHeight = (room.getHeight() + 2) * tileSize;
        MainAvatar player = entityWorld.getMainAvatar();
        float targetX = player == null ? worldWidth / 2 : player.position.x + player.dimension.x / 2;
        float targetY = player == null ? worldHeight / 2 : player.position.y + player.dimension.y / 2;
        camera.follow(targetX, targetY, worldWidth, worldHeight);
        camera.apply();
        OrthographicCamera orthoCamera = camera.getCamera();
        roomRenderer.render(room, orthoCamera.combined);
        entityBatch.setProjectionMatrix(orthoCamera.combined);
        entityWorld.render(entityBatch);
        dropService.render(entityBatch);
        dropService.renderPickups(orthoCamera.combined);
    }

    public void resize(int width, int height) {
        camera.resize(width, height);
    }

    public void dispose() {
        if (entityWorld.getMainAvatar() != null) {
            entityWorld.getMainAvatar().free();
            entityWorld.setMainAvatar(null);
        }
        dropService.dispose();
        entityWorld.clear();
        roomRenderer.dispose();
        entityBatch.dispose();
    }

    private void loadMainAvatar() {
        MainAvatar avatar = EntityFactory.mainAvatar(DataManager.INSTANCE.getCurrentCharacter(),
                Constant.MAIN_AVATAR_SPAWN_POSITION.x, Constant.MAIN_AVATAR_SPAWN_POSITION.y,
                DirectionType.RIGHT, this);
        entityWorld.setMainAvatar(avatar);
        // JSON room entry starts playable and vulnerable, without the legacy spawn delay/blink.
        avatar.setCurrentState(MainAvatarStateFactory.idle(avatar));
        avatar.spawnTimer = -1;
        // Debug room loading preserves a defeated run; only an explicit restart restores health.
        if (runState.getToothHealth().isDefeated()) survivalService.beginDefeat(avatar);
    }

    private void loadEnemies() {
        RoomInstance room = runState.getCurrentRoom();
        if (room == null || room.getLayout() == null) {
            return;
        }
        int tileSize = room.getTileSize();
        for (int i = 0; i < room.getLayout().getEnemies().size; i++) {
            RoomEnemySpawn spawn = room.getLayout().getEnemies().get(i);
            Enemy enemy = EntityFactory.enemy(spawn.getType(),
                    (spawn.getX() + 1) * tileSize, (spawn.getY() + 1) * tileSize,
                    spawn.getFacing(), this);
            addEnemy(enemy);
        }
    }

    private void loadMeals() {
        RoomInstance room = runState.getCurrentRoom();
        if (room == null || room.getLayout() == null) {
            return;
        }
        int tileSize = room.getTileSize();
        for (int i = 0; i < room.getLayout().getMeals().size; i++) {
            RoomMealSpawn spawn = room.getLayout().getMeals().get(i);
            float x = (spawn.getX() + 1) * tileSize;
            float y = (spawn.getY() + 1) * tileSize;
            MealType type = spawn.getType();
            Meal meal;
            if (type.isBoost()) {
                meal = EntityFactory.boost(x, y, type, this, spawn.isExpiring());
            } else if (type.isFruit()) {
                meal = EntityFactory.fruit(x, y, type, this);
            } else {
                meal = EntityFactory.verdure(x, y, type, this);
            }
            if (type.isFruit() || type.isVerdure()) dropService.prepareFood(meal);
            entityWorld.addMeal(meal);
        }
    }

    @Override public TextureAtlas getTextureAtlas() { return textureAtlas; }
    @Override public MediaManager getMediaManager() { return mediaManager; }
    @Override public GameStatus getGameStatus() { return gameStatus; }
    @Override public EntityWorld getEntityWorld() { return entityWorld; }
    @Override public CollisionWorld getCollisionWorld() { return collisionWorld; }
    public ToothHealth getToothHealth() { return runState.getToothHealth(); }
    @Override public RunSurvivalService getSurvivalService() { return survivalService; }
    @Override public RunDropService getDropService() { return dropService; }
    @Override public RunCombatService getCombatService() { return combatService; }
    @Override public SalivationReserve getSalivationReserve() { return runState.getSalivationReserve(); }
    @Override public RunStats getRunStats() { return runState.getStats(); }
    @Override public void addEnemy(Enemy enemy) {
        combatService.prepareEnemy(enemy);
        entityWorld.addEnemy(enemy);
    }
    @Override public void addMeal(Meal meal) { entityWorld.addMeal(meal); }
    @Override public void addProjectile(Projectile projectile) {
        combatService.prepareAttack(projectile);
        entityWorld.addProjectile(projectile);
    }
    @Override public void addPet(Avatar avatar) { entityWorld.addPet(avatar); }
    @Override public void mainAvatarDead() { loadMainAvatar(); }
    @Override public void notifyMessage(String message, float x, float y) { }
    @Override public int getAutomaticAirMode() { return 0; }
    @Override public RaiseType getBubbleRaiseType() { return RaiseType.STATIC; }
    @Override public BubbleAttractionService getBubbleAttractionService() { return bubbleAttractionService; }
    @Override public long nextEntityId() { return nextEntityId++; }
    @Override public Array<Constant.Coordinates> getLeftGuide() { return leftGuide; }
    @Override public Array<Constant.Coordinates> getRightGuide() { return rightGuide; }

    public BubbleTerrain getBubbleTerrain() { return bubbleTerrain; }

    public MainAvatar getMainAvatar() {
        return entityWorld.getMainAvatar();
    }
}
