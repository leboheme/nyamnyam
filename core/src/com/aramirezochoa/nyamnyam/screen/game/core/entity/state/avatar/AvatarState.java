package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;

/**
 * Created by boheme on 15/01/15.
 */
public abstract class AvatarState extends EntityState {

    protected Avatar avatar;

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

}
