package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.main;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.Entity;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.AvatarStateFactory;

/**
 * Created by boheme on 15/01/15.
 */
public class MainAvatarStateFactory extends AvatarStateFactory {

    public static EntityState eat(MainAvatar avatar, Entity entity) {
        EatEntityState state = state(EatEntityState.class);
        state.setAvatar(avatar);
        state.setEntity(entity);
        return state;
    }

    public static EntityState dead(MainAvatar avatar) {
        DeadEntityState state = state(DeadEntityState.class);
        state.setAvatar(avatar);
        return state;
    }

}
