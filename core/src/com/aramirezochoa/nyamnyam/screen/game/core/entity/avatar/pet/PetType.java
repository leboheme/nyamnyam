package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.pet;

import com.badlogic.gdx.math.Vector2;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;

/**
 * Created by leboheme on 2/02/15.
 */
public enum PetType implements EntityType {

    TFLY {
        @Override
        public Vector2 getDimension() {
            return Constant.AVATAR_DIMENSION;
        }
    },
    GLUTY {
        @Override
        public Vector2 getDimension() {
            return Constant.AVATAR_DIMENSION;
        }
    };

    public abstract Vector2 getDimension();

}
