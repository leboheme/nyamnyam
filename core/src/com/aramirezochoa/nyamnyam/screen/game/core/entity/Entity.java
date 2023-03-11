package com.aramirezochoa.nyamnyam.screen.game.core.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.common.DieEntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.common.ExplodeEntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.MealStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.candy.LiberateCandyState;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by boheme on 13/01/15.
 */
public abstract class Entity implements Pool.Poolable {

    protected GameWrapper parent;
    protected Pool pool;
    protected EntityType type;
    protected DirectionType lookingAt;
    protected EntityState currentState;
    public boolean dead;
    public volatile Boolean enabled = Boolean.FALSE;

    public Vector2 dimension = new Vector2(0f, 0f);
    public Rectangle box = new Rectangle();
    public Vector2 position = new Vector2(0f, 0f);
    public Vector2 velocity = new Vector2(0f, 0f);
    public Vector2 acceleration = new Vector2(0f, 0f);

    // Frame & animations
    private TextureRegion currentFrame;
    private float currentFrameTimer;
    private Animation<TextureRegion> currentAnimation;
    private String currentAnimationKey;
    private Map<String, Animation> animations;
    public boolean grounded;

    public void init(EntityType type, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper) {
        this.type = type;
        this.lookingAt = lookingAt;
        this.dimension.x = type.getDimension().x;
        this.dimension.y = type.getDimension().y;
        this.velocity.x = this.velocity.y = 0;
        this.acceleration.x = this.acceleration.y = 0;
        this.position.x = x;
        this.position.y = y;
        initBoxes();
        updateBoxes();
        this.currentState = getInitialState();
        this.currentState.init();
        this.dead = false;
        this.enabled = true;
        this.animations = new HashMap<String, Animation>();
        this.currentAnimation = new Animation(0.1f, gameWrapper.getTextureAtlas().findRegions(getFullCode()));
        this.currentFrameTimer = 0;
        this.currentFrame = currentAnimation.getKeyFrame(currentFrameTimer);
        this.currentAnimationKey = getFullCode();
        this.animations.put(currentAnimationKey, currentAnimation);
        if (isLoopAnimation()) {
            this.currentAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }
        this.parent = gameWrapper;
        this.grounded = false;
    }

    protected String getFullCode() {
        if (LiberateCandyState.CODE.equals(currentState.getCode())
                || ExplodeEntityState.CODE.equals(currentState.getCode())) {
            return ExplodeEntityState.CODE;
        } else if (DieEntityState.CODE.equals(currentState.getCode())) {
            return DieEntityState.CODE;
        } else if (ExplodeEntityState.BIG_EXPLOSION.equals(currentState.getCode())) {
            return ExplodeEntityState.BIG_EXPLOSION;
        } else {
            return getStatusCode();
        }
    }

    protected String getStatusCode() {
        return type.name() + "_" + currentState.getCode();
    }

    public void update(float deltaTime) {
        updateState(deltaTime);
        updateBoxes();
        if (dead) {
            remove();
        } else {
            checkCollisions();
            try {
                updateAnimation(deltaTime);
            } catch (Exception e) {
                Gdx.app.error("Entity " + getClass(), "Full code: " + getFullCode());
            }
        }
    }

    protected void initBoxes() {
        box.set(position.x + getBoxOffset(), position.y + getBoxOffset(), dimension.x - 2 * getBoxOffset(), dimension.y - 2 * getBoxOffset());
    }

    protected void updateBoxes() {
        box.x = position.x + getBoxOffset();
        box.y = position.y + getBoxOffset();
    }

    protected float getBoxOffset() {
        return Constant.ENTITY_BOX_OFFSET;
    }

    protected void updateState(float deltaTime) {
        if (currentState.update(deltaTime)) {
            setCurrentState(getDefaultIdleState());
        }
    }

    protected abstract EntityState getInitialState();

    protected EntityState getDefaultIdleState() {
        return EnemyStateFactory.idle(this);
    }

    public void setCurrentState(EntityState newState) {
        if (currentState != null) {
            currentState.free();
        }
        currentState = newState;
    }

    protected abstract void checkCollisions();

    public void remove() {
        free();
    }

    public void free() {
        currentState.free();
        pool.free(this);
    }

    private void updateAnimation(float deltaTime) {
        if (DirectionType.RIGHT.equals(lookingAt) && velocity.x < 0
                || DirectionType.LEFT.equals(lookingAt) && velocity.x > 0) {
            lookingAt = lookingAt.flip();
        }
        String newAnimationKey = getFullCode();
        if (!currentAnimationKey.equals(newAnimationKey)) {
            if (!animations.containsKey(newAnimationKey)) {
                animations.put(newAnimationKey, new Animation(0.1f, getTextureAtlas().findRegions(newAnimationKey)));
            }
            currentAnimation = animations.get(newAnimationKey);
            if (isLoopAnimation()) {
                currentAnimation.setPlayMode(Animation.PlayMode.LOOP);
            }
            currentAnimationKey = newAnimationKey;
            currentFrameTimer = 0;
        } else {
            currentFrameTimer += deltaTime;
        }
        currentFrame = currentAnimation.getKeyFrame(currentFrameTimer);
    }

    protected abstract boolean isLoopAnimation();

    public void render(Batch batch) {
        boolean flip = DirectionType.LEFT.equals(lookingAt);
        currentFrame.flip(flip, false);
        batch.begin();
        batch.setColor(getColor());
        batch.draw(currentFrame, position.x - (currentFrame.getRegionWidth() - dimension.x) / 2, position.y);
        batch.setColor(1, 1, 1, 1);
        batch.end();
        currentFrame.flip(flip, false);
        if (GameWrapper.debug) {
            drawBoxes();
        }
    }

    protected void drawBoxes() {
        GameWrapper.debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        GameWrapper.debugRenderer.setColor(Color.BLUE);
        GameWrapper.debugRenderer.rect(box.x, box.y, box.width, box.height);
        GameWrapper.debugRenderer.end();
    }

    protected Color getColor() {
        return Color.WHITE;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    @Override
    public void reset() {

    }

    public DirectionType getLookingAt() {
        return lookingAt;
    }

    public void setLookingAt(DirectionType lookingAt) {
        this.lookingAt = lookingAt;
    }

    public GameWrapper getParent() {
        return parent;
    }

    public TextureAtlas getTextureAtlas() {
        return getParent().getTextureAtlas();
    }

    public EntityType getType() {
        return type;
    }

    public void die() {
        dead = true;
    }

    public abstract void dampVelocity();

    public void eaten(Avatar avatar) {
        enabled = false;
        getParent().getGameStatus().addScore(getEatenScore(), position.x + (dimension.x / 2), position.y + dimension.y - Constant.SCREEN_GAME_Y_OFFSET);
        staffBeforeEaten(avatar);
        setCurrentState(MealStateFactory.explode(this, false));
    }

    protected void staffBeforeEaten(Avatar avatar) {
        // do something before exploding
    }

    protected int getEatenScore() {
        return 0;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void notifyWall() {
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public float getMaxNegativeVelocity() {
        return Constant.MAX_NEGATIVE_VELOCITY;
    }

    public float getMaxPositiveVelocity() {
        return -2 * Constant.MAX_NEGATIVE_VELOCITY;
    }
}
