package com.aramirezochoa.nyamnyam.screen.game.core.entity.state;

import com.badlogic.gdx.utils.Pool;

/**
 * Created by boheme on 15/01/15.
 */
public abstract class EntityState implements Pool.Poolable {

    private Pool pool;

    public void init() {
        // ...
    }

    public abstract boolean update(float deltaTime);

    public abstract String getCode();

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public void free() {
        if (pool != null) {
            pool.free(this);
        }
    }

    public void restart() {
        reset();
    }

}
