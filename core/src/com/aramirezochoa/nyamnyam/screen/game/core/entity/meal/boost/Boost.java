package com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.boost;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.MealStateFactory;

/**
 * Created by leboheme on 20/01/15.
 */
public class Boost extends Meal {

    private boolean expiring;

    public void init(float x, float y, MealType boostType, GameWrapper gameWrapper, boolean expiring) {
        this.expiring = expiring;
        super.init(boostType, x, y, lookingAt, gameWrapper);
        acceleration.y = 0;
    }

    @Override
    protected EntityState getInitialState() {
        if (expiring) {
            return MealStateFactory.sequence(MealStateFactory.idle(this, Constant.MEAL_EXPIRATION_TIME), MealStateFactory.explode(this, false));
        } else {
            return MealStateFactory.sequence(MealStateFactory.idle(this), MealStateFactory.explode(this, false));
        }
    }

    @Override
    protected int getEatenScore() {
        return Constant.SCORE_BOOST;
    }

    @Override
    protected String getStatusCode() {
        return type.name();
    }

    @Override
    public void reset() {
        super.reset();
        acceleration.y = 0;
        expiring = false;
    }
}
