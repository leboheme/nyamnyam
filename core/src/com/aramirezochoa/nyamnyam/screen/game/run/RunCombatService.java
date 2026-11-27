package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.EntityFactory;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.bubble.Bubble;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.Projectile;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.ProjectileType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.projectile.ProjectileStateFactory;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Prototype JSON combat rules. Locomotion and bubble contact physics remain independent. */
public class RunCombatService {
    private final RunRuntime context;

    public RunCombatService(RunRuntime context) {
        this.context = context;
    }

    public void prepareEnemy(Enemy enemy) {
        // A returning captive retains its original maximum, even if balance values change later.
        if (enemy.getInitialResistance() == 0) enemy.setInitialResistance(ENEMY_INITIAL_RESISTANCE);
    }

    public void prepareAttack(Projectile attack) {
        RunStats stats = context.getRunStats();
        attack.setPower(stats == null ? ATTACK_POWER : stats.getPower());
    }

    /** Returns whether this attack applied a new impact; callers stop iterating after capture. */
    public boolean hit(Projectile attack, Enemy enemy) {
        if (!enemy.isEnabled() || enemy.isBoss() || !attack.registerImpact(enemy.getId())) return false;
        enemy.reduceResistance(attack.getPower());
        enemy.showHitFeedback();
        context.getMediaManager().playSound(MediaManager.SOUND_HIT_PROJECTILE);
        if (enemy.getResistance() == 0) {
            float size = Constant.CANDY_DIMENSION.x * attack.getBubbleScale();
            float x = enemy.position.x + (enemy.dimension.x - size) / 2;
            float y = enemy.position.y + (enemy.dimension.y - size) / 2;
            if (!context.getBubbleTerrain().canOccupy(x, y, size, size)) {
                // The projectile's valid footprint is the closest safe location at impact.
                x = attack.position.x;
                y = attack.position.y;
            }
            Bubble bubble = EntityFactory.bubble(x, y, attack.getLookingAt(), attack.getBubbleScale(), context);
            if (bubble.capture(enemy, context.getCaptureDuration())) {
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

    /** Debug action: same resolution and rewards as a remate, without changing run state. */
    public int defeatAllEnemies() {
        int defeated = 0;
        // Defeat removes and frees each enemy immediately; reverse iteration avoids stale references.
        for (int i = context.getEntityWorld().getEnemies().size - 1; i >= 0; i--) {
            defeat(context.getEntityWorld().getEnemies().get(i));
            defeated++;
        }
        for (int i = context.getEntityWorld().getBubbles().size - 1; i >= 0; i--) {
            Bubble bubble = context.getEntityWorld().getBubbles().get(i);
            if (bubble.hasCapturedEnemy() && bubble.isEnabled()) {
                // The owning bubble clears its reference before resolving/freeing the captive.
                bubble.resolveBurst();
                defeated++;
            }
        }
        return defeated;
    }

    public void defeat(Enemy enemy) {
        context.getMediaManager().playSound(MediaManager.SOUND_HIT_PROJECTILE);
        context.getGameStatus().addScore(Constant.SCORE_ENEMY, enemy.position.x + enemy.dimension.x / 2,
                enemy.position.y + enemy.dimension.y - Constant.SCREEN_GAME_Y_OFFSET);
        context.getDropService().onDefeat(enemy);
        enemy.remove();
    }
}
