package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.DataManager;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
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

/**
 * Created by boheme on 13/01/15.
 */
public abstract class MainAvatar extends Avatar {

    public float fireTimer, spawnTimer, garlicBoostTimer;
    protected boolean fireEnabled;
    protected boolean jumpIfPossible;
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
    public void init(EntityType type, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper) {
        super.init(type, x, y, lookingAt, gameWrapper);
        fireTimer = 0;
        fireEnabled = true;
        jumpIfPossible = false;
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
        if (velocity.y == 0) {
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
            for (Meal meal : getParent().food) {
                if (meal.box.overlaps(box)) checkEdible(meal);
            }
            for (int i = 0; i < getParent().enemies.size; i++) {
                if (getParent().enemies.get(i).isEnabled()
                        && box.overlaps(getParent().enemies.get(i).box)) {
                    if (garlicBoost && getParent().enemies.get(i).isEdible()) {
                        eat(getParent().enemies.get(i));
                    } else {
                        hit();
                    }
                }
            }
            jumpIfPossible = false;
        }
    }

    protected void checkEdible(Meal meal) {
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
        for (int i = 0; i < getParent().enemies.size; i++) {
            getParent().enemies.get(i).setScared(scare);
        }
    }

    protected void eat(Entity entity) {
        setCurrentState(MainAvatarStateFactory.eat(this, entity));
    }

    public abstract void left();

    public abstract void right();

    public void jump() {
        if (canPerformAction()) {
            if (velocity.y == 0) {
                createJump();
            } else if (velocity.y < 0) {
                jumpIfPossible = true;
            }
        }
    }

    public abstract void fire();

    public void createFire() {
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

    protected abstract void createNormalFire();

    public void resetFire() {
        fireEnabled = false;
        fireTimer = Constant.MAIN_AVATAR_FIRE_TIME_LAPSE / ((getParent().getGameStatus().isFireLapseBoost() || isSpecialBusted()) ? Constant.MAIN_AVATAR_BOOST_FIRE_RATE : 1);
    }

    @Override
    public float getFireDistance() {
        return Constant.BEAM_DISTANCE * (getParent().getGameStatus().isFireDistanceBoost() || isSpecialBusted() ? Constant.MAIN_AVATAR_BOOST_FIRE_DISTANCE : 1f);
    }

    @Override
    public float getFireVelocity() {
        return Constant.BEAM_VELOCITY * (getParent().getGameStatus().isFireVelocityBoost() || isSpecialBusted() ? Constant.MAIN_AVATAR_BOOST_FIRE_VELOCITY : 1f);
    }

    protected boolean canPerformAction() {
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
            for (int i = 0; i < getParent().pets.size; i++) {
                getParent().pets.get(i).die();
            }
        }
    }

    public void hit() {
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
