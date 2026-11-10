package com.aramirezochoa.nyamnyam.screen.game.core;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.DataManager;
import com.aramirezochoa.nyamnyam.activity.ActivityManager;
import com.aramirezochoa.nyamnyam.i18n.LanguageManager;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.GameScreen;
import com.aramirezochoa.nyamnyam.screen.game.GameState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.EnemyType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.boss.Boss;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.Projectile;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.gui.GameUserInterface;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomDebugRenderer;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;
import com.aramirezochoa.nyamnyam.screen.game.run.RunState;
import com.aramirezochoa.nyamnyam.screen.game.status.GameStatus;
import com.aramirezochoa.nyamnyam.screen.game.status.GameStatusListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * Created by boheme on 13/01/15.
 */
public class GameWrapper implements GameScreen.GameScreenComponent, GameStatusListener, GameplayContext {

    private final MediaManager mediaManager;
    private final GameUserInterface gameUserInterface;
    private SpriteBatch batch;
    private final GameplayCamera gameplayCamera;
    public static ShapeRenderer debugRenderer;

    private final RoomDebugRenderer roomDebugRenderer;
    private final RunState runState;
    private final EntityWorld entityWorld = new EntityWorld();
    private final StageContentSpawner stageContentSpawner = new StageContentSpawner(this);
    private final StageTimers stageTimers = new StageTimers();
    private final StageCompletion stageCompletion = new StageCompletion(this);
    private TmxStage tmxStage;
    private CollisionWorld collisionWorld;
    private boolean showRoomPreview;

    private GameStatus gameStatus;
    private TextureRegion background;
    private TextureAtlas textureAtlas;
    public static boolean debug;


    public GameWrapper(MediaManager mediaManager, GameStatus gameStatus, GameUserInterface gameUserInterface,
                       RunState runState) {
        this.gameUserInterface = gameUserInterface;
        this.mediaManager = mediaManager;
        this.runState = runState;
        this.roomDebugRenderer = new RoomDebugRenderer();
        this.batch = new SpriteBatch();
        this.gameplayCamera = new GameplayCamera();
        this.batch.setProjectionMatrix(gameplayCamera.getCamera().combined);

        this.textureAtlas = mediaManager.get("data/game/game.atlas");

        this.debugRenderer = new ShapeRenderer();
        this.debugRenderer.setProjectionMatrix(gameplayCamera.getCamera().combined);
        this.debug = false;
    }

    public void init(MediaManager mediaManager, GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        setBackground(mediaManager);

        cleanEntities();
        loadMainAvatar();
        loadMap(gameStatus.getLevel());
        showRoomPreview = false;
        updateCamera();
        loadEnemies();

        stageCompletion.reset();
        stageContentSpawner.initialize(tmxStage, entityWorld.getMainAvatar());

        getMediaManager().stopAllThemes();
        getMediaManager().playTheme(isBossLevel() ? MediaManager.THEME_BOSS : MediaManager.THEME_NORMAL);

        ActivityManager.INSTANCE.trackScreen(ActivityManager.SCREEN_GAME);

        initLevelTime();
    }

    private void initLevelTime() {
        gameUserInterface.showMessage("");
        stageTimers.reset();
        entityWorld.setDark(null);
        stageCompletion.resetNoMoreEnemies();
    }

    private void setBackground(MediaManager mediaManager) {
        int backgroundNumber = MathUtils.random(2);
        this.background = ((TextureAtlas) mediaManager.get("data/game/background.atlas")).findRegion("background", backgroundNumber);
    }

    @Override
    public void update(float deltaTime) {
        checkKeyboardInput();

        entityWorld.update(deltaTime);
        updateCamera();
        stageContentSpawner.update(deltaTime);
        checkLevelTime(deltaTime);

        stageCompletion.update(deltaTime, gameStatus);
    }

    private void checkLevelTime(float deltaTime) {
        if (stageTimers.updateHurry(deltaTime, isBossLevel())) {
            getMediaManager().playSound(MediaManager.SOUND_HURRY);
            gameUserInterface.showMessage(LanguageManager.INSTANCE.getString("hurry"));
            for (Enemy enemy : entityWorld.getEnemies()) {
                enemy.anger(true);
            }
        }
        if (stageTimers.updateDark(deltaTime, isBossLevel())) {
            if (entityWorld.getDark() != null) {
                entityWorld.getDark().update(deltaTime);
            } else if (!stageCompletion.hasNoMoreEnemies()) {
                getMediaManager().playSound(MediaManager.SOUND_DARK);
                entityWorld.setDark(EntityFactory.enemy(EnemyType.DARK, Constant.DARK_SPAWN_POSITION.x, Constant.DARK_SPAWN_POSITION.y, DirectionType.RIGHT, this));
            }
        }
    }

