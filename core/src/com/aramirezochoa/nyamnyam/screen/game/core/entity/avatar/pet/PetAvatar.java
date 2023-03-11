package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.pet;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;

/**
 * Created by leboheme on 2/02/15.
 */
public abstract class PetAvatar extends Avatar {

    @Override
    protected boolean isLoopAnimation() {
        return true;
    }

    @Override
    public void remove() {
        super.remove();
        parent.pets.removeValue(this, true);
    }

}
