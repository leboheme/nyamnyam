package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.candy.LiberateCandyState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.common.RaiseType;

/**
 * Created by boheme on 15/01/15.
 */
public class MealStateFactory extends EntityStateFactory {

    public static EntityState raise(Meal meal, RaiseType raiseType) {
        MealState state = state(raiseType.getRaiseClass());
        state.setMeal(meal);
        return state;
    }

    public static EntityState liberate(Meal meal) {
        LiberateCandyState state = state(LiberateCandyState.class);
        state.setMeal(meal);
        return state;
    }

}
