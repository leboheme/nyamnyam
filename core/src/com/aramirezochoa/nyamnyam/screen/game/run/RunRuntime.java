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
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.Projectile;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;
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
    private final GameplayCamera camera = new GameplayCamera();
    private final RoomRenderer roomRenderer;
    private final SpriteBatch entityBatch = new SpriteBatch();
    private final Array<Constant.Coordinates> leftGuide = new Array<Constant.Coordinates>();
    private final Array<Constant.Coordinates> rightGuide = new Array<Constant.Coordinates>();
    private final CollisionWorld collisionWorld;

    public RunRuntime(MediaManager mediaManager, RunState runState, GameStatus gameStatus) {
        this.runState = runState;
        this.gameStatus = gameStatus;
        this.mediaManager = mediaManager;
        textureAtlas = mediaManager.get("data/game/game.atlas");
        roomRenderer = new RoomRenderer(mediaManager.get("data/stages/tiles.png"));
        collisionWorld = new RoomCollisionWorld(runState.getCurrentRoom());
        loadMainAvatar();
    }

    public void update(float deltaTime) {
        MainAvatar player = entityWorld.getMainAvatar();
        if (player != null) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) player.left();
            if (Gdx.input.isKeyPressed(Input.Keys.D)) player.right();
            if (Gdx.input.isKeyPressed(Input.Keys.W)) player.jump();
        }
        entityWorld.update(deltaTime);
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
    }

    public void resize(int width, int height) {
        camera.resize(width, height);
    }

    public void dispose() {
        if (entityWorld.getMainAvatar() != null) {
            entityWorld.getMainAvatar().free();
            entityWorld.setMainAvatar(null);
        }
        entityWorld.clear();
        roomRenderer.dispose();
        entityBatch.dispose();
    }

    private void loadMainAvatar() {
        MainAvatar avatar;
        if (gameStatus.getLives() > 0) {
            avatar = EntityFactory.mainAvatar(DataManager.INSTANCE.getCurrentCharacter(),
                    Constant.MAIN_AVATAR_SPAWN_POSITION.x, Constant.MAIN_AVATAR_SPAWN_POSITION.y,
                    DirectionType.RIGHT, this);
        } else {
            avatar = EntityFactory.empty(this);
        }
        entityWorld.setMainAvatar(avatar);
    }

    @Override public TextureAtlas getTextureAtlas() { return textureAtlas; }
    @Override public MediaManager getMediaManager() { return mediaManager; }
    @Override public GameStatus getGameStatus() { return gameStatus; }
    @Override public EntityWorld getEntityWorld() { return entityWorld; }
    @Override public CollisionWorld getCollisionWorld() { return collisionWorld; }
    @Override public void addEnemy(Enemy enemy) { entityWorld.addEnemy(enemy); }
    @Override public void addMeal(Meal meal) { entityWorld.addMeal(meal); }
    @Override public void addProjectile(Projectile projectile) { entityWorld.addProjectile(projectile); }
    @Override public void addPet(Avatar avatar) { entityWorld.addPet(avatar); }
    @Override public void mainAvatarDead() { loadMainAvatar(); }
    @Override public void notifyMessage(String message, float x, float y) { }
    @Override public int getAutomaticAirMode() { return 0; }
    @Override public Array<Constant.Coordinates> getLeftGuide() { return leftGuide; }
    @Override public Array<Constant.Coordinates> getRightGuide() { return rightGuide; }

    public MainAvatar getMainAvatar() {
        return entityWorld.getMainAvatar();
    }
}
