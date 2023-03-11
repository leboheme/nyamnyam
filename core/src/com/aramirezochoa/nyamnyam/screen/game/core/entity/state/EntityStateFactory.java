package com.aramirezochoa.nyamnyam.screen.game.core.entity.state;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.Entity;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.common.*;

/**
 * Created by boheme on 15/01/15.
 */
public abstract class EntityStateFactory {

    public static <T extends EntityState> T state(Class<T> type) {
        Pool<T> pool = Pools.get(type);
        T entity = pool.obtain();
        entity.setPool(pool);
        return entity;
    }

    public static EntityState idle(Entity entity) {
        IdleEntityState state = state(IdleEntityState.class);
        state.setEntity(entity);
        return state;
    }

    public static EntityState idle(Entity entity, float time) {
        IdleEntityState state = state(IdleEntityState.class);
        state.setEntity(entity);
        state.setTime(time);
        return state;
    }

    public static EntityState explode(Entity entity, boolean bigExplosion) {
        ExplodeEntityState state = state(ExplodeEntityState.class);
        state.setEntity(entity);
        state.setBigExplosion(bigExplosion);
        return state;
    }

    public static EntityState explode(Entity entity, boolean bigExplosion, float explosionTimer) {
        ExplodeEntityState state = state(ExplodeEntityState.class);
        state.setEntity(entity);
        state.setBigExplosion(bigExplosion);
        state.setTimer(explosionTimer);
        return state;
    }

    public static SequenceEntityState sequence(EntityState state1, EntityState state2) {
        SequenceEntityState sequence = state(SequenceEntityState.class);
        sequence.addState(state1);
        sequence.addState(state2);
        return sequence;
    }

    public static SequenceEntityState sequence(EntityState state1, EntityState state2, EntityState state3) {
        SequenceEntityState sequence = state(SequenceEntityState.class);
        sequence.addState(state1);
        sequence.addState(state2);
        sequence.addState(state3);
        return sequence;
    }

    public static SequenceEntityState sequence(EntityState state1, EntityState state2, EntityState state3, EntityState state4) {
        SequenceEntityState sequence = state(SequenceEntityState.class);
        sequence.addState(state1);
        sequence.addState(state2);
        sequence.addState(state3);
        sequence.addState(state4);
        return sequence;
    }

    public static SequenceEntityState sequence(EntityState state1, EntityState state2, EntityState state3, EntityState state4, EntityState state5) {
        SequenceEntityState sequence = state(SequenceEntityState.class);
        sequence.addState(state1);
        sequence.addState(state2);
        sequence.addState(state3);
        sequence.addState(state4);
        sequence.addState(state5);
        return sequence;
    }

    public static LoopEntityState loop(EntityState state) {
        LoopEntityState loop = state(LoopEntityState.class);
        loop.setState(state);
        return loop;
    }

    public static EntityState die(Entity entity) {
        DieEntityState die = state(DieEntityState.class);
        die.setTarget(entity);
        return die;
    }
}
