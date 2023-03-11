package com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.air;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.MealStateFactory;

/**
 * Created by boheme on 3/02/15.
 */
public class Air extends AbstractAir {

    @Override
    protected EntityState getInitialState() {
        return EntityStateFactory.sequence(MealStateFactory.raise(this, raiseType), MealStateFactory.explode(this, false));
    }

    @Override
    protected int getEatenScore() {
        return Constant.SCORE_AIR;
    }

}