    private void checkKeyboardInput() {

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            entityWorld.getMainAvatar().left();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            entityWorld.getMainAvatar().right();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            entityWorld.getMainAvatar().jump();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            entityWorld.getMainAvatar().fire();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.F9)) {
            debug = !debug;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F10)) {
            showRoomPreview = !showRoomPreview;
        }
    }

    @Override
    public void render(float deltaTime) {
        gameplayCamera.apply();
        OrthographicCamera camera = gameplayCamera.getCamera();
        batch.setProjectionMatrix(camera.combined);
        debugRenderer.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(background, camera.position.x - camera.viewportWidth / 2,
                camera.position.y - camera.viewportHeight / 2);
        batch.end();

        tmxStage.render(camera);
        if (showRoomPreview && runState.getCurrentRoom() != null) {
            roomDebugRenderer.render(runState.getCurrentRoom(), camera.combined);
        }
        entityWorld.render(batch);
    }

    public void resize(int width, int height) {
        gameplayCamera.resize(width, height);
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
        roomDebugRenderer.dispose();
        batch.dispose();
        unloadLegacyStage();
    }

    public void unloadLegacyStage() {
        if (tmxStage != null) {
            tmxStage.dispose();
            tmxStage = null;
        }
        collisionWorld = null;
        cleanEntities();
    }

    @Override
    public CollisionWorld getCollisionWorld() {
        return collisionWorld;
    }

    private void cleanEntities() {
        entityWorld.clear();

    }

    private void loadMap(int levelNumber) {
        if (tmxStage != null) {
            tmxStage.dispose();
        }
        tmxStage = new TmxStage(MediaManager.GAME.loadMap("data/stages/" + levelNumber + ".tmx"));
        collisionWorld = new TmxCollisionWorld(tmxStage.getMap());
        showRoomPreview = false;
    }

    private void updateCamera() {
        if (entityWorld.getMainAvatar() == null || tmxStage == null) {
            return;
        }

        float targetX = entityWorld.getMainAvatar().position.x + entityWorld.getMainAvatar().dimension.x / 2;
        float targetY = entityWorld.getMainAvatar().position.y + entityWorld.getMainAvatar().dimension.y / 2;
        gameplayCamera.follow(targetX, targetY, tmxStage.getWorldWidth(), tmxStage.getWorldHeight());
    }

    private void loadEnemies() {
        for (TmxStage.EnemySpawn spawn : tmxStage.getEnemySpawns()) {
            Enemy enemy = EntityFactory.enemy(spawn.getType(), spawn.getX(), spawn.getY(), spawn.getFacing(), this);
            if (spawn.getBossBoost() != null) {
                ((Boss) enemy).setBoostType(spawn.getBossBoost());
            }
            entityWorld.addEnemy(enemy);
        }
    }

    public void addEnemy(Enemy enemy) {
        entityWorld.addEnemy(enemy);
    }

    public void addMeal(Meal meal) {
        entityWorld.addMeal(meal);
    }

    public void addProjectile(Projectile projectile) {
        entityWorld.addProjectile(projectile);
    }

    public void addPet(Avatar avatar) {
        entityWorld.addPet(avatar);
    }

    public Array<Constant.Coordinates> getLeftGuide() {
        return stageContentSpawner.getLeftGuide();
    }

    public Array<Constant.Coordinates> getRightGuide() {
        return stageContentSpawner.getRightGuide();
    }

    public EntityWorld getEntityWorld() {
        return entityWorld;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public TiledMap getMap() {
        return tmxStage == null ? null : tmxStage.getMap();
    }

    public void mainAvatarDead() {
        initLevelTime();
        if (entityWorld.getDark() != null) {
            entityWorld.getDark().setCurrentState(EnemyStateFactory.explode(entityWorld.getDark(), false));
        }
        for (Enemy enemy : entityWorld.getEnemies()) {
            enemy.anger(false);
        }
        loadMainAvatar();
    }

    private void loadMainAvatar() {
        if (gameStatus.getLives() > 0) {
            entityWorld.setMainAvatar(EntityFactory.mainAvatar(DataManager.INSTANCE.getCurrentCharacter(), Constant.MAIN_AVATAR_SPAWN_POSITION.x, Constant.MAIN_AVATAR_SPAWN_POSITION.y, DirectionType.RIGHT, this));
        } else {
            entityWorld.setMainAvatar(EntityFactory.empty(this));
        }
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    public MediaManager getMediaManager() {
        return mediaManager;
    }

    @Override
    public void notifyState(GameState state) {
        switch (state) {
            case IN_GAME:
                getMediaManager().playTheme(isBossLevel() ? MediaManager.THEME_BOSS : MediaManager.THEME_NORMAL);
                break;
            case PAUSE:
                getMediaManager().pauseTheme(isBossLevel() ? MediaManager.THEME_BOSS : MediaManager.THEME_NORMAL);
                break;
        }
    }

    @Override
    public void notifyScore(int score, int increased, float x, float y) {

    }

    @Override
    public void notifyLives(int lives, float x, float y) {

    }

    public boolean isBossLevel() {
        return tmxStage != null && tmxStage.isBossLevel();
    }

    public int getAutomaticAirMode() {
        return stageContentSpawner.getAutomaticAirMode();
    }

    public void notifyMessage(String message, float x, float y) {
        gameUserInterface.showMessage(message);
    }
}
