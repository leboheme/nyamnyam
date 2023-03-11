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

    public void init(Enemy enemy) {
        super.init(MealType.randomCandy(), enemy.position.x, enemy.position.y, lookingAt, enemy.getParent());
        timerState = Constant.CANDY_TIME_TO_LOOSEN;
        enemyTrappedType = (EnemyType) enemy.getType();
        velocity.y = Constant.CANDY_VELOCITY;
    }

    @Override
    protected EntityState getInitialState() {
        return EntityStateFactory.sequence(MealStateFactory.raise(this, RaiseType.AUTOMATIC), MealStateFactory.liberate(this));
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
        for (int i = 0; i < getParent().food.size; i++) {
            meal = getParent().food.get(i);
            if (meal.isEnabled()
                    && meal.box.overlaps(box)
                    && (meal.getType().isAir() || meal.getType().isCandy())) {
                meal.eaten(avatar);
            }
        }
    }
}
