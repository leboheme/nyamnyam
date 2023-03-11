package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.common;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.Entity;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;

/**
 * Created by boheme on 15/01/15.
 */
public class ExplodeEntityState extends EntityState {

    protected float timer = Constant.EXPLODE_TIME;
    public static final String CODE = "EXPLODE";
    public static final String BIG_EXPLOSION = "BIG_EXPLODE";

    private Entity entity;
    private boolean bigExplosion;

    @Override
    public boolean update(float deltaTime) {
        entity.enabled = false;
        if (timer < 0) {
            entity.die();
            return true;
        }
        timer -= deltaTime;
        return false;
    }

    @Override
    public String getCode() {
        if (bigExplosion) {
            return BIG_EXPLOSION;
        } else {
            return CODE;
        }
    }

    @Override
    public void reset() {
        timer = Constant.EXPLODE_TIME;
        bigExplosion = false;
    }

    public void setTimer(float timer) {
        this.timer = timer;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setBigExplosion(boolean bigExplosion) {
        this.bigExplosion = bigExplosion;
    }
}
