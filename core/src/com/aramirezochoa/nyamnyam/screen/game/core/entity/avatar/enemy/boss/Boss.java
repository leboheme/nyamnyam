package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.boss;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.i18n.LanguageManager;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.fruit.Fruit;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy.EnemyStateFactory;

/**
 * Created by boheme on 5/02/15.
 */
public abstract class Boss extends Enemy {

    private int lives, maxLives;
    private MealType boostType;

    @Override
    public void init(EntityType type, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper) {
        super.init(type, x, y, lookingAt, gameWrapper);
        lives = getLives();
        maxLives = lives;
    }

    protected abstract int getLives();

    protected float getWalkVelocity() {
        return Constant.BOSS_VELOCITY;
    }

    @Override
    protected float getBoxOffset() {
        return Constant.BOSS_BOX_OFFSET;
    }

    @Override
    protected void updateState(float deltaTime) {
        super.updateState(deltaTime);
        if (enabled && lives <= 0) {
            setCurrentState(EnemyStateFactory.explode(this, true));
            createFruits();
        } else {
            if (!getParent().mainAvatar.isSpecialBusted() && !isBoostAlive()) {
                getParent().addMeal(EntityFactory.boost((Constant.SCREEN_WIDTH - boostType.getDimension().x) / 2,
                        Constant.SCREEN_HEIGHT + Constant.SCREEN_GAME_Y_OFFSET - boostType.getDimension().y, boostType, getParent(), false));
            }
        }
    }

    private boolean isBoostAlive() {
        for (int i = 0; i < getParent().food.size; i++) {
            if (getParent().food.get(i).getType().isBoost()) return true;
        }
        return false;
    }

    private void createFruits() {
        createFruite(-getVelocity() * 2, 200);
        createFruite(-getVelocity() * 1.3f, 200);
        createFruite(-getVelocity() * 0.6f, 200);
        createFruite(getVelocity() * 0.6f, 200);
        createFruite(getVelocity() * 1.3f, 200);
        createFruite(getVelocity() * 2, 200);
    }

    private void createFruite(float x, float y) {
        Fruit fruit = EntityFactory.fruit(position.x + dimension.x / 2, position.y, MealType.randomFruit(), getParent());
        fruit.velocity.x = x;
        fruit.velocity.y = y;
        getParent().addMeal(fruit);
    }

    @Override
    public boolean hit(boolean transformToCandy) {

        if (!transformToCandy) {
            getParent().getMediaManager().playSound(MediaManager.SOUND_HIT_PROJECTILE);
            getParent().getGameStatus().addScore(getEatenScore(), position.x + (dimension.x / 2), position.y + dimension.y - Constant.SCREEN_GAME_Y_OFFSET);
            lives--;
            if (lives < 0) lives = 0;
            getParent().notifyMessage(LanguageManager.INSTANCE.getString("bossLives") + ": " + lives + "/" + maxLives, position.x + dimension.x / 2,
                    position.y + dimension.y - Constant.SCREEN_GAME_Y_OFFSET);
        }

        return true;
    }

    @Override
    public void dampVelocity() {
        // nothing
    }

    @Override
    public boolean isBoss() {
        return true;
    }

    @Override
    public boolean isEdible() {
        return false;
    }

    public void setBoostType(MealType boost) {
        this.boostType = boost;
    }

    @Override
    protected boolean isLoopAnimation() {
        return true;
    }
}
