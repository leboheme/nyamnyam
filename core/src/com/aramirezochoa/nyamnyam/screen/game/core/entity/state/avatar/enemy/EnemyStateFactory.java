package com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.enemy;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.boss.Boss;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.avatar.AvatarStateFactory;

/**
 * Created by boheme on 15/01/15.
 */
public class EnemyStateFactory extends AvatarStateFactory {

    public static EntityState walk(Enemy enemy, float time) {
        WalkEnemyState state = state(WalkEnemyState.class);
        state.setAvatar(enemy);
        state.setTimerValue(time);
        return state;
    }

    public static EntityState jump(Enemy enemy, boolean move) {
        JumpEnemyState state = state(JumpEnemyState.class);
        state.setAvatar(enemy);
        state.setMove(move);
        return state;
    }

    public static EntityState invade(Enemy enemy, ProjectileType projectileType) {
        InvadeEnemyState state = state(InvadeEnemyState.class);
        state.setAvatar(enemy);
        state.setProjectileType(projectileType);
        return state;
    }

    public static EntityState climb(Enemy enemy) {
        ClimbEnemyState state = state(ClimbEnemyState.class);
        state.setAvatar(enemy);
        return state;
    }

    public static EntityState boss(Boss boss, int bossMode) {
        BossState state = state(BossState.class);
        state.setAvatar(boss);
        state.setBossMode(bossMode);
        return state;
    }

    public static EntityState follow(Enemy enemy) {
        FollowEnemyState state = state(FollowEnemyState.class);
        state.setAvatar(enemy);
        return state;
    }
}
