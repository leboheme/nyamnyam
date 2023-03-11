package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types;

import com.badlogic.gdx.math.MathUtils;

/**
 * Created by boheme on 13/01/15.
 */
public enum DirectionType {
    RIGHT {
        @Override
        public DirectionType flip() {
            return LEFT;
        }
    },
    LEFT {
        @Override
        public DirectionType flip() {
            return RIGHT;
        }
    };

    public abstract DirectionType flip();

    public static DirectionType random() {
        return (MathUtils.random(100) < 50) ? DirectionType.RIGHT : DirectionType.LEFT;
    }
}
