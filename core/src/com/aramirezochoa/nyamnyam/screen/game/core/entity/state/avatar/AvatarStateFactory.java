package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityStateFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.main.SpawnEntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.pet.RoundAvatarState;

/**
 * Created by boheme on 15/01/15.
 */
public class AvatarStateFactory extends EntityStateFactory {

    public static EntityState fire(Avatar avatar) {
        return fire(avatar, FireAvatarState.DEFAULT_TIME);
    }

    public static EntityState fire(Avatar avatar, float time) {
        FireAvatarState state = state(FireAvatarState.class);
        state.setAvatar(avatar);
        state.setTime(time);
        return state;
    }

    public static EntityState round(Avatar avatar, Avatar center) {
        RoundAvatarState state = state(RoundAvatarState.class);
        state.setAvatar(avatar);
        state.setCenter(center);
        return state;
    }

    public static EntityState bounce(Avatar avatar, float time) {
        BounceAvatarState state = state(BounceAvatarState.class);
        state.setAvatar(avatar);
        state.setTime(time);
        return state;
    }

    public static EntityState think(Avatar avatar, float time) {
        ThinkAvatarState state = state(ThinkAvatarState.class);
        state.setAvatar(avatar);
        state.setTimer(time);
        return state;
    }

    public static EntityState spawn(Avatar avatar) {
        SpawnEntityState state = state(SpawnEntityState.class);
        state.setAvatar(avatar);
        return state;
    }

}
