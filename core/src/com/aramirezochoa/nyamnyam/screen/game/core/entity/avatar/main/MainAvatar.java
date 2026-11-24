package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.DataManager;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.core.CollisionLayer;
import com.aramirezochoa.nyamnyam.screen.game.core.GameplayContext;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.Entity;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.main.MainAvatarStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.main.SpawnEntityState;
import com.aramirezochoa.nyamnyam.screen.game.run.RunStats;

/**
 * Created by boheme on 13/01/15.
 */
public abstract class MainAvatar extends Avatar {

    public float fireTimer, spawnTimer, garlicBoostTimer;
    protected boolean fireEnabled;
    protected boolean jumpIfPossible;
    private boolean jumpRequested;
    private boolean petBoost;
    protected boolean garlicBoost;
    private boolean chilliBoost;
    private boolean tornadoBoost;
    protected boolean secondJump;

    private Color color = new Color(1, 1, 1, 1);
    private float radians;
    private Rectangle topBox = new Rectangle();
    private Rectangle bottomBox = new Rectangle();

    @Override
    public void init(EntityType type, float x, float y, DirectionType lookingAt, GameplayContext gameWrapper) {
        super.init(type, x, y, lookingAt, gameWrapper);
        setCollisionLayer(CollisionLayer.PLAYER);
        fireTimer = 0;
        fireEnabled = true;
        jumpIfPossible = false;
        jumpRequested = false;
        secondJump = false;
        petBoost = false;
        garlicBoost = false;
        chilliBoost = false;
        tornadoBoost = false;
        spawnTimer = getSpawnTime() * 2;
        color.a = 1;
        radians = 0;
    }

    @Override
    public float getSpawnTime() {
        return Constant.MAIN_AVATAR_TIME_SPAWN;
    }

    @Override
    protected void updateState(float deltaTime) {
        super.updateState(deltaTime);
        if (!fireEnabled) {
            fireTimer -= deltaTime;
            if (fireTimer <= 0) {
                fireEnabled = true;
            }
        }
        if (spawnTimer > 0) {
            spawnTimer -= deltaTime;
            radians += 10f;
            color.a = MathUtils.sinDeg(radians);
        }
        if (garlicBoost) {
            garlicBoostTimer -= deltaTime;
            if (garlicBoostTimer < 0) {
                scareEnemies(false);
            }
        }
        if (grounded) {
            secondJump = false;
        }
    }

    @Override
    protected EntityState getInitialState() {
        return MainAvatarStateFactory.spawn(this);
    }

    @Override
    protected void initBoxes() {
        super.initBoxes();
        bottomBox.width = topBox.width = box.width;
        bottomBox.height = box.height / 4;
        topBox.height = box.height * 3 / 4;
    }

    @Override
    protected void updateBoxes() {
        super.updateBoxes();
        bottomBox.x = box.x;
        bottomBox.y = box.y;
        topBox.x = box.x;
        topBox.y = box.y + box.height / 4;
    }

    @Override
    protected void drawBoxes() {
        GameWrapper.debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        GameWrapper.debugRenderer.setColor(Color.ORANGE);
        GameWrapper.debugRenderer.rect(topBox.x, topBox.y, topBox.width, topBox.height);
        GameWrapper.debugRenderer.rect(bottomBox.x, bottomBox.y, bottomBox.width, bottomBox.height);
        GameWrapper.debugRenderer.end();
    }

    @Override
    protected void checkCollisions() {
        if (canPerformAction()) {
            for (Meal meal : getParent().getEntityWorld().getFood()) {
                if (meal.box.overlaps(box)
                        && (getParent().getDropService() == null
                        || CollisionLayer.canCollide(getCollisionLayer(), meal.getCollisionLayer()))) {
                    checkEdible(meal);
                }
            }
            for (int i = 0; i < getParent().getEntityWorld().getEnemies().size; i++) {
                if (getParent().getEntityWorld().getEnemies().get(i).isEnabled()
                        && (getParent().getDropService() == null
                        || CollisionLayer.canCollide(getCollisionLayer(), getParent().getEntityWorld().getEnemies().get(i).getCollisionLayer()))
                        && box.overlaps(getParent().getEntityWorld().getEnemies().get(i).box)) {
                    if (garlicBoost && getParent().getEntityWorld().getEnemies().get(i).isEdible()) {
                        eat(getParent().getEntityWorld().getEnemies().get(i));
                    } else {
                        hit();
                    }
                }
            }
            jumpIfPossible = false;
        }
    }

