package com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.candy;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.EnemyType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.MealStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.common.RaiseType;

/**
 * Created by boheme on 14/01/15.
 */
public class Candy extends Meal {

    public float timerState;
    public EnemyType enemyTrappedType;
    private RaiseType raiseType;

    public void init(Enemy enemy) {
        raiseType = enemy.getParent().getBubbleRaiseType();
        super.init(MealType.randomCandy(), enemy.position.x, enemy.position.y, lookingAt, enemy.getParent());
        timerState = Constant.CANDY_TIME_TO_LOOSEN;
        enemyTrappedType = (EnemyType) enemy.getType();
        velocity.y = Constant.CANDY_VELOCITY;
    }

    @Override
    protected EntityState getInitialState() {
        return EntityStateFactory.sequence(MealStateFactory.raise(this, raiseType), MealStateFactory.liberate(this));
    }

    @Override
    public void reset() {
        super.reset();
        raiseType = RaiseType.AUTOMATIC;
    }

    @Override
    protected int getEatenScore() {
        getParent().getMediaManager().playSound(MediaManager.SOUND_NYAM_CANDY);
        return Constant.SCORE_CANDY;
    }

    @Override
    protected void staffBeforeEaten(Avatar avatar) {
        super.staffBeforeEaten(avatar);
        // check other air / candies
        Meal meal;
        for (int i = 0; i < getParent().getEntityWorld().getFood().size; i++) {
            meal = getParent().getEntityWorld().getFood().get(i);
            if (meal.isEnabled()
                    && meal.box.overlaps(box)
                    && (meal.getType().isAir() || meal.getType().isCandy())) {
                meal.eaten(avatar);
            }
        }
    }
}
