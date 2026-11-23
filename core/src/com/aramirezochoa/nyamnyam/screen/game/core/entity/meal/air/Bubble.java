package com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.air;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.MealStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.GameplayContext;

/** Empty bubble used by the JSON room runtime. */
public class Bubble extends AbstractAir {

    public void init(float x, float y, DirectionType lookingAt, GameplayContext context) {
        super.init(MealType.AIR, x, y, lookingAt, context, context.getBubbleRaiseType());
    }

    @Override
    protected EntityState getInitialState() {
        return EntityStateFactory.sequence(MealStateFactory.raise(this, raiseType), MealStateFactory.explode(this, false));
    }

    @Override
    protected int getEatenScore() {
        return Constant.SCORE_AIR;
    }
}
