package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.common;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.MealState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;

/** Keeps a bubble in place for the legacy raise duration before its next state. */
public class RaiseStaticMealState extends MealState {

    private static final String COLD = "RAISE_COLD";
    private static final String WARM = "RAISE_WARM";
    private static final String HOT = "RAISE_HOT";

    private float timer1 = Constant.MEAL_RAISE_TIME_1;
    private float timer2 = Constant.MEAL_RAISE_TIME_2;
    private float timer3 = Constant.MEAL_RAISE_TIME_3;

    @Override
    public boolean update(float deltaTime) {
        meal.acceleration.set(0, -Constant.GRAVITY.y);
        meal.getParent().getBubbleAttractionService().setVelocity(meal.getId(), meal.position.x + meal.dimension.x / 2,
                meal.position.y + meal.dimension.y / 2, meal.velocity);
        GameWrapperAssistant.updateEntity(meal, deltaTime, true,
                meal.getParent().getBubbleAttractionService().getBaseSpeed());
        return checkTimers(deltaTime);
    }

    private boolean checkTimers(float deltaTime) {
        if (timer1 < 0) {
            if (timer2 < 0) {
                if (timer3 < 0) {
                    return true;
                }
                timer3 -= deltaTime;
            }
            timer2 -= deltaTime;
        }
        timer1 -= deltaTime;
        return false;
    }

    @Override
    public String getCode() {
        if (timer1 > 0) {
            return COLD;
        } else if (timer2 > 0) {
            return WARM;
        } else {
            return HOT;
        }
    }

    @Override
    public void reset() {
        timer1 = Constant.MEAL_RAISE_TIME_1;
        timer2 = Constant.MEAL_RAISE_TIME_2;
        timer3 = Constant.MEAL_RAISE_TIME_3;
    }
}
