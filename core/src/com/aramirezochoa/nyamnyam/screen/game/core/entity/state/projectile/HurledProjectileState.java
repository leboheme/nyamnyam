package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.projectile;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.run.RunRuntime;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.Projectile;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.GameWrapperAssistant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.BEAM_TO_BUBBLE_DELAY_SECONDS;

/**
 * Created by boheme on 15/01/15.
 */
public class HurledProjectileState extends ProjectileState {

    public static final String CODE = "HURLED";
    private float initX, distance;
    private boolean transformToCandy;
    private boolean startPending;
    private boolean transformPending, blockedAtEnd;
    private float transformDelay;

    @Override
    public void init() {
        super.init();
        getProjectile().acceleration.y = -Constant.GRAVITY.y;
        getProjectile().velocity.x = DirectionType.RIGHT.equals(getProjectile().getLookingAt()) ? getPitcher().getFireVelocity() : -getPitcher().getFireVelocity();
        distance = getPitcher().getFireDistance();
        startPending = true;
        transformPending = false;
    }

    private Avatar getPitcher() {
        return getProjectile().getPitcher();
    }

    @Override
    public boolean update(float deltaTime) {
        Projectile projectile = getProjectile();
        if (transformPending) {
            transformDelay -= deltaTime;
            if (transformDelay > 0) return false;
            projectile.transform(blockedAtEnd);
            return true;
        }
        // Projectile.init can widen a JSON shot after the legacy state is initialized.
        if (startPending) {
            initX = projectile.position.x;
            startPending = false;
        }
        if (projectile.getParent() instanceof RunRuntime) {
            RunRuntime runtime = (RunRuntime) projectile.getParent();
            float movementX = projectile.velocity.x * deltaTime;
            float collisionX = projectile.position.x;
            float collisionWidth = projectile.dimension.x;
            if (projectile.getType() == ProjectileType.BEAM) {
                // The beam starts over the shooter. Only its advancing edge encounters new terrain;
                // tiles already behind that edge must not stop a shot traveling away from them.
                collisionWidth = 1;
                if (projectile.getLookingAt() == DirectionType.RIGHT) {
                    collisionX += projectile.dimension.x - collisionWidth;
                }
            }
            if (!runtime.getBubbleTerrain().canTravel(collisionX, projectile.position.y,
                    collisionWidth, projectile.dimension.y, movementX, 0)) {
                if (transformToCandy) {
                    delayTransform(projectile, true);
                    return false;
                }
                projectile.setEnabled(false);
                return true;
            }
            projectile.setPosition(projectile.position.x + movementX, projectile.position.y);
            projectile.dampVelocity();
        } else {
            GameWrapperAssistant.updateEntity(projectile, deltaTime, false);
        }
        if ((getProjectile().velocity.x == 0 || Math.abs(getProjectile().position.x - initX) >= distance)) {
            if (transformToCandy) {
                if (projectile.getParent() instanceof RunRuntime) {
                    delayTransform(projectile, false);
                    return false;
                }
                projectile.transform();
            }
            return true;
        }
        return false;
    }

    private void delayTransform(Projectile projectile, boolean blocked) {
        projectile.setEnabled(false);
        projectile.velocity.setZero();
        transformPending = true;
        blockedAtEnd = blocked;
        transformDelay = BEAM_TO_BUBBLE_DELAY_SECONDS;
    }

    @Override
    public String getCode() {
        return CODE;
    }

    @Override
    public void reset() {
        getProjectile().velocity.x = 0;
        getProjectile().acceleration.y = 0;
        startPending = false;
        transformPending = false;
        transformDelay = 0;
    }

    public void setInitX(float initX) {
        this.initX = initX;
    }

    public void setTransformToCandy(boolean transformToCandy) {
        this.transformToCandy = transformToCandy;
    }
}
