package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.projectile;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;

/**
 * Created by boheme on 3/02/15.
 */
public class ScatterProjectileState extends ProjectileState {

    private boolean created;
    private float creationTimer = Constant.SPIKE_GERMINATION_RATE;
    private float timer = Constant.SPIKE_LIFE_DURATION;

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateProjectile(getProjectile(), deltaTime, getProjectile().getType().isTileCollision());

        if (getProjectile().getGermination() < Constant.SPIKE_MAX_GERMINATIONS && !created && (creationTimer < 0 || getProjectile().getGermination() == -1)) {
            getProjectile().getParent().addProjectile(EntityFactory.projectileGermination(ProjectileType.SPIKE, getProjectile(), true, false, getProjectile().getGermination() + 1));
            created = true;
            return false;
        }
        if ((getProjectile().getGermination() == -1 && created) || timer < 0) {
            return true;
        }
        creationTimer -= deltaTime;
        timer -= deltaTime;
        return false;
    }

    @Override
    public String getCode() {
        return getProjectile().getType().name();
    }

    @Override
    public void reset() {
        created = false;
        creationTimer = Constant.SPIKE_GERMINATION_RATE;
        timer = Constant.SPIKE_LIFE_DURATION;
    }
}
