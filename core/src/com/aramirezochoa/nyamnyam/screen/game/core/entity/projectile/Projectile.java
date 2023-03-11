package com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile;

import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.Entity;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.common.RaiseType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.projectile.ProjectileStateFactory;

/**
 * Created by boheme on 26/01/15.
 */
public class Projectile extends Entity {


    private Avatar pitcher;
    private boolean friendly, transformToCandy;
    private int germination;

    public void init(EntityType type, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper, boolean friendly, boolean transformToCandy, Avatar pitcher) {
        this.friendly = friendly;
        this.transformToCandy = transformToCandy;
        this.pitcher = pitcher;
        this.germination = -1;
        super.init(type, x, y, lookingAt, gameWrapper);
    }

    public void init(EntityType type, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper, boolean friendly, boolean transformToCandy, int germination) {
        this.friendly = friendly;
        this.transformToCandy = transformToCandy;
        super.init(type, x, y, lookingAt, gameWrapper);
        this.germination = germination;
    }

    @Override
    protected EntityState getInitialState() {
        return ((ProjectileType) type).getDefaultState(this, transformToCandy);
    }

    @Override
    protected void checkCollisions() {
        if (enabled) {
            if (friendly) {
                for (Enemy enemy : getParent().enemies) {
                    if (enemy.box.overlaps(box)) {
                        enemy.hit(transformToCandy);
                        if (!ProjectileType.SPIKE.equals(getType())) {
                            setCurrentState(ProjectileStateFactory.explode(this, false));
                        }

                    }
                }
            } else {
                if (box.overlaps(getParent().mainAvatar.box)) {
                    getParent().mainAvatar.hit();
                }
            }
        }
    }

    @Override
    public ProjectileType getType() {
        return (ProjectileType) type;
    }

    @Override
    public void remove() {
        super.remove();
        parent.projectiles.removeValue(this, true);
    }

    @Override
    protected boolean isLoopAnimation() {
        if (ProjectileType.SPIKE.equals(getType())) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void dampVelocity() {
        velocity.x *= getType().getDampingFactor();
    }

    @Override
    protected String getStatusCode() {
        return type.name();
    }

    public float getVelocity() {
        return ((ProjectileType) type).getVelocity();
    }

    public Avatar getPitcher() {
        return pitcher;
    }

    public int getGermination() {
        return germination;
    }

    @Override
    public void reset() {
        super.reset();
        pitcher = null;
        germination = -1;
    }

    public void transform() {
        if (getParent().mainAvatar.isTornadoBoost()) {
            getParent().addMeal(EntityFactory.tornado(MealType.AIR_TORNADO, RaiseType.AUTOMATIC, position.x, position.y, getLookingAt(), getParent()));
        } else {
            getParent().addMeal(EntityFactory.air(MealType.AIR, RaiseType.AUTOMATIC, position.x, position.y, getLookingAt(), getParent()));
        }
    }
}
