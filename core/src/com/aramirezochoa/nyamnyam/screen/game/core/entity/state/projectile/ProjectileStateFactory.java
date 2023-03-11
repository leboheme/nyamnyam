package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.projectile;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.Projectile;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityStateFactory;

/**
 * Created by boheme on 26/01/15.
 */
public class ProjectileStateFactory extends EntityStateFactory {

    public static EntityState drop(Projectile projectile) {
        DropProjectileState state = state(DropProjectileState.class);
        state.setProjectile(projectile);
        return state;
    }

    public static EntityState parabolic(Projectile projectile) {
        ParabolicProjectileState state = state(ParabolicProjectileState.class);
        state.setProjectile(projectile);
        return state;
    }

    public static EntityState hurled(Projectile projectile, boolean transformToCandy) {
        HurledProjectileState state = state(HurledProjectileState.class);
        state.setProjectile(projectile);
        state.setInitX(projectile.position.x);
        state.setTransformToCandy(transformToCandy);
        return state;
    }

    public static EntityState horizontal(Projectile projectile) {
        HorizontalProjectileState state = state(HorizontalProjectileState.class);
        state.setProjectile(projectile);
        return state;
    }

    public static EntityState scatter(Projectile projectile) {
        ScatterProjectileState state = state(ScatterProjectileState.class);
        state.setProjectile(projectile);
        return state;
    }

    public static EntityState boss(Projectile projectile) {
        BossProjectileState state = state(BossProjectileState.class);
        state.setProjectile(projectile);
        return state;
    }
}
