package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.Projectile;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;

/**
 * Created by boheme on 5/02/15.
 */
public class BossState extends EnemyState {

    private static final String FLY = "FLY";

    private boolean bouncingToTop;
    private int bossMode;
    private boolean shotChecked = true;

    public void setBossMode(int bossMode) {
        this.bossMode = bossMode;
    }

    @Override
    public void setAvatar(Avatar avatar) {
        super.setAvatar(avatar);
    }

    @Override
    public boolean update(float deltaTime) {
        avatar.acceleration.y = -Constant.GRAVITY.y;
        GameWrapperAssistant.updateBoss(avatar, deltaTime);

        if (avatar.velocity.x == 0) {
            if (DirectionType.RIGHT.equals(avatar.getLookingAt())) {
                avatar.velocity.x = avatar.getVelocity();
            } else {
                avatar.velocity.x = -avatar.getVelocity();
            }
            shotChecked = false;
        }
        if (avatar.velocity.y == 0) {
            if (bouncingToTop) {
                avatar.velocity.y = -avatar.getVelocity();
            } else {
                avatar.velocity.y = avatar.getVelocity();
            }
            bouncingToTop = !bouncingToTop;
        }

        if (!shotChecked) {
            checkShoot();
        }

        return false;
    }

    private void checkShoot() {
        switch (bossMode) {
            case Constant.BOSS_MODE_1:
                addProjectileWithVelocity(0.98f, 0.17f);
                addProjectileWithVelocity(0.98f, -0.17f);
                addProjectileWithVelocity(0.86f, 0.5f);
                addProjectileWithVelocity(0.86f, -0.5f);
                addProjectileWithVelocity(0.64f, -0.77f);
                addProjectileWithVelocity(0.64f, -0.77f);
                break;
            case Constant.BOSS_MODE_2:
                addProjectileWithVelocity(0f, 1f);
                addProjectileWithVelocity(0.5f, 0.87f);
                addProjectileWithVelocity(0.87f, 0.5f);
                addProjectileWithVelocity(1, 0f);
                addProjectileWithVelocity(0.87f, -0.5f);
                addProjectileWithVelocity(0.5f, -0.87f);
                addProjectileWithVelocity(0f, -1f);
                break;
        }
        shotChecked = true;
    }

    private void addProjectileWithVelocity(float x, float y) {
        Projectile projectile = EntityFactory.projectile(ProjectileType.BIG_FIRE, avatar, avatar.getLookingAt(), false, false);
        projectile.velocity.x = DirectionType.RIGHT.equals(projectile.getLookingAt()) ? projectile.getVelocity() * x : -projectile.getVelocity() * x;
        projectile.velocity.y = projectile.getVelocity() * y;
        avatar.getParent().addProjectile(projectile);
    }

    @Override
    public String getCode() {
        return FLY;
    }

    @Override
    public void reset() {
        bouncingToTop = false;
        avatar.setLookingAt(avatar.getLookingAt().flip());
        avatar.velocity.x = 0;
        avatar.velocity.y = 0;
        shotChecked = true;
    }

}
