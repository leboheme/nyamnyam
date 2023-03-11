package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.pet;

import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.AvatarStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.MealStateFactory;

/**
 * Created by boheme on 2/02/15.
 */
public class PetTflyAvatar extends PetAvatar {

    private Avatar center;

    public void init(Avatar center, GameWrapper gameWrapper) {
        this.center = center;
        super.init(PetType.TFLY, center.position.x, center.position.y, DirectionType.RIGHT, gameWrapper);
    }

    @Override
    protected void checkCollisions() {
        for (int i = 0; i < getParent().enemies.size; i++) {
            if (box.overlaps(getParent().enemies.get(i).box)) {
                getParent().enemies.get(i).hit(false);
            }
        }
    }

    @Override
    protected EntityState getInitialState() {
        return MealStateFactory.loop(AvatarStateFactory.round(this, center));
    }


}
