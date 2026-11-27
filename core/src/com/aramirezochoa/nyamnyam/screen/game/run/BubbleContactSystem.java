package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.EntityWorld;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.bubble.Bubble;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** JSON bubble contacts, soft groups and iterative burst propagation. */
public class BubbleContactSystem {
    private final BubbleTerrain terrain;
    private final Rectangle previousPlayer = new Rectangle();
    private final Rectangle previousBubble = new Rectangle();
    private final Array<Bubble> bursts = new Array<Bubble>();

    public BubbleContactSystem(BubbleTerrain terrain) {
        this.terrain = terrain;
    }

    public void update(EntityWorld world, float deltaTime, boolean jumpRequested, float intendedX, float intendedY) {
        for (int i = 0; i < world.getBubbles().size; i++) world.getBubbles().get(i).relaxPressure(deltaTime);
        MainAvatar player = world.getMainAvatar();
        if (player != null && player.canPerformAction()) {
            previousPlayer.set(player.box);
            previousPlayer.x += player.previousPosition.x - player.position.x;
            previousPlayer.y += player.previousPosition.y - player.position.y;
            for (int i = 0; i < world.getBubbles().size; i++) {
                Bubble bubble = world.getBubbles().get(i);
                if (!bubble.isEnabled() || bubble.isBurstRequested()) continue;
                previousBubble.set(bubble.box);
                previousBubble.x += bubble.previousPosition.x - bubble.position.x;
                previousBubble.y += bubble.previousPosition.y - bubble.position.y;

                float oldGap = previousPlayer.y - (previousBubble.y + previousBubble.height);
                float newGap = player.box.y - (bubble.box.y + bubble.box.height);
                if (player.velocity.y < 0 && oldGap >= -BUBBLE_CONTACT_MARGIN && newGap <= 0
                        && overlapsHorizontallyAtCrossing(player.box, bubble.box, oldGap, newGap)) {
                    if (jumpRequested) {
                        movePlayer(player, 0, -newGap);
                        player.rebound();
                        break;
                    }
                    bubble.burst();
                    continue;
                }
                if (!touches(player.box, bubble.box)) continue;

                float oldHeadGap = previousBubble.y - (previousPlayer.y + previousPlayer.height);
                float newHeadGap = bubble.box.y - (player.box.y + player.box.height);
                if (oldHeadGap >= -BUBBLE_CONTACT_MARGIN && newHeadGap <= BUBBLE_CONTACT_MARGIN
                        && player.box.y < bubble.box.y) {
                    float risingSpeed = Math.max(0, intendedY);
                    float impactSpeed = risingSpeed - bubble.velocity.y;
                    if (impactSpeed >= BUBBLE_UNDERSIDE_BURST_IMPACT_SPEED) {
                        bubble.burst();
                        continue;
                    }
                    boolean dense = !canCompress(world, bubble, 0, BUBBLE_BLOCKED_PROBE_DISTANCE);
                    boolean blocked = pushBubble(world, bubble, 0, Math.max(BUBBLE_MIN_UPWARD_PUSH_SPEED, risingSpeed), 0);
                    if (risingSpeed > 0 || jumpRequested || dense) bubble.press(deltaTime, impactSpeed, blocked);
                    if (!bubble.isBurstRequested()) {
                        movePlayer(player, 0, Math.min(0, newHeadGap));
                        player.velocity.y = Math.min(0, player.velocity.y);
                    }
                    continue;
                }

                boolean playerOnLeft = previousPlayer.x + previousPlayer.width / 2
                        < previousBubble.x + previousBubble.width / 2;
                float penetration = playerOnLeft ? player.box.x + player.box.width - bubble.box.x
                        : bubble.box.x + bubble.box.width - player.box.x;
                if (penetration <= 0) continue;
                float outward = playerOnLeft ? 1 : -1;
                float relativeSpeed = outward * (intendedX - bubble.velocity.x);
                if (outward * intendedX > BUBBLE_CONTACT_MOVEMENT_EPSILON) {
                    // Only the contacted bubble moves: neighbors can overlap to form a cluster.
                    boolean blocked = pushBubble(world, bubble, 0, 0, outward * penetration);
                    bubble.press(blocked ? deltaTime : 0, relativeSpeed, blocked);
                } else {
                    // The incoming bubble carries a stationary (or retreating) player with it.
                    movePlayerAsFarAsPossible(player, -outward * penetration);
                    float remaining = playerOnLeft ? player.box.x + player.box.width - bubble.box.x
                            : bubble.box.x + bubble.box.width - player.box.x;
                    if (remaining > BUBBLE_CONTACT_MOVEMENT_EPSILON) {
                        boolean blocked = pushBubble(world, bubble, 0, 0, outward * remaining);
                        bubble.press(blocked ? deltaTime : 0, relativeSpeed, blocked);
                    }
                }
            }
        }
        propagateBursts(world);
    }

    private boolean movePlayer(MainAvatar player, float dx, float dy) {
        if (!terrain.canTravel(player.position.x, player.position.y, player.dimension.x, player.dimension.y, dx, dy)) return false;
        player.setPosition(player.position.x + dx, player.position.y + dy);
        return true;
    }

    private void movePlayerAsFarAsPossible(MainAvatar player, float dx) {
        float fraction = travelFraction(player.position.x, player.position.y,
                player.dimension.x, player.dimension.y, dx);
        player.setPosition(player.position.x + dx * fraction, player.position.y);
    }

