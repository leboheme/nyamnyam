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
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

/** Prototype JSON combat rules. Locomotion and bubble contact physics remain independent. */
public class RunCombatService {
    private final RunRuntime context;
    private final Array<DefeatedEnemyFlight> defeatedFlights = new Array<DefeatedEnemyFlight>();

    public RunCombatService(RunRuntime context) {
        this.context = context;
    }

    /** Returns whether this attack applied a new impact; callers stop iterating after capture. */
    public boolean hit(Projectile attack, Enemy enemy) {
        if (!enemy.isEnabled() || enemy.isBoss() || !attack.registerImpact(enemy.getId())) return false;
        context.getMediaManager().playSound(MediaManager.SOUND_HIT_PROJECTILE);
        float size = Constant.CANDY_DIMENSION.x;
        float x = enemy.position.x + (enemy.dimension.x - size) / 2;
        float y = enemy.position.y + (enemy.dimension.y - size) / 2;
        if (!context.getBubbleTerrain().canOccupy(x, y, size, size)) {
            // The projectile's valid footprint is the closest safe location at impact.
            x = attack.position.x;
            y = attack.position.y;
        }
        Bubble bubble = EntityFactory.bubble(x, y, attack.getLookingAt(), context);
        if (bubble.capture(enemy)) {
            context.getEntityWorld().addBubble(bubble);
        } else {
            bubble.free();
        }
        if (attack.getType() != ProjectileType.SPIKE && attack.getType() != ProjectileType.BOMB) {
            attack.setEnabled(false);
            attack.setCurrentState(ProjectileStateFactory.explode(attack, false));
        }
        return true;
    }

    public void release(Enemy enemy, float x, float y, DirectionType facing) {
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
        context.getDropService().onDefeat(enemy);
        float direction = context.getMainAvatar() == null || enemy.position.x >= context.getMainAvatar().position.x
                ? 1 : -1;
        defeatedFlights.add(new DefeatedEnemyFlight(enemy, direction, context.getCollisionWorld()));
    }

    public void update(float deltaTime) {
        for (int i = defeatedFlights.size - 1; i >= 0; i--) {
            DefeatedEnemyFlight flight = defeatedFlights.get(i);
            if (flight.isFading()) {
                if (!flight.updateDeathFade(deltaTime)) continue;
                flight.enemy.free();
                defeatedFlights.removeIndex(i);
            } else if (flight.update(deltaTime)) {
                onDefeatedEnemyLanded(flight);
            }
        }
    }

    /** Future durable enemies can resolve this landing into another combat cycle. */
    private void onDefeatedEnemyLanded(DefeatedEnemyFlight flight) {
        flight.startDeathFade();
    }

    public boolean hasUnresolvedDefeatedEnemies() { return defeatedFlights.size > 0; }

    public void render(Batch batch) {
        if (defeatedFlights.size == 0) return;
        batch.begin();
        for (DefeatedEnemyFlight flight : defeatedFlights) flight.render(batch);
        batch.end();
    }

    public void dispose() {
        for (DefeatedEnemyFlight flight : defeatedFlights) flight.enemy.free();
        defeatedFlights.clear();
    }
}
