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
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.Projectile;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.common.RaiseType;
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
public class GameWrapper implements GameScreen.GameScreenComponent, GameStatusListener {

    private final MediaManager mediaManager;
    private final GameUserInterface gameUserInterface;
    private SpriteBatch batch;
    private final GameplayCamera gameplayCamera;
    public static ShapeRenderer debugRenderer;

    private TmxStage tmxStage;
    private final RoomDebugRenderer roomDebugRenderer;
    private final RunState runState;
    private final EntityWorld entityWorld = new EntityWorld();
    private boolean showRoomPreview;

    public MainAvatar mainAvatar;
    public Enemy dark;
    private GameStatus gameStatus;
    private TextureRegion background;
    private TextureAtlas textureAtlas;
    private float loadMealTimer, loadAirTimer;
    private boolean loadMealFinished, guidedAir, specialAir, extraMeal;
    private int automaticAirMode, mealSpawnIndex;
    private float winCounter;
    public Array<Constant.Coordinates> leftGuide = new Array<Constant.Coordinates>(true, 6);
    public Array<Constant.Coordinates> rightGuide = new Array<Constant.Coordinates>(true, 6);

    public static boolean debug;
    private boolean bossLevel;

    private float levelDarkTimer, levelHurryTimer;
    private boolean noMoreEnemies, hurryAdvised;

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
        updateCamera();
        loadEnemies();
        loadAir();

        loadMealTimer = Constant.MEAL_SPAWN_DELAY;
        loadMealFinished = false;
        mealSpawnIndex = 0;
        extraMeal = false;
        specialAir = true;

        winCounter = Constant.GAME_WIN_FREE_TIME;

        getMediaManager().stopAllThemes();
        getMediaManager().playTheme(isBossLevel() ? MediaManager.THEME_BOSS : MediaManager.THEME_NORMAL);

        ActivityManager.INSTANCE.trackScreen(ActivityManager.SCREEN_GAME);

