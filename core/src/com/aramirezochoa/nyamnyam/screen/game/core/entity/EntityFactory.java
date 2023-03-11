package com.aramirezochoa.nyamnyam.screen.game.core.entity;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.Avatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.EnemyType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.EmptyEntity;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatarType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.pet.PetGlutyAvatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.pet.PetTflyAvatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.air.Air;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.air.AirSpikes;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.air.AirTornado;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.boost.Boost;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.candy.Candy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.fruit.Fruit;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.verdure.Verdure;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.BombProjectile;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.Projectile;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.meal.common.RaiseType;

/**
 * Created by boheme on 14/01/15.
 */
public class EntityFactory {

    public static <T extends Entity> T entity(Class<T> type) {
        Pool<T> pool = Pools.get(type);
        T entity = pool.obtain();
        entity.setPool(pool);
        return entity;
    }

    public static MainAvatar mainAvatar(MainAvatarType type, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper) {
        MainAvatar mainAvatar = entity(type.getMainAvatarClass());
        mainAvatar.init(type, x, y, lookingAt, gameWrapper);
        return mainAvatar;
    }

    public static EmptyEntity empty(GameWrapper gameWrapper) {
        EmptyEntity empty = entity(EmptyEntity.class);
        empty.init(null, 0, 0, null, gameWrapper);
        return empty;
    }

    public static Enemy enemy(EnemyType enemyType, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper) {
        Enemy enemy = entity(enemyType.getEnemyClass());
        enemy.init(enemyType, x, y, lookingAt, gameWrapper);
        return enemy;
    }

    public static Air air(MealType mealType, RaiseType raiseType, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper) {
        Air air = entity(Air.class);
        air.init(mealType, x, y, lookingAt, gameWrapper, raiseType);
        return air;
    }

    public static AirTornado tornado(MealType mealType, RaiseType raiseType, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper) {
        AirTornado tornado = entity(AirTornado.class);
        tornado.init(mealType, x, y, lookingAt, gameWrapper, raiseType);
        return tornado;
    }

    public static AirSpikes spikes(MealType mealType, RaiseType raiseType, float x, float y, DirectionType lookingAt, GameWrapper gameWrapper) {
        AirSpikes spikes = entity(AirSpikes.class);
        spikes.init(mealType, x, y, lookingAt, gameWrapper, raiseType);
        return spikes;
    }

    public static Candy candy(Enemy enemy) {
        Candy candy = entity(Candy.class);
        candy.init(enemy);
        return candy;
    }

    public static Boost boost(float x, float y, MealType boostType, GameWrapper gameWrapper, boolean expiring) {
        Boost boost = entity(Boost.class);
        boost.init(x, y, boostType, gameWrapper, expiring);
        return boost;
    }

    public static Verdure randomVerdure(float x, float y, GameWrapper gameWrapper) {
        Verdure verdure = entity(Verdure.class);
        verdure.init(x, y, MealType.randomVerdure(), gameWrapper);
        return verdure;
    }

    public static Fruit fruit(float x, float y, MealType mealType, GameWrapper gameWrapper) {
        Fruit fruit = entity(Fruit.class);
        fruit.init(x, y, mealType, gameWrapper);
        return fruit;
    }

    public static Projectile projectile(ProjectileType type, Avatar entity, DirectionType lookingAt, boolean friendly, boolean transformToCandy) {
        Projectile projectile = entity(Projectile.class);
        projectile.init(type, type.getInitialX(entity), type.getInitialY(entity), lookingAt, entity.getParent(), friendly, transformToCandy, entity);
        return projectile;
    }

    public static Projectile projectile(ProjectileType type, Entity reference, Avatar pitcher, boolean friendly, boolean transformToCandy) {
        Projectile projectile = entity(Projectile.class);
        projectile.init(type, type.getInitialX(reference), type.getInitialY(reference), pitcher.getLookingAt().flip(), pitcher.getParent(), friendly, transformToCandy, pitcher);
        return projectile;
    }

    public static Projectile projectileGermination(ProjectileType type, Projectile reference, boolean friendly, boolean transformToCandy, int germination) {
        Projectile projectile = entity(Projectile.class);
        projectile.init(type, type.getInitialX(reference) + (type.getDimension().x * (DirectionType.RIGHT.equals(reference.getLookingAt()) ? 1 : -1)),
                type.getInitialY(reference), reference.getLookingAt(), reference.getParent(), friendly, transformToCandy, germination);
        return projectile;
    }

    public static Projectile bomb(Avatar entity, DirectionType lookingAt) {
        ProjectileType type = ProjectileType.BOMB;
        BombProjectile projectile = entity(BombProjectile.class);
        projectile.init(type, type.getInitialX(entity), type.getInitialY(entity), lookingAt, entity.getParent(), true, false, entity);
        return projectile;
    }

    public static PetTflyAvatar tfly(Avatar avatar) {
        PetTflyAvatar tfly = entity(PetTflyAvatar.class);
        tfly.init(avatar, avatar.getParent());
        return tfly;
    }

    public static PetGlutyAvatar gluty(float x, float y, DirectionType lookingAt, GameWrapper gameWrapper) {
        PetGlutyAvatar gluty = entity(PetGlutyAvatar.class);
        gluty.init(x, y, lookingAt, gameWrapper);
        return gluty;
    }
}
