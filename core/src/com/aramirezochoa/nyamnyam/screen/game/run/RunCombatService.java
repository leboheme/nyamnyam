package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.GameplayContext;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.bubble.Bubble;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.Projectile;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.projectile.ProjectileStateFactory;

/** Prototype JSON combat rules. Locomotion and bubble contact physics remain independent. */
public class RunCombatService {
    // Temporary balance values: change Power here to compare ceil(resistance / Power) hits.
    private static final float INITIAL_RESISTANCE = 3f;
    private static final float ATTACK_POWER = 1f;
    private final GameplayContext context;

    public RunCombatService(GameplayContext context) {
        this.context = context;
    }

    public void prepareEnemy(Enemy enemy) {
        // A returning captive retains its original maximum, even if balance values change later.
        if (enemy.getInitialResistance() == 0) enemy.setInitialResistance(INITIAL_RESISTANCE);
    }

    public void prepareAttack(Projectile attack) {
        attack.setPower(ATTACK_POWER);
    }

    /** Returns whether this attack applied a new impact; callers stop iterating after capture. */
    public boolean hit(Projectile attack, Enemy enemy) {
        if (!enemy.isEnabled() || enemy.isBoss() || !attack.registerImpact(enemy.getId())) return false;
        enemy.reduceResistance(attack.getPower());
        enemy.showHitFeedback();
        context.getMediaManager().playSound(MediaManager.SOUND_HIT_PROJECTILE);
        if (enemy.getResistance() == 0) {
            Bubble bubble = EntityFactory.bubble(enemy.position.x, enemy.position.y, attack.getLookingAt(), context);
            if (bubble.capture(enemy)) {
                context.getEntityWorld().addBubble(bubble);
            } else {
                bubble.free();
            }
        }
        if (attack.getType() != ProjectileType.SPIKE && attack.getType() != ProjectileType.BOMB) {
            attack.setEnabled(false);
            attack.setCurrentState(ProjectileStateFactory.explode(attack, false));
        }
        return true;
    }

    public void release(Enemy enemy, float x, float y, DirectionType facing) {
        enemy.restoreResistance();
        enemy.anger(true);
        enemy.escapeBubble(x, y, facing);
    }

    public void defeat(Enemy enemy) {
        context.getMediaManager().playSound(MediaManager.SOUND_HIT_PROJECTILE);
        context.getGameStatus().addScore(Constant.SCORE_ENEMY, enemy.position.x + enemy.dimension.x / 2,
                enemy.position.y + enemy.dimension.y - Constant.SCREEN_GAME_Y_OFFSET);
        enemy.remove();
    }
}