    private float travelFraction(float x, float y, float width, float height, float dx) {
        if (terrain.canTravel(x, y, width, height, dx, 0)) return 1;
        float allowed = 0, blocked = 1;
        for (int i = 0; i < 12; i++) {
            float middle = (allowed + blocked) / 2;
            if (terrain.canTravel(x, y, width, height, dx * middle, 0)) allowed = middle;
            else blocked = middle;
        }
        return allowed;
    }

    private boolean pushBubble(EntityWorld world, Bubble bubble, float x, float y, float displacementX) {
        float probeX = Math.signum(displacementX != 0 ? displacementX : x) * BUBBLE_BLOCKED_PROBE_DISTANCE;
        float probeY = Math.signum(y) * BUBBLE_BLOCKED_PROBE_DISTANCE;
        boolean blocked = !terrain.canTravel(bubble.position.x, bubble.position.y,
                bubble.dimension.x, bubble.dimension.y, probeX, probeY)
                || !canCompress(world, bubble, probeX, probeY);
        bubble.push(blocked ? 0 : x, blocked ? 0 : y);
        if (displacementX != 0) {
            float fraction = travelFraction(bubble.position.x, bubble.position.y,
                    bubble.dimension.x, bubble.dimension.y, displacementX);
            if (!canCompress(world, bubble, displacementX * fraction, 0)) {
                float allowed = 0, dense = fraction;
                for (int i = 0; i < 12; i++) {
                    float middle = (allowed + dense) / 2;
                    if (canCompress(world, bubble, displacementX * middle, 0)) allowed = middle;
                    else dense = middle;
                }
                fraction = allowed;
                blocked = true;
            }
            if (fraction < 1) blocked = true;
            bubble.setPosition(bubble.position.x + displacementX * fraction, bubble.position.y);
        }
        return blocked;
    }

    private boolean canCompress(EntityWorld world, Bubble bubble, float dx, float dy) {
        // Existing dense clusters may decompress; simply being dense never creates pressure.
        float current = density(world, bubble, 0, 0);
        return density(world, bubble, dx, dy) <= Math.max(BUBBLE_CLUSTER_MAX_OVERLAP,
                current + BUBBLE_CONTACT_MOVEMENT_EPSILON);
    }

    private float density(EntityWorld world, Bubble bubble, float dx, float dy) {
        Rectangle box = bubble.box;
        float total = 0;
        for (Bubble other : world.getBubbles()) {
            if (other == bubble || !other.isEnabled() || other.isBurstRequested()) continue;
            float overlapX = Math.min(box.x + dx + box.width, other.box.x + other.box.width)
                    - Math.max(box.x + dx, other.box.x);
            float overlapY = Math.min(box.y + dy + box.height, other.box.y + other.box.height)
                    - Math.max(box.y + dy, other.box.y);
            if (overlapX > 0 && overlapY > 0
                    && terrain.connects(centerX(bubble) + dx, centerY(bubble) + dy, centerX(other), centerY(other))) {
                total += overlapX * overlapY / (box.width * box.height);
            }
        }
        return total;
    }

    private void propagateBursts(EntityWorld world) {
        bursts.clear();
        for (int i = 0; i < world.getBubbles().size; i++) {
            Bubble bubble = world.getBubbles().get(i);
            if (bubble.isEnabled() && bubble.isBurstRequested()) bursts.add(bubble);
        }
        // Discover neighbors before resolving. Bubble instances remain in the world until their animation ends.
        for (int i = 0; i < bursts.size; i++) {
            Bubble bubble = bursts.get(i);
            for (int j = 0; j < world.getBubbles().size; j++) {
                Bubble candidate = world.getBubbles().get(j);
                if (candidate.isEnabled() && !bursts.contains(candidate, true)
                        && connected(bubble, candidate)) bursts.add(candidate);
            }
            bubble.resolveBurst();
        }
        bursts.clear();
    }

    private boolean connected(Bubble a, Bubble b) {
        float dx = centerX(a) - centerX(b);
        float dy = centerY(a) - centerY(b);
        float reach = radius(a) + radius(b) + BUBBLE_CONTACT_MARGIN;
        return dx * dx + dy * dy <= reach * reach
                && terrain.connects(centerX(a), centerY(a), centerX(b), centerY(b));
    }

    private float radius(Bubble bubble) { return Math.min(bubble.dimension.x, bubble.dimension.y) / 2; }
    private float centerX(Bubble bubble) { return bubble.position.x + bubble.dimension.x / 2; }
    private float centerY(Bubble bubble) { return bubble.position.y + bubble.dimension.y / 2; }

    private boolean touches(Rectangle a, Rectangle b) {
        return a.x <= b.x + b.width + BUBBLE_CONTACT_MARGIN && a.x + a.width + BUBBLE_CONTACT_MARGIN >= b.x
                && a.y <= b.y + b.height + BUBBLE_CONTACT_MARGIN && a.y + a.height + BUBBLE_CONTACT_MARGIN >= b.y;
    }

    private boolean overlapsHorizontallyAtCrossing(Rectangle player, Rectangle bubble, float oldGap, float newGap) {
        float fraction = oldGap == newGap ? 0 : Math.max(0, Math.min(1, oldGap / (oldGap - newGap)));
        float playerX = previousPlayer.x + (player.x - previousPlayer.x) * fraction;
        float bubbleX = previousBubble.x + (bubble.x - previousBubble.x) * fraction;
        return playerX < bubbleX + bubble.width && playerX + player.width > bubbleX;
    }
}
