package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy;

import com.badlogic.gdx.math.Vector2;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.boss.BanquiBoss;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.boss.BoofBoss;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.boss.FollBoss;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.boss.NyamBoss;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.dark.DarkEnemy;

/**
 * Created by boheme on 13/01/15.
 */
public enum EnemyType implements EntityType {

    // Minions
    BOOF(BoofEnemy.class),
    PHANT(PhantEnemy.class) {
        public Vector2 getDimension() {
            return Constant.PHANT_DIMENSION;
        }
    },
    RINOS(RinosEnemy.class),
    LITO(LitoEnemy.class),
    FOLL(FollEnemy.class),
    MBER(MberEnemy.class),
    BANQUI(BanquiEnemy.class),

    // Dark
    DARK(DarkEnemy.class),

    // Bosses
    BOSS_BOOF(BoofBoss.class) {
        public Vector2 getDimension() {
            return Constant.BOSS_DIMENSION;
        }
    },
    BOSS_FOLL(FollBoss.class) {
        public Vector2 getDimension() {
            return Constant.BOSS_DIMENSION;
        }
    },
    BOSS_BANQUI(BanquiBoss.class) {
        public Vector2 getDimension() {
            return Constant.BOSS_DIMENSION;
        }
    },
    BOSS_NYAM(NyamBoss.class) {
        public Vector2 getDimension() {
            return Constant.SUPER_BOSS_DIMENSION;
        }
    };

    private final Class<? extends Enemy> enemyClass;

    EnemyType(Class<? extends Enemy> enemyClass) {
        this.enemyClass = enemyClass;
    }

    public Vector2 getDimension() {
        return Constant.AVATAR_DIMENSION;
    }


    public Class<? extends Enemy> getEnemyClass() {
        return enemyClass;
    }
}
