package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.common;

import com.badlogic.gdx.utils.Array;
import com.aramirezochoa.nyamnyam.Constant;

/**
 * Created by boheme on 3/02/15.
 */
public class RaiseLeftGuidedMealState extends RaiseGuidedMealState {

    @Override
    protected Array<Constant.Coordinates> getGuide() {
        return meal.getParent().leftGuide;
    }

}