        initLevelTime();
    }

    private void initLevelTime() {
        gameUserInterface.showMessage("");
        levelDarkTimer = Constant.GAME_LEVEL_DARK_TIME;
        levelHurryTimer = Constant.GAME_LEVEL_HURRY_TIME;
        dark = null;
        noMoreEnemies = false;
        hurryAdvised = false;
    }

    private void setBackground(MediaManager mediaManager) {
        int backgroundNumber = MathUtils.random(2);
        this.background = ((TextureAtlas) mediaManager.get("data/game/background.atlas")).findRegion("background", backgroundNumber);
    }

    @Override
    public void update(float deltaTime) {
        checkKeyboardInput();

        mainAvatar.update(deltaTime);
        entityWorld.update(deltaTime);
        updateCamera();
        checkSpawnMeals(deltaTime);
        checkSpawnAir(deltaTime);
        checkLevelTime(deltaTime);

        if (!entityWorld.hasEnemies()) {
            boolean enemyTrapped = false;
            for (int i = 0; i < entityWorld.getFood().size; i++) {
                if (entityWorld.getFood().get(i).getType().isCandy()) {
                    enemyTrapped = true;
                }
            }
            if (!enemyTrapped) {
                noMoreEnemies = true;
                if (winCounter < 0) {
                    gameStatus.setGameState(GameState.WIN);
                }
                winCounter -= deltaTime;
                if (!extraMeal) {
                    for (int i = 0; i < entityWorld.getFood().size; i++) {
                        if (entityWorld.getFood().get(i).getType().isAir()) {
                            entityWorld.getFood().get(i).setCurrentState(EntityStateFactory.die(entityWorld.getFood().get(i)));
                            addMeal(EntityFactory.randomVerdure(entityWorld.getFood().get(i).position.x + entityWorld.getFood().get(i).dimension.x / 2, entityWorld.getFood().get(i).position.y, this));
                        }
                    }
                    extraMeal = true;
                }
                if (dark != null && dark.isEnabled()) {
                    dark.setCurrentState(EnemyStateFactory.explode(dark, false));
                }
            }
        }
        // here or in gameStatus??
        if (gameStatus.getLives() <= 0) {
            gameStatus.setGameState(GameState.LOSE);
        }
    }

    private void checkLevelTime(float deltaTime) {
        if (!bossLevel) {
            if (levelHurryTimer < 0 && !hurryAdvised) {
                getMediaManager().playSound(MediaManager.SOUND_HURRY);
                gameUserInterface.showMessage(LanguageManager.INSTANCE.getString("hurry"));
                hurryAdvised = true;
                for (Enemy enemy : entityWorld.getEnemies()) {
                    enemy.anger(true);
                }
            } else {
                levelHurryTimer -= deltaTime;
            }
            if (levelDarkTimer < 0) {
                if (dark != null) {
                    dark.update(deltaTime);
                } else if (!noMoreEnemies) {
                    getMediaManager().playSound(MediaManager.SOUND_DARK);
                    dark = EntityFactory.enemy(EnemyType.DARK, Constant.DARK_SPAWN_POSITION.x, Constant.DARK_SPAWN_POSITION.y, DirectionType.RIGHT, this);
                }
            } else {
                levelDarkTimer -= deltaTime;
            }
        }
    }

    private void checkKeyboardInput() {

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            mainAvatar.left();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            mainAvatar.right();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            mainAvatar.jump();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            mainAvatar.fire();
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
        mainAvatar.render(batch);
        if (dark != null) {
            dark.render(batch);
        }
    }

    public void resize(int width, int height) {
        gameplayCamera.resize(width, height);
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
        roomDebugRenderer.dispose();
        batch.dispose();
        if (tmxStage != null) {
            tmxStage.dispose();
        }
        cleanEntities();
    }

    private void cleanEntities() {
        entityWorld.clear();

        leftGuide.clear();
        rightGuide.clear();

        if (dark != null) {
            dark.free();
        }
    }

    private void loadMap(int levelNumber) {
        if (tmxStage != null) {
            tmxStage.dispose();
        }
        tmxStage = new TmxStage(MediaManager.GAME.loadMap("data/stages/" + levelNumber + ".tmx"));
        showRoomPreview = false;
        bossLevel = tmxStage.isBossLevel();
    }

    private void updateCamera() {
        if (mainAvatar == null || tmxStage == null) {
            return;
        }

        float targetX = mainAvatar.position.x + mainAvatar.dimension.x / 2;
        float targetY = mainAvatar.position.y + mainAvatar.dimension.y / 2;
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

    private void checkSpawnMeals(float deltaTime) {
        if (!loadMealFinished && loadMealTimer < 0) {
            if (mealSpawnIndex < tmxStage.getMealSpawns().size) {
                TmxStage.MealSpawn spawn = tmxStage.getMealSpawns().get(mealSpawnIndex);
                MealType mealType = spawn.getType() == null ? MealType.randomFruit() : spawn.getType();
                if (mealType.isBoost()) {
                    entityWorld.addMeal(EntityFactory.boost(spawn.getX(), spawn.getY(), mealType, this, true));
                } else {
                    entityWorld.addMeal(EntityFactory.fruit(spawn.getX(), spawn.getY(), mealType, this));
                }
                mealSpawnIndex++;
                loadMealTimer = Constant.MEAL_SPAWN_DELAY;

                if (mealSpawnIndex == tmxStage.getMealSpawns().size) {
                    loadMealFinished = true;
                }
            }
        } else {
            loadMealTimer -= deltaTime;
        }
    }

    private void checkSpawnAir(float deltaTime) {
        if (guidedAir) {
            if (loadAirTimer < 0) {
                MealType mealType;
                if (!specialAir) {
                    mealType = MealType.AIR;
                } else {
                    mealType = tmxStage.getAirSettings().getSpecialAir();
                }
                switch (mealType) {
                    case AIR:
                        if (leftGuide.size > 0) {
                            entityWorld.addMeal(EntityFactory.air(mealType, RaiseType.LEFT_GUIDED, leftGuide.first().x - mealType.getDimension().x / 2, leftGuide.first().y - mealType.getDimension().y / 2, DirectionType.LEFT, this));
                        }
                        if (rightGuide.size > 0) {
                            entityWorld.addMeal(EntityFactory.air(mealType, RaiseType.RIGHT_GUIDED, rightGuide.first().x - mealType.getDimension().x / 2, rightGuide.first().y - mealType.getDimension().y / 2, DirectionType.RIGHT, this));
                        }
                        break;
                    case AIR_TORNADO:
                        if (leftGuide.size > 0) {
                            entityWorld.addMeal(EntityFactory.tornado(mealType, RaiseType.LEFT_GUIDED, leftGuide.first().x - mealType.getDimension().x / 2, leftGuide.first().y - mealType.getDimension().y / 2, DirectionType.LEFT, this));
                        }
                        if (rightGuide.size > 0) {
                            entityWorld.addMeal(EntityFactory.tornado(mealType, RaiseType.RIGHT_GUIDED, rightGuide.first().x - mealType.getDimension().x / 2, rightGuide.first().y - mealType.getDimension().y / 2, DirectionType.RIGHT, this));
                        }
                        break;
                    case AIR_SPIKES:
                        if (leftGuide.size > 0) {
                            entityWorld.addMeal(EntityFactory.spikes(mealType, RaiseType.LEFT_GUIDED, leftGuide.first().x - mealType.getDimension().x / 2, leftGuide.first().y - mealType.getDimension().y / 2, DirectionType.LEFT, this));
                        }
                        if (rightGuide.size > 0) {
                            entityWorld.addMeal(EntityFactory.spikes(mealType, RaiseType.RIGHT_GUIDED, rightGuide.first().x - mealType.getDimension().x / 2, rightGuide.first().y - mealType.getDimension().y / 2, DirectionType.RIGHT, this));
                        }
                        break;
                }
                loadAirTimer = Constant.AIR_SPAWN_RATE;
                specialAir = !specialAir;
            }
            loadAirTimer -= deltaTime;
        }
    }

    private void loadAir() {
        TmxStage.AirSettings airSettings = tmxStage.getAirSettings();
        guidedAir = airSettings.isGuided() || (airSettings.hasExtraAir() && !mainAvatar.isAirSkilled());
        if (guidedAir) {
            leftGuide.addAll(airSettings.getLeftGuide());
            rightGuide.addAll(airSettings.getRightGuide());

            loadAirTimer = Constant.AIR_SPAWN_RATE;
        }
        automaticAirMode = airSettings.getAutomaticMode();
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
        if (dark != null) {
            dark.setCurrentState(EnemyStateFactory.explode(dark, false));
        }
        for (Enemy enemy : entityWorld.getEnemies()) {
            enemy.anger(false);
        }
        loadMainAvatar();
    }

    private void loadMainAvatar() {
        if (gameStatus.getLives() > 0) {
            mainAvatar = EntityFactory.mainAvatar(DataManager.INSTANCE.getCurrentCharacter(), Constant.MAIN_AVATAR_SPAWN_POSITION.x, Constant.MAIN_AVATAR_SPAWN_POSITION.y, DirectionType.RIGHT, this);
        } else {
            mainAvatar = EntityFactory.empty(this);
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
        return bossLevel;
    }

    public int getAutomaticAirMode() {
        return automaticAirMode;
    }

    public void notifyMessage(String message, float x, float y) {
        gameUserInterface.showMessage(message);
    }
}
