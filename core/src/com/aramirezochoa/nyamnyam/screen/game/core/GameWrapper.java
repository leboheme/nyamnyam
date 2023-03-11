package com.aramirezochoa.nyamnyam.screen.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
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
import com.aramirezochoa.nyamnyam.screen.game.status.GameStatus;
import com.aramirezochoa.nyamnyam.screen.game.status.GameStatusListener;

/**
 * Created by boheme on 13/01/15.
 */
public class GameWrapper implements GameScreen.GameScreenComponent, GameStatusListener {

    private final MediaManager mediaManager;
    private final GameUserInterface gameUserInterface;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    public MainAvatar mainAvatar;
    public Enemy dark;
    public final SnapshotArray<Enemy> enemies = new SnapshotArray(true, 10, Enemy.class);
    public final SnapshotArray<Meal> food = new SnapshotArray(true, 4, Meal.class);
    public final SnapshotArray<Projectile> projectiles = new SnapshotArray(true, 4, Projectile.class);
    public final SnapshotArray<Avatar> pets = new SnapshotArray(true, 4, Avatar.class);
    private GameStatus gameStatus;
    private TextureRegion background;
    private TextureAtlas textureAtlas;
    private float loadMealTimer, loadAirTimer;
    private boolean loadMealFinished, guidedAir, specialAir, extraMeal;
    private int automaticAirMode;
    private float winCounter;
    public Array<Constant.Coordinates> leftGuide = new Array<Constant.Coordinates>(true, 6);
    public Array<Constant.Coordinates> rightGuide = new Array<Constant.Coordinates>(true, 6);

    public static ShapeRenderer debugRenderer;
    public static boolean debug;
    private boolean bossLevel;

    private static final String MEAL_LAYER = "meals";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String PATH = "path";

    private float levelDarkTimer, levelHurryTimer;
    private boolean noMoreEnemies, hurryAdvised;

    public GameWrapper(MediaManager mediaManager, GameStatus gameStatus, GameUserInterface gameUserInterface) {
        this.gameUserInterface = gameUserInterface;
        this.mediaManager = mediaManager;
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT);
        this.camera.setToOrtho(false, Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT);
        this.camera.position.y += Constant.SCREEN_GAME_Y_OFFSET;
        this.camera.update();
        this.batch.setProjectionMatrix(camera.combined);

        this.textureAtlas = mediaManager.get("data/game/game.atlas");

