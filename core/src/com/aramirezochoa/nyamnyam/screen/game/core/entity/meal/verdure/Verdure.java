package com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.verdure;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.MealStateFactory;

/**
 * Created by boheme on 6/02/15.
 */
public class Verdure extends Meal {

    public void init(float x, float y, MealType boostType, GameWrapper gameWrapper) {
        super.init(boostType, x, y, lookingAt, gameWrapper);
        acceleration.y = 0;
    }

    @Override
    protected EntityState getInitialState() {
        return MealStateFactory.sequence(MealStateFactory.idle(this, Constant.MEAL_EXPIRATION_TIME), MealStateFactory.explode(this, false));
    }

    @Override
    protected int getEatenScore() {
        return Constant.SCORE_VERDURE;
    }

    @Override
    protected String getStatusCode() {
        return type.name();
    }

    @Override
    public void reset() {
        super.reset();
        acceleration.y = 0;
    }

    @Override
    public void dampVelocity() {
        velocity.x *= Constant.VERDURE_DAMPING;
    }

}
