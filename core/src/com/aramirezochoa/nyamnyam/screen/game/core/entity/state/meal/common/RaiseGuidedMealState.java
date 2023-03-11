package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.common;

import com.badlogic.gdx.utils.Array;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.MealState;

/**
 * Created by boheme on 3/02/15.
 */
public abstract class RaiseGuidedMealState extends MealState {

    private static final String COLD = "RAISE_COLD";

    private int index = 0;
    private float centerX, centerY;

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateEntityNoCollisions(meal, deltaTime);

        centerX = meal.position.x + meal.dimension.x / 2;
        centerY = meal.position.y + meal.dimension.y / 2;

        if (centerX >= getGuide().get(index).x - Constant.AIR_GUIDE_HALF_WIDTH
                && centerX <= getGuide().get(index).x + Constant.AIR_GUIDE_HALF_WIDTH
                && centerY >= getGuide().get(index).y - Constant.AIR_GUIDE_HALF_WIDTH
                && centerY <= getGuide().get(index).y + Constant.AIR_GUIDE_HALF_WIDTH) {
            index++;
        } else {
            updateVelocity();
        }
        return index >= getGuide().size;
    }

    protected abstract Array<Constant.Coordinates> getGuide();

    private void updateVelocity() {
        if (centerX < getGuide().get(index).x - Constant.AIR_GUIDE_HALF_WIDTH) {
            meal.velocity.x = Constant.MEAL_RAISE_VELOCITY;
        } else if (centerX > getGuide().get(index).x + Constant.AIR_GUIDE_HALF_WIDTH) {
            meal.velocity.x = -Constant.MEAL_RAISE_VELOCITY;
        } else {
            meal.velocity.x = 0;
        }

        if (centerY < getGuide().get(index).y - Constant.AIR_GUIDE_HALF_WIDTH) {
            meal.velocity.y = Constant.MEAL_RAISE_VELOCITY;
        } else if (centerY > getGuide().get(index).y + Constant.AIR_GUIDE_HALF_WIDTH) {
            meal.velocity.y = -Constant.MEAL_RAISE_VELOCITY;
        } else {
            meal.velocity.y = 0;
        }
    }

    @Override
    public String getCode() {
        return COLD;
    }

    @Override
    public void reset() {
        index = 0;
    }
}