        this.debugRenderer = new ShapeRenderer();
        this.debugRenderer.setProjectionMatrix(camera.combined);
        this.debug = false;
    }

    public void init(MediaManager mediaManager, GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        setBackground(mediaManager);

        cleanEntities();
        loadMainAvatar();
        loadMap(gameStatus.getLevel());
        loadEnemies();
        loadAir();

        loadMealTimer = Constant.MEAL_SPAWN_DELAY;
        loadMealFinished = false;
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
        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).update(deltaTime);
        }
        for (int i = 0; i < food.size; i++) {
            food.get(i).update(deltaTime);
        }
        for (int i = 0; i < projectiles.size; i++) {
            projectiles.get(i).update(deltaTime);
        }
        for (int i = 0; i < pets.size; i++) {
            pets.get(i).update(deltaTime);
        }
        checkSpawnMeals(deltaTime);
        checkSpawnAir(deltaTime);
        checkLevelTime(deltaTime);

        if (enemies.size == 0) {
            boolean enemyTrapped = false;
            for (int i = 0; i < food.size; i++) {
                if (food.get(i).getType().isCandy()) {
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
                    for (int i = 0; i < food.size; i++) {
                        if (food.get(i).getType().isAir()) {
                            food.get(i).setCurrentState(EntityStateFactory.die(food.get(i)));
                            addMeal(EntityFactory.randomVerdure(food.get(i).position.x + food.get(i).dimension.x / 2, food.get(i).position.y, this));
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
                for (Enemy enemy : enemies) {
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

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mainAvatar.left();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mainAvatar.right();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            mainAvatar.jump();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            mainAvatar.fire();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.F9)) {
            debug = !debug;
        }
    }

    @Override
    public void render(float deltaTime) {
        batch.begin();
        batch.draw(background, 0, 0);
        batch.end();

        mapRenderer.render();
        for (int i = 0; i < food.size; i++) {
            food.get(i).render(batch);
        }
        for (int i = 0; i < projectiles.size; i++) {
            projectiles.get(i).render(batch);
        }
        for (int i = 0; i < pets.size; i++) {
            pets.get(i).render(batch);
        }
        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).render(batch);
        }
        mainAvatar.render(batch);
        if (dark != null) {
            dark.render(batch);
        }
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
        cleanEntities();
    }

    private void cleanEntities() {
        for (int i = 0; i < enemies.size; i++) {
            enemies.get(i).free();
        }
        enemies.clear();
        for (int i = 0; i < food.size; i++) {
            food.get(i).free();
        }
        food.clear();
        for (int i = 0; i < projectiles.size; i++) {
            projectiles.get(i).free();
        }
        projectiles.clear();
        for (int i = 0; i < pets.size; i++) {
            pets.get(i).free();
        }
        pets.clear();

        leftGuide.clear();
        rightGuide.clear();

        if (dark != null) {
            dark.free();
        }
    }

    private void loadMap(int levelNumber) {
        map = new TmxMapLoader().load("data/stages/" + levelNumber + ".tmx");
        bossLevel = Boolean.parseBoolean((String) map.getProperties().get("bossLevel"));
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);
        mapRenderer.setView(camera);
    }

    private void loadEnemies() {
        for (MapObject mapObject : map.getLayers().get("enemies").getObjects()) {
            try {
                EnemyType entityType = EnemyType.valueOf(mapObject.getName());
                DirectionType lookingAt = Boolean.parseBoolean((String) mapObject.getProperties().get("type")) ? DirectionType.LEFT : DirectionType.RIGHT;
                float posX = mapObject instanceof RectangleMapObject ? ((RectangleMapObject) mapObject).getRectangle().x : ((TiledMapTileMapObject) mapObject).getX();
                float posY = mapObject instanceof RectangleMapObject ? ((RectangleMapObject) mapObject).getRectangle().y : ((TiledMapTileMapObject) mapObject).getY();
                Enemy enemy = EntityFactory.enemy(entityType, posX, posY, lookingAt, this);
                if (bossLevel && enemy.isBoss()) {
                    ((Boss) enemy).setBoostType(MealType.valueOf((String) map.getProperties().get("bossBoost")));
                }
                enemies.add(enemy);
            } catch (Exception e) {
                Gdx.app.error("Game", "Error parsing enemy value", e);
            }
        }
    }

    private void checkSpawnMeals(float deltaTime) {
        if (!loadMealFinished && loadMealTimer < 0) {
            if (map.getLayers().get(MEAL_LAYER).getObjects().getCount() > 0) {
                MapObject mapObject = map.getLayers().get(MEAL_LAYER).getObjects().get(0);
                MealType mealType = MealType.parseMeal(mapObject.getName());
                float posX = mapObject instanceof RectangleMapObject ? ((RectangleMapObject) mapObject).getRectangle().x : ((TiledMapTileMapObject) mapObject).getX();
                float posY = mapObject instanceof RectangleMapObject ? ((RectangleMapObject) mapObject).getRectangle().y : ((TiledMapTileMapObject) mapObject).getY();
                if (mealType.isBoost()) {
                    food.add(EntityFactory.boost(posX, posY, mealType, this, true));
                } else {
                    food.add(EntityFactory.fruit(posX, posY, mealType, this));
                }
                map.getLayers().get(MEAL_LAYER).getObjects().remove(0);
                loadMealTimer = Constant.MEAL_SPAWN_DELAY;

                if (map.getLayers().get(MEAL_LAYER).getObjects().getCount() == 0) {
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
                    mealType = MealType.parseAir((String) map.getProperties().get("specialAir"));
                }
                switch (mealType) {
                    case AIR:
                        if (leftGuide.size > 0) {
                            food.add(EntityFactory.air(mealType, RaiseType.LEFT_GUIDED, leftGuide.first().x - mealType.getDimension().x / 2, leftGuide.first().y - mealType.getDimension().y / 2, DirectionType.LEFT, this));
                        }
                        if (rightGuide.size > 0) {
                            food.add(EntityFactory.air(mealType, RaiseType.RIGHT_GUIDED, rightGuide.first().x - mealType.getDimension().x / 2, rightGuide.first().y - mealType.getDimension().y / 2, DirectionType.RIGHT, this));
                        }
                        break;
                    case AIR_TORNADO:
                        if (leftGuide.size > 0) {
                            food.add(EntityFactory.tornado(mealType, RaiseType.LEFT_GUIDED, leftGuide.first().x - mealType.getDimension().x / 2, leftGuide.first().y - mealType.getDimension().y / 2, DirectionType.LEFT, this));
                        }
                        if (rightGuide.size > 0) {
                            food.add(EntityFactory.tornado(mealType, RaiseType.RIGHT_GUIDED, rightGuide.first().x - mealType.getDimension().x / 2, rightGuide.first().y - mealType.getDimension().y / 2, DirectionType.RIGHT, this));
                        }
                        break;
                    case AIR_SPIKES:
                        if (leftGuide.size > 0) {
                            food.add(EntityFactory.spikes(mealType, RaiseType.LEFT_GUIDED, leftGuide.first().x - mealType.getDimension().x / 2, leftGuide.first().y - mealType.getDimension().y / 2, DirectionType.LEFT, this));
                        }
                        if (rightGuide.size > 0) {
                            food.add(EntityFactory.spikes(mealType, RaiseType.RIGHT_GUIDED, rightGuide.first().x - mealType.getDimension().x / 2, rightGuide.first().y - mealType.getDimension().y / 2, DirectionType.RIGHT, this));
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
        guidedAir = Boolean.parseBoolean((String) map.getProperties().get("guidedAir"))
                || (Boolean.parseBoolean((String) map.getProperties().get("extraAir"))
                && !mainAvatar.isAirSkilled());
        if (guidedAir) {
            loadGuide(LEFT, leftGuide);
            loadGuide(RIGHT, rightGuide);

            loadAirTimer = Constant.AIR_SPAWN_RATE;
        }
        try {
            automaticAirMode = Integer.valueOf(((String) map.getProperties().get("automaticAirMode")));
        } catch (NumberFormatException e) {
            automaticAirMode = 0;
        }
    }

    private void loadGuide(String id, Array<Constant.Coordinates> guide) {
        if (map.getLayers().get(PATH).getObjects().get(id) != null) {
            float[] vertices = ((PolylineMapObject) map.getLayers().get(PATH).getObjects().get(id)).getPolyline().getVertices();
            float origX = ((PolylineMapObject) map.getLayers().get(PATH).getObjects().get(id)).getPolyline().getX();
            float origY = ((PolylineMapObject) map.getLayers().get(PATH).getObjects().get(id)).getPolyline().getY();
            int i = 0;
            while (i < vertices.length) {
                guide.add(new Constant.Coordinates(origX + vertices[i], origY + vertices[i + 1]));
                i += 2;
            }
        }
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void addMeal(Meal meal) {
        food.add(meal);
    }

    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

    public void addPet(Avatar avatar) {
        pets.add(avatar);
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public TiledMap getMap() {
        return map;
    }

    public void mainAvatarDead() {
        initLevelTime();
        if (dark != null) {
            dark.setCurrentState(EnemyStateFactory.explode(dark, false));
        }
        for (Enemy enemy : enemies) {
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
