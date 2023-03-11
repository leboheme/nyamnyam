package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.common;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.MealState;

/**
 * Created by boheme on 15/01/15.
 */
public class RaiseAutomaticMealState extends MealState {

    private static final int CENTER_TRUE = 0; // default
    private static final int CENTER_FALSE = 1;
    private static final int TOP_TRUE = 2;
    private static final int TOP_FALSE = 3;
    private static final int TOP_CENTER_TRUE = 4;
    private static final int TOP_CENTER_FALSE = 5;

    private static final String COLD = "RAISE_COLD";
    private static final String WARM = "RAISE_WARM";
    private static final String HOT = "RAISE_HOT";

    public float timer1 = Constant.MEAL_RAISE_TIME_1;
    public float timer2 = Constant.MEAL_RAISE_TIME_2;
    public float timer3 = Constant.MEAL_RAISE_TIME_3;

    // We separate it in 3 phases to advise the player

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateEntity(meal, deltaTime, meal.getParent().getAutomaticAirMode() % 2 == 0);

        switch (meal.getParent().getAutomaticAirMode()) {
            case CENTER_TRUE:
            case CENTER_FALSE:
                center();
                break;
            case TOP_TRUE:
            case TOP_FALSE:
                top();
                break;
            case TOP_CENTER_TRUE:
            case TOP_CENTER_FALSE:
                topCenter();
                break;
        }

        return checkTimers(deltaTime);
    }

    private void center() {
        if (meal.velocity.y == 0) {
            if (meal.position.x + (meal.dimension.x / 2) < Constant.SCREEN_WIDTH / 2) {
                meal.velocity.x = Constant.MEAL_RAISE_VELOCITY;
            } else {
                meal.velocity.x = -Constant.MEAL_RAISE_VELOCITY;
            }
        } else {
            meal.velocity.x = 0;
        }
        if (meal.position.y <= (Constant.SCREEN_HEIGHT + Constant.SCREEN_GAME_Y_OFFSET) / 2) {
            meal.velocity.y = Constant.MEAL_RAISE_VELOCITY;
        } else if (meal.position.y > ((Constant.SCREEN_HEIGHT + Constant.SCREEN_GAME_Y_OFFSET) / 2) + meal.dimension.y) {
            meal.velocity.y = -Constant.MEAL_RAISE_VELOCITY;
        } else {
            meal.velocity.y = 0;
        }
    }

    private void top() {
        if (meal.position.y > Constant.SCREEN_HEIGHT + Constant.SCREEN_GAME_Y_OFFSET - Constant.TILE_SIZE - meal.dimension.y) {
            meal.velocity.y = -Constant.MEAL_RAISE_VELOCITY;
        } else {
            meal.velocity.y = Constant.MEAL_RAISE_VELOCITY;
        }
    }

    private void topCenter() {
        if (meal.position.y > Constant.SCREEN_HEIGHT + Constant.SCREEN_GAME_Y_OFFSET - 6 * Constant.TILE_SIZE - meal.dimension.y) {
            meal.velocity.y = -Constant.MEAL_RAISE_VELOCITY;
            if (meal.position.x + (meal.dimension.x / 2) < Constant.SCREEN_WIDTH / 2) {
                meal.velocity.x = Constant.MEAL_RAISE_VELOCITY;
            } else {
                meal.velocity.x = -Constant.MEAL_RAISE_VELOCITY;
            }
        } else {
            meal.velocity.y = Constant.MEAL_RAISE_VELOCITY;
        }
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
