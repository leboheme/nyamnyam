package com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.air;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.MealStateFactory;

/**
 * Created by boheme on 3/02/15.
 */
public class AirTornado extends AbstractAir {

    @Override
    protected EntityState getInitialState() {
        return MealStateFactory.sequence(MealStateFactory.raise(this, raiseType), MealStateFactory.die(this));
    }

    @Override
    protected int getEatenScore() {
        return Constant.SCORE_AIR_TORNADO;
    }

    @Override
    protected void staffBeforeEaten(Avatar avatar) {
        super.staffBeforeEaten(avatar);
        getParent().addProjectile(EntityFactory.projectile(ProjectileType.TORNADO, this, avatar, true, false));
    }

}
