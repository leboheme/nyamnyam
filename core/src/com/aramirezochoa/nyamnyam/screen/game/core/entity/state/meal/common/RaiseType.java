package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.common;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.MealState;

/**
 * Created by boheme on 3/02/15.
 */
public enum RaiseType {

    AUTOMATIC(RaiseAutomaticMealState.class),
    LEFT_GUIDED(RaiseLeftGuidedMealState.class),
    RIGHT_GUIDED(RaiseRightGuidedMealState.class);

    private final Class<? extends MealState> raiseClass;

    RaiseType(Class<? extends MealState> raiseClass) {
        this.raiseClass = raiseClass;
    }

    public Class<? extends MealState> getRaiseClass() {
        return raiseClass;
    }
}
