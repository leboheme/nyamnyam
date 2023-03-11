package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;

/**
 * Created by boheme on 30/01/15.
 */
public class ClimbEnemyState extends EnemyState {

    private static final String WALK = "WALK";
    private static final String FIRE = "FIRE";

    private float fireTimer = Constant.ENEMY_FIRE_RATE;
    private float statusFireTimer = 2f;
    private boolean firing;

    public void init() {
        super.init();
        getEnemy().acceleration.y = -2 * Constant.GRAVITY.y;
        if (DirectionType.RIGHT.equals(avatar.getLookingAt())) {
            avatar.velocity.x = getEnemy().getVelocity();
        } else {
            avatar.velocity.x = -getEnemy().getVelocity();
        }
    }

    @Override
    public boolean update(float deltaTime) {
        GameWrapperAssistant.updateEntity(getEnemy(), deltaTime, true);

        if (getEnemy().getParent().mainAvatar.position.x + (getEnemy().getParent().mainAvatar.dimension.x / 2) < getEnemy().position.x) {
            avatar.velocity.x = -getEnemy().getVelocity();
        } else if (getEnemy().getParent().mainAvatar.position.x + (getEnemy().getParent().mainAvatar.dimension.x / 2) > getEnemy().position.x + getEnemy().dimension.x) {
            avatar.velocity.x = getEnemy().getVelocity();
        } else {
            if (!firing && fireTimer < 0) {
                firing = true;
                statusFireTimer = 0.25f;
            }
        }

        if (!firing) {
            fireTimer -= deltaTime;
        } else if (statusFireTimer < 0) {
            fireTimer = Constant.ENEMY_FIRE_RATE;
            firing = false;
            if (getEnemy().canFire()) {
                avatar.getParent().addProjectile(EntityFactory.projectile(ProjectileType.ACID, avatar, DirectionType.RIGHT, false, false));
            }
        } else {
            statusFireTimer -= deltaTime;
        }

        return false;
    }

    @Override
    public void reset() {
        getEnemy().acceleration.y = 0;
    }

    @Override
    public String getCode() {
        if (firing) {
            return FIRE;
        } else {
            return WALK;
        }
    }
}