    protected void checkEdible(Meal meal) {
        // JSON common food is collected by its resource service, without legacy eating rewards.
        if (getParent().getDropService() != null && (meal.getType().isFruit() || meal.getType().isVerdure())) return;
        if (meal.isEnabled()) {
            if (meal.getType().isBoost()) {
                checkBoost(meal);
            } else if (meal.getType().isVerdure() || meal.getType().isFruit()) {
                getParent().getMediaManager().playSound(MediaManager.SOUND_NYAM_MEAL);
                eat(meal);
            } else {
                // Check collision in detail
                if (meal.bottomBox.overlaps(topBox)) {
                    if (velocity.y >= 0) {
                        eat(meal);
                    }
                } else if ((meal.topBox.overlaps(bottomBox))) {
                    if (velocity.y <= 0) {
                        if (jumpIfPossible) {
                            createJump();
                            jumpIfPossible = false;
                            secondJump = false;
                        } else {
                            eat(meal);
                        }
                    }
                } else if (meal.leftBox.overlaps(box)) {
                    pushOrBePushed(meal);
                } else if (meal.rightBox.overlaps(box)) {
                    pushOrBePushed(meal);
                }
            }
        }
    }

    private void pushOrBePushed(Meal meal) {
        if (velocity.x != 0) { // push
            if (meal != null && meal.getLookingAt() != null && lookingAt.equals(meal.getLookingAt().flip())) {
                meal.velocity.x = velocity.x * 1.5f;
            }
        } else { // be pushed
            velocity.x = Constant.AVATAR_WALK_VELOCITY * Math.signum(meal.velocity.x);
        }
    }

    protected void createJump() {
        getParent().getMediaManager().playSound(MediaManager.SOUND_JUMP);
        velocity.y = getJumpVelocity();
    }

    private void checkBoost(Meal boost) {
        if (boost.isEnabled()) {
            switch (boost.getType()) {
                case BOOST_PIZZA:
                    getParent().getGameStatus().enableMovementBoost();
                    break;
                case BOOST_BURGER:
                    getParent().getGameStatus().enableFireLapseBoost();
                    break;
                case BOOST_MEAT:
                    getParent().getGameStatus().enableFireDistanceBoost();
                    break;
                case BOOST_HOTDOG:
                    getParent().getGameStatus().enableFireVelocityBoost();
                    break;
                case BOOST_LIVE:
                    getParent().getGameStatus().increaseLive();
                    break;
                case BOOST_CATERPILLAR:
                    getParent().addPet(EntityFactory.tfly(this));
                    petBoost = true;
                    break;
                case BOOST_EGG:
                    getParent().addPet(EntityFactory.gluty(boost.position.x, boost.position.y, lookingAt, getParent()));
                    petBoost = true;
                    break;
                case BOOST_GARLIC:
                    scareEnemies(true);
                    break;
                case BOOST_CHILLI:
                    chilliBoost = true;
                    break;
                case BOOST_TORNADO:
                    tornadoBoost = true;
                    break;
            }
            eat(boost);
            if (!MealType.BOOST_LIVE.equals(boost.getType())) {
                getParent().getMediaManager().playSound(MediaManager.SOUND_NYAM_BOOST);
            }
        }
    }

    private void scareEnemies(boolean scare) {
        garlicBoost = scare;
        garlicBoostTimer = Constant.MAIN_AVATAR_TIME_GARLIC_BOOST;
        for (int i = 0; i < getParent().getEntityWorld().getEnemies().size; i++) {
            getParent().getEntityWorld().getEnemies().get(i).setScared(scare);
        }
    }

    protected void eat(Entity entity) {
        setCurrentState(MainAvatarStateFactory.eat(this, entity));
    }

