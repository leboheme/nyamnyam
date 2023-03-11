package com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.projectile.ProjectileStateFactory;

/**
 * Created by leboheme on 12/03/15.
 */
public class BombProjectile extends Projectile {

    private float explosionTimer;
    private float bombTimer;
    private boolean exploded;
    private boolean bigExplosion;

    @Override
    public void init(EntityType type, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper, boolean friendly, boolean transformToCandy, Avatar pitcher) {
        super.init(type, x, y, lookingAt, gameWrapper, friendly, transformToCandy, pitcher);
        bombTimer = Constant.TFLY_BOMB_TIMER; //default
        bigExplosion = false;
        exploded = false;
    }

    @Override
    protected void updateState(float deltaTime) {
        super.updateState(deltaTime);
        if (bombTimer < 0 && !exploded) {
            setCurrentState(ProjectileStateFactory.explode(this, false, explosionTimer));
            exploded = true;
            getParent().getMediaManager().playSound(MediaManager.SOUND_EXPLOSION);
            if (!bigExplosion) {
                position.x -= ((Constant.TFLY_BOMB_EXPLOSION_WIDTH - dimension.x) / 2);
                dimension.x = Constant.TFLY_BOMB_EXPLOSION_WIDTH;
            } else {
                position.x -= ((Constant.TFLY_BOMB_BIG_EXPLOSION_WIDTH - dimension.x) / 2);
                dimension.x = Constant.TFLY_BOMB_BIG_EXPLOSION_WIDTH;
            }
            initBoxes();
        }
        bombTimer -= deltaTime;
    }

    @Override
    protected boolean isLoopAnimation() {
        return true;
    }

    @Override
    protected void checkCollisions() {
        if (exploded) {
            for (Enemy enemy : getParent().enemies) {
                if (enemy.box.overlaps(box)
                        && enemy.isEnabled()) {
                    enemy.hit(false);
                    if (enemy.isBoss()) {
                        setCurrentState(ProjectileStateFactory.explode(this, false));
                    }

                }
            }
        }
    }

    @Override
    protected String getFullCode() {
        if (exploded) {
            if (bigExplosion) return "BOMB_BIG_EXPLODE";
            else return "BOMB_EXPLODE";
        } else {
            return "BOMB";
        }
    }

    @Override
    public void dampVelocity() {
        if (velocity.y == 0) {
            velocity.x *= getType().getDampingFactor();
        }
    }

    public void setExplosionTimer(float explosionTimer) {
        this.explosionTimer = explosionTimer;
    }

    public void setBigExplosion(boolean bigExplosion) {
        this.bigExplosion = bigExplosion;
    }
}
