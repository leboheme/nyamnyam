package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.EntityWorld;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatar;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.bubble.Bubble;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

/** JSON bubble contacts, soft groups and iterative burst propagation. */
public class BubbleContactSystem {
    private static final float CONTACT_EPSILON = 0.25f;
    private static final float GROUP_SEPARATION_SPEED = 60f;
    private static final float PUSH_MULTIPLIER = 1.5f;
    private final BubbleTerrain terrain;
    private final Rectangle previousPlayer = new Rectangle();
    private final Rectangle previousBubble = new Rectangle();
    private final Array<Bubble> group = new Array<Bubble>();
    private final FloatArray separationBudget = new FloatArray();
    private final Array<Bubble> bursts = new Array<Bubble>();

    public BubbleContactSystem(BubbleTerrain terrain) {
        this.terrain = terrain;
    }

    public void update(EntityWorld world, float deltaTime, boolean jumpRequested, float intendedX, float intendedY) {
        for (int i = 0; i < world.getBubbles().size; i++) world.getBubbles().get(i).relaxPressure(deltaTime);
        settleGroups(world, deltaTime);
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
                if (player.velocity.y < 0 && oldGap >= -CONTACT_EPSILON && newGap <= 0
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
                if (oldHeadGap >= -CONTACT_EPSILON && newHeadGap <= CONTACT_EPSILON
                        && player.box.y < bubble.box.y) {
                    float risingSpeed = Math.max(0, intendedY);
                    boolean blocked = pushGroup(world, bubble, 0, Math.max(40, risingSpeed));
                    if (risingSpeed > 0 || jumpRequested) bubble.press(deltaTime, risingSpeed - bubble.velocity.y, blocked);
                    if (!bubble.isBurstRequested()) {
                        movePlayer(player, 0, Math.min(0, newHeadGap));
                        player.velocity.y = Math.min(0, player.velocity.y);
                    }
                    continue;
                }

                boolean playerOnLeft = previousPlayer.x + previousPlayer.width / 2
                        < previousBubble.x + previousBubble.width / 2;
                float correction = playerOnLeft ? bubble.box.x - (player.box.x + player.box.width)
                        : bubble.box.x + bubble.box.width - player.box.x;
                float towardBubble = playerOnLeft ? intendedX : -intendedX;
                if (towardBubble > 1) {
                    boolean blocked = pushGroup(world, bubble, intendedX * PUSH_MULTIPLIER, 0);
                    float relativeSpeed = playerOnLeft ? intendedX - bubble.velocity.x : bubble.velocity.x - intendedX;
                    bubble.press(deltaTime, relativeSpeed, blocked);
                }
                if (!bubble.isBurstRequested() && !movePlayer(player, correction, 0)) {
                    // The bubble is squeezing the player against terrain; never teleport through it.
                    bubble.press(deltaTime, 0, true);
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

    private boolean pushGroup(EntityWorld world, Bubble first, float x, float y) {
        group.clear();
        group.add(first);
        boolean blocked = false;
        for (int i = 0; i < group.size; i++) {
            Bubble member = group.get(i);
            if (!terrain.canTravel(member.position.x, member.position.y, member.dimension.x, member.dimension.y,
                    Math.signum(x) * 2, Math.signum(y) * 2)) blocked = true;
            member.push(x, y);
            for (int j = 0; j < world.getBubbles().size; j++) {
                Bubble candidate = world.getBubbles().get(j);
                if (candidate.isEnabled() && !candidate.isBurstRequested() && !group.contains(candidate, true)
                        && connected(member, candidate)) group.add(candidate);
            }
        }
        group.clear();
        return blocked;
    }

    private void settleGroups(EntityWorld world, float deltaTime) {
        separationBudget.clear();
        for (int i = 0; i < world.getBubbles().size; i++) separationBudget.add(GROUP_SEPARATION_SPEED * deltaTime);
        for (int i = 0; i < world.getBubbles().size; i++) {
            Bubble a = world.getBubbles().get(i);
            if (!a.isEnabled() || a.isBurstRequested()) continue;
            for (int j = i + 1; j < world.getBubbles().size; j++) {
                Bubble b = world.getBubbles().get(j);
                if (!b.isEnabled() || b.isBurstRequested() || !connected(a, b)) continue;
                float dx = centerX(b) - centerX(a);
                float dy = centerY(b) - centerY(a);
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float overlap = radius(a) + radius(b) - distance;
                if (overlap <= 0) continue;
                if (distance < 0.001f) {
                    dx = a.getId() < b.getId() ? 1 : -1;
                    dy = 0;
                    distance = 1;
                }
                float correction = Math.min(overlap / 2, GROUP_SEPARATION_SPEED * deltaTime);
                float correctionA = Math.min(correction, separationBudget.get(i));
                float correctionB = Math.min(correction, separationBudget.get(j));
                if (moveBubble(a, -dx / distance * correctionA, -dy / distance * correctionA)) {
                    separationBudget.set(i, separationBudget.get(i) - correctionA);
                }
                if (moveBubble(b, dx / distance * correctionB, dy / distance * correctionB)) {
                    separationBudget.set(j, separationBudget.get(j) - correctionB);
                }
            }
        }
    }

    private boolean moveBubble(Bubble bubble, float dx, float dy) {
        if (terrain.canTravel(bubble.position.x, bubble.position.y, bubble.dimension.x, bubble.dimension.y, dx, dy)) {
            bubble.setPosition(bubble.position.x + dx, bubble.position.y + dy);
            return true;
        }
        return false;
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
        float reach = radius(a) + radius(b) + CONTACT_EPSILON;
        return dx * dx + dy * dy <= reach * reach
                && terrain.connects(centerX(a), centerY(a), centerX(b), centerY(b));
    }

    private float radius(Bubble bubble) { return Math.min(bubble.dimension.x, bubble.dimension.y) / 2; }
    private float centerX(Bubble bubble) { return bubble.position.x + bubble.dimension.x / 2; }
    private float centerY(Bubble bubble) { return bubble.position.y + bubble.dimension.y / 2; }

    private boolean touches(Rectangle a, Rectangle b) {
        return a.x <= b.x + b.width + CONTACT_EPSILON && a.x + a.width + CONTACT_EPSILON >= b.x
                && a.y <= b.y + b.height + CONTACT_EPSILON && a.y + a.height + CONTACT_EPSILON >= b.y;
    }

    private boolean overlapsHorizontallyAtCrossing(Rectangle player, Rectangle bubble, float oldGap, float newGap) {
        float fraction = oldGap == newGap ? 0 : Math.max(0, Math.min(1, oldGap / (oldGap - newGap)));
        float playerX = previousPlayer.x + (player.x - previousPlayer.x) * fraction;
        float bubbleX = previousBubble.x + (bubble.x - previousBubble.x) * fraction;
        return playerX < bubbleX + bubble.width && playerX + player.width > bubbleX;
    }
}
