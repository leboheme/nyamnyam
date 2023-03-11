package com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.air;

import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.common.RaiseType;

/**
 * Created by boheme on 3/02/15.
 */
public abstract class AbstractAir extends Meal {

    protected RaiseType raiseType;

    public void init(EntityType type, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper, RaiseType raiseType) {
        this.raiseType = raiseType;
        super.init(type, x, y, lookingAt, gameWrapper);
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
