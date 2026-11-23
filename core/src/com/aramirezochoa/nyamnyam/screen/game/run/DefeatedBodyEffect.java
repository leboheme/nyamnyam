package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.types.DirectionType;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Snapshot only: owns no Enemy and cannot affect collision, pools or rewards. */
public class DefeatedBodyEffect {
    private final TextureRegion frame;
    private float x, y, velocityY = DEFEAT_BODY_UPWARD_SPEED, elapsed;
    private float velocityX, rotation;
    private final float left, right;

    public DefeatedBodyEffect(Enemy enemy, float direction) {
        left = enemy.getParent().getCollisionWorld().getTileSize();
        right = enemy.getParent().getCollisionWorld().getWidth() - left;
        frame = enemy.copyCurrentFrame();
        if (enemy.getLookingAt() == DirectionType.LEFT) frame.flip(true, false);
        x = enemy.position.x - (frame.getRegionWidth() - enemy.dimension.x) / 2;
        y = enemy.position.y;
        velocityX = direction * DEFEAT_BODY_HORIZONTAL_SPEED;
    }

    public boolean update(float deltaTime) {
        elapsed += deltaTime;
        x += velocityX * deltaTime;
        if (x < left) {
            x = left;
            velocityX = Math.abs(velocityX);
        } else if (x + frame.getRegionWidth() > right) {
            x = right - frame.getRegionWidth();
            velocityX = -Math.abs(velocityX);
        }
        rotation -= Math.signum(velocityX) * DEFEAT_BODY_SPIN_DEGREES_PER_SECOND * deltaTime;
        y += velocityY * deltaTime;
        velocityY -= DEFEAT_BODY_GRAVITY * deltaTime;
        return elapsed >= DEFEAT_BODY_SECONDS;
    }

    public void render(Batch batch) {
        float alpha = Math.min(1, Math.max(0, (DEFEAT_BODY_SECONDS - elapsed) / DEFEAT_BODY_FADE_SECONDS));
        batch.setColor(1, 1, 1, alpha);
        float width = frame.getRegionWidth(), height = frame.getRegionHeight();
        batch.draw(frame, x, y, width / 2, height / 2, width, height, 1, 1, rotation);
    }
}
