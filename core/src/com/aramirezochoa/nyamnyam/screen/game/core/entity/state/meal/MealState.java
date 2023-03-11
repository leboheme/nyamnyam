package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.Meal;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;

/**
 * Created by boheme on 15/01/15.
 */
public abstract class MealState extends EntityState {

    protected Meal meal;

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

}