    public abstract void left();

    public abstract void right();

    public void jump() {
        if (canPerformAction()) {
            jumpRequested = true;
            if (grounded) {
                createJump();
            } else if (velocity.y < 0) {
                jumpIfPossible = true;
            }
        }
    }

    /** Consumed by the runtime after input, before entity updates clear legacy jump flags. */
    public boolean consumeJumpRequest() {
        boolean requested = jumpRequested;
        jumpRequested = false;
        return requested;
    }

    public void rebound() {
        createJump();
        grounded = false;
        secondJump = false;
    }

    public abstract void fire();

    public void createFire() {
        if ((chilliBoost || tornadoBoost || !garlicBoost) && !tryPayForFire()) return;
        if (chilliBoost) {
            getParent().getMediaManager().playSound(MediaManager.SOUND_SHOT_FIRE);
            getParent().addProjectile(EntityFactory.projectile(ProjectileType.FIRE_BALL, this, getLookingAt(), true, false));
            resetFire();
        } else if (tornadoBoost) {
            getParent().getMediaManager().playSound(MediaManager.SOUND_SHOT_FIRE);
            getParent().addProjectile(EntityFactory.projectile(ProjectileType.BEAM, this, getLookingAt(), true, true));
            resetFire();
        } else if (!garlicBoost) {
            createNormalFire();
            resetFire();
        }
    }

    /** Called only when this action will emit an attack; legacy attacks are free. */
    protected boolean tryPayForFire() {
        return true;
    }

    protected abstract void createNormalFire();

    public void resetFire() {
        fireEnabled = false;
        float boostRate = (getParent().getGameStatus().isFireLapseBoost() || isSpecialBusted())
                ? Constant.MAIN_AVATAR_BOOST_FIRE_RATE : 1f;
        float baseInterval = Constant.MAIN_AVATAR_FIRE_TIME_LAPSE / boostRate;
        if (getParent().getRunStats() == null) {
            fireTimer = baseInterval;
        } else {
            float cadence = getParent().getRunStats().getValue(RunStats.Stat.CADENCE, 1f / baseInterval);
            fireTimer = cadence <= 0f ? Float.MAX_VALUE : 1f / cadence;
        }
    }

    @Override
    public float getFireDistance() {
        return Constant.BEAM_DISTANCE * (getParent().getGameStatus().isFireDistanceBoost() || isSpecialBusted() ? Constant.MAIN_AVATAR_BOOST_FIRE_DISTANCE : 1f);
    }

    @Override
    public float getFireVelocity() {
        return Constant.BEAM_VELOCITY * (getParent().getGameStatus().isFireVelocityBoost() || isSpecialBusted() ? Constant.MAIN_AVATAR_BOOST_FIRE_VELOCITY : 1f);
    }

    public boolean canPerformAction() {
        synchronized (enabled) {
            return enabled && !currentState.getCode().equals(SpawnEntityState.CODE);
        }
    }

    @Override
    public void die() {
        super.die();
        getParent().mainAvatarDead();
    }

    public void dieStuff() {
        DataManager.INSTANCE.getGameStatus().decreaseLive();
        if (petBoost) {
            for (int i = 0; i < getParent().getEntityWorld().getPets().size; i++) {
                getParent().getEntityWorld().getPets().get(i).die();
            }
        }
    }

    public void hit() {
        if (getParent().getSurvivalService() != null) {
            getParent().getSurvivalService().hit(this);
            return;
        }
        synchronized (enabled) {
            if (enabled && spawnTimer < 0) {
                enabled = false;
                getParent().getMediaManager().playSound(MediaManager.SOUND_NYAM_HIT);
                setCurrentState(MainAvatarStateFactory.dead(this));
            }
        }
    }

    @Override
    protected Color getColor() {
        if (spawnTimer < 0) {
            return super.getColor();
        } else {
            return color;
        }
    }

    public boolean isSpecialBusted() {
        return chilliBoost || tornadoBoost;
    }

    public boolean isTornadoBoost() {
        return tornadoBoost;
    }

    public abstract boolean isAirSkilled();
}
