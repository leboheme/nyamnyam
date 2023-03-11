package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;

/**
 * Created by boheme on 26/01/15.
 */
public class InvadeEnemyState extends EnemyState {

    private static final String FALL = "FALL";
    private static final String WALK = "WALK";
    private static final String FIRE = "FIRE";

    private float fireTimer = Constant.ENEMY_FIRE_RATE;
    private float statusFireTimer = 0.25f;
    private boolean firing;
    private ProjectileType projectileType;

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateEntity(getEnemy(), deltaTime, true);
        if (DirectionType.RIGHT.equals(avatar.getLookingAt())) {
            avatar.velocity.x = getEnemy().getVelocity();
        } else {
            avatar.velocity.x = -getEnemy().getVelocity();
        }
        if (!firing) {
            if (fireTimer < 0) {
                firing = true;
                statusFireTimer = 0.25f;
            }
            fireTimer -= deltaTime;
        } else if (statusFireTimer < 0) {
            fireTimer = Constant.ENEMY_FIRE_RATE;
            firing = false;
            if (getEnemy().canFire()) {
                addProjectile();
            }
        } else {
            statusFireTimer -= deltaTime;
        }
        return false;
    }

    private void addProjectile() {
        switch (projectileType) {
            case HORN:
                avatar.getParent().addProjectile(EntityFactory.projectile(projectileType, avatar, DirectionType.RIGHT, false, false));
                break;
            case ROCK:
                avatar.getParent().addProjectile(EntityFactory.projectile(projectileType, avatar, DirectionType.RIGHT, false, false));
                avatar.getParent().addProjectile(EntityFactory.projectile(projectileType, avatar, DirectionType.LEFT, false, false));
                break;
        }
    }

    @Override
    public String getCode() {
        if (firing) {
            return FIRE;
        } else {
            if (avatar.velocity.y != 0) {
                return FALL;
            } else {
                return WALK;
            }
        }
    }

    @Override
    public void reset() {
        fireTimer = Constant.ENEMY_FIRE_RATE;
        statusFireTimer = 0.25f;
        projectileType = null;
    }

    public void setProjectileType(ProjectileType projectileType) {
        this.projectileType = projectileType;
    }
}
