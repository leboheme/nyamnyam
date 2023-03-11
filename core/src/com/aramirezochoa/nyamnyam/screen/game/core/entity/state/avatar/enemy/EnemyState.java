package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.AvatarState;

/**
 * Created by boheme on 26/01/15.
 */
public abstract class EnemyState extends AvatarState {

    private static final String JUMP = "JUMP";
    private static final String FALL = "FALL";
    private static final String WALK = "WALK";
    private static final String STAND = "STAND";

    protected Enemy getEnemy() {
        return (Enemy) avatar;
    }

    @Override
    public String getCode() {
        if (getEnemy().velocity.y > 0) {
            return JUMP;
        } else if (getEnemy().velocity.y < 0) {
            return FALL;
        } else if (getEnemy().velocity.x != 0) {
            return WALK;
        } else {
            return STAND;
        }
    }

}
