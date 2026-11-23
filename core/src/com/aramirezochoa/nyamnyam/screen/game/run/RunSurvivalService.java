package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.GameState;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.state.EntityState;
import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** JSON damage never decrements persistent lives or respawns the player. */
public class RunSurvivalService {
    private final RunRuntime runtime;
    private final ToothHealth health;
    private float defeatElapsed;

    public RunSurvivalService(RunRuntime runtime, ToothHealth health) {
        this.runtime = runtime;
        this.health = health;
    }

    public void hit(MainAvatar player) {
        if (health.isDefeated() || !player.canPerformAction() || player.spawnTimer > 0) return;
        health.damage(BASIC_ENEMY_DAMAGE_HALVES);
        runtime.getMediaManager().playSound(MediaManager.SOUND_NYAM_HIT);
        // Reuse the existing blinking/invulnerability timer instead of a second overlapping window.
        player.spawnTimer = PLAYER_HIT_INVULNERABILITY_SECONDS;
        if (health.isDefeated()) beginDefeat(player);
    }

    public void beginDefeat(MainAvatar player) {
        defeatElapsed = 0;
        player.setEnabled(false);
        player.velocity.setZero();
        player.spawnTimer = -1;
        player.setCurrentState(new DeathPoseState());
    }

    /** Gameplay is stopped; only the death pose and presentation effects advance. */
    public void updateDefeat(float deltaTime) {
        defeatElapsed += deltaTime;
        if (defeatElapsed >= PLAYER_DEFEAT_DELAY_SECONDS && runtime.getGameStatus().getGameState() != GameState.LOSE) {
            runtime.getGameStatus().setGameState(GameState.LOSE);
        }
    }

    // Reuse the DEAD atlas animation without DeadEntityState's legacy lives/respawn side effects.
    private static class DeathPoseState extends EntityState {
        @Override public boolean update(float deltaTime) { return false; }
        @Override public String getCode() { return "DEAD"; }
        @Override public void reset() { }
    }
}
