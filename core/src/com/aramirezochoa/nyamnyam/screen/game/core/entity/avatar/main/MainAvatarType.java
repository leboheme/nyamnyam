package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main;

import com.badlogic.gdx.math.Vector2;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;

/**
 * Created by leboheme on 10/03/15.
 */
public enum MainAvatarType implements EntityType {
    NYAM(NyamAvatar.class),
    GLUTY(GlutyAvatar.class),
    TFLY(TflyAvatar.class);

    private final Class<? extends MainAvatar> mainAvatarClass;

    MainAvatarType(Class<? extends MainAvatar> mainAvatarClass) {
        this.mainAvatarClass = mainAvatarClass;
    }

    @Override
    public Vector2 getDimension() {
        return Constant.AVATAR_DIMENSION;
    }

    public Class<? extends MainAvatar> getMainAvatarClass() {
        return mainAvatarClass;
    }
}
