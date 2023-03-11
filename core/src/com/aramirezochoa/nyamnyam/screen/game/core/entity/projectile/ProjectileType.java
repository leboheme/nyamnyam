package com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile;

import com.badlogic.gdx.math.Vector2;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.Entity;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.projectile.ProjectileStateFactory;

/**
 * Created by boheme on 26/01/15.
 */
public enum ProjectileType implements EntityType {

    HORN {
        @Override
        public Vector2 getDimension() {
            return Constant.HORN_DIMENSION;
        }

        @Override
        protected EntityState getDefaultState(Projectile projectile, boolean transformToCandy) {
            return ProjectileStateFactory.sequence(ProjectileStateFactory.drop(projectile), ProjectileStateFactory.die(projectile));
        }

        @Override
        public float getVelocity() {
            return Constant.HORN_VELOCITY;
        }

        @Override
        public float getInitialX(Entity entity) {
            return entity.position.x + (entity.dimension.x / 2);
        }

        @Override
        public float getInitialY(Entity entity) {
            return entity.position.y - getDimension().y;
        }
    },
    ROCK {
        @Override
        public Vector2 getDimension() {
            return Constant.ROCK_DIMENSION;
        }

        @Override
        protected EntityState getDefaultState(Projectile projectile, boolean transformToCandy) {
            return ProjectileStateFactory.sequence(ProjectileStateFactory.parabolic(projectile), ProjectileStateFactory.die(projectile));
        }

        @Override
        public float getInitialX(Entity entity) {
            return entity.position.x + (entity.dimension.x / 2);
        }

        @Override
        public float getInitialY(Entity entity) {
            return entity.position.y + entity.dimension.y;
        }
    },
    ENERGY {
        @Override
        public Vector2 getDimension() {
            return Constant.ENERGY_DIMENSION;
        }

        @Override
        protected EntityState getDefaultState(Projectile projectile, boolean transformToCandy) {
            return ProjectileStateFactory.sequence(ProjectileStateFactory.horizontal(projectile), ProjectileStateFactory.die(projectile));
        }

        @Override
        public float getVelocity() {
            return Constant.ENERGY_VELOCITY;
        }

        @Override
        public float getInitialX(Entity entity) {
            return entity.position.x + (entity.dimension.x / 2) - (getDimension().x / 2);
        }

        @Override
        public float getInitialY(Entity entity) {
            return entity.position.y + (entity.dimension.y / 2) - (getDimension().y / 2);
        }

        @Override
        public boolean isTileCollision() {
            return true;
        }
    },
    ACID {
        @Override
        public Vector2 getDimension() {
            return Constant.ACID_DIMENSION;
        }

        @Override
        protected EntityState getDefaultState(Projectile projectile, boolean transformToCandy) {
            return ProjectileStateFactory.sequence(ProjectileStateFactory.drop(projectile), ProjectileStateFactory.die(projectile));
        }

        @Override
        public float getVelocity() {
            return Constant.ACID_VELOCITY;
        }

        @Override
        public float getInitialX(Entity entity) {
            return entity.position.x + (entity.dimension.x / 2) - getDimension().x / 2;
        }

        @Override
        public float getInitialY(Entity entity) {
            return entity.position.y - getDimension().y;
        }
    },
    TORNADO {
        @Override
        public Vector2 getDimension() {
            return Constant.TORNADO_DIMENSION;
        }

        @Override
        protected EntityState getDefaultState(Projectile projectile, boolean transformToCandy) {
            return ProjectileStateFactory.sequence(ProjectileStateFactory.horizontal(projectile), ProjectileStateFactory.explode(projectile, false));
        }

        @Override
        public float getVelocity() {
            return Constant.TORNADO_VELOCITY;
        }

        @Override
        public float getInitialX(Entity entity) {
            return entity.position.x + (entity.dimension.x / 2) - (getDimension().x / 2);
        }

        @Override
        public float getInitialY(Entity entity) {
            return entity.position.y + (entity.dimension.y / 2) - (getDimension().y / 2);
        }
    },
    SPIKE_BALL {
        @Override
        public Vector2 getDimension() {
            return Constant.SPIKE_BALL_DIMENSION;
        }

        @Override
        protected EntityState getDefaultState(Projectile projectile, boolean transformToCandy) {
            return ProjectileStateFactory.sequence(ProjectileStateFactory.drop(projectile), ProjectileStateFactory.scatter(projectile), ProjectileStateFactory.explode(projectile, false));
        }

        @Override
        public float getVelocity() {
            return Constant.SPIKE_BALL_VELOCITY;
        }

        @Override
        public float getInitialX(Entity entity) {
            return entity.position.x + (entity.dimension.x / 2) - getDimension().x / 2;
        }

        @Override
        public float getInitialY(Entity entity) {
            return entity.position.y;
        }

        @Override
        public boolean isTileCollision() {
            return true;
        }
    },
    SPIKE {
        @Override
        public Vector2 getDimension() {
            return Constant.SPIKE_DIMENSION;
        }

        @Override
        protected EntityState getDefaultState(Projectile projectile, boolean transform) {
            return ProjectileStateFactory.sequence(ProjectileStateFactory.scatter(projectile), ProjectileStateFactory.explode(projectile, false));
        }

        @Override
        public float getInitialX(Entity entity) {
            return entity.position.x + (entity.dimension.x / 2) - getDimension().x / 2;
        }

        @Override
        public float getInitialY(Entity entity) {
            return entity.position.y;
        }

        @Override
        public boolean isTileCollision() {
            return true;
        }
    },
    BEAM {
        @Override
        public Vector2 getDimension() {
            return Constant.BEAM_DIMENSION;
        }

        @Override
        public EntityState getDefaultState(Projectile projectile, boolean transformToCandy) {
            return ProjectileStateFactory.sequence(ProjectileStateFactory.hurled(projectile, transformToCandy), ProjectileStateFactory.die(projectile));
        }

        @Override
        public float getInitialX(Entity entity) {
            return entity.position.x;
        }

        @Override
        public float getInitialY(Entity entity) {
            return entity.position.y;
        }

        @Override
        public float getDampingFactor() {
            return 1;
        }
    },
    FIRE_BALL {
        @Override
        public Vector2 getDimension() {
            return Constant.BEAM_DIMENSION;
        }

        @Override
        public EntityState getDefaultState(Projectile projectile, boolean transformToCandy) {
            return ProjectileStateFactory.sequence(ProjectileStateFactory.hurled(projectile, transformToCandy), ProjectileStateFactory.die(projectile));
        }

        @Override
        public float getInitialX(Entity entity) {
            return entity.position.x;
        }

        @Override
        public float getInitialY(Entity entity) {
            return entity.position.y;
        }

        @Override
        public float getDampingFactor() {
            return 1;
        }
    },
    // Reserved to bosses
    BIG_FIRE {
        @Override
        public Vector2 getDimension() {
            return Constant.BEAM_DIMENSION;
        }

        @Override
        protected EntityState getDefaultState(Projectile projectile, boolean transformToCandy) {
            return ProjectileStateFactory.sequence(ProjectileStateFactory.boss(projectile), ProjectileStateFactory.explode(projectile, false));
        }

        @Override
        public float getVelocity() {
            return Constant.BIG_FIRE_VELOCITY;
        }

        @Override
        public float getInitialX(Entity entity) {
            return DirectionType.RIGHT.equals(entity.getLookingAt()) ? entity.position.x : entity.position.x + entity.dimension.x - getDimension().x;
        }

        @Override
        public float getInitialY(Entity entity) {
            return entity.position.y;
        }

        @Override
        public float getDampingFactor() {
            return 1;
        }
    }, BOMB {
        @Override
        public Vector2 getDimension() {
            return Constant.BEAM_DIMENSION;
        }

        @Override
        public EntityState getDefaultState(Projectile projectile, boolean transformToCandy) {
            return ProjectileStateFactory.idle(projectile);
        }

        @Override
        public float getInitialX(Entity entity) {
            return entity.position.x;
        }

        @Override
        public float getInitialY(Entity entity) {
            return entity.position.y;
        }

        @Override
        public float getDampingFactor() {
            return 0.9f;
        }
    };

    @Override
    public abstract Vector2 getDimension();

    protected abstract EntityState getDefaultState(Projectile projectile, boolean transform);

    public float getVelocity() {
        return 0;
    }

    public abstract float getInitialX(Entity entity);

    public abstract float getInitialY(Entity entity);

    public float getDampingFactor() {
        return Constant.PROJECTILE_DAMPING;
    }

    public boolean isTileCollision() {
        return false;
    }
}
