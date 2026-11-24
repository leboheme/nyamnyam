package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.CollisionBody;
import com.aramirezochoa.nyamnyam.screen.game.core.CollisionLayer;
import com.aramirezochoa.nyamnyam.screen.game.core.CollisionWorld;
import com.aramirezochoa.nyamnyam.screen.game.core.EntityWorld;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.*;

/** Owns room-entity gravity, terrain response, and layer-based physical contacts for JSON runs. */
public final class RunPhysicsSystem {
    private static final float COLLISION_EPSILON = 0.01f;

    private final CollisionWorld collisionWorld;
    private final Array<CollisionBody> bodies = new Array<CollisionBody>();

    public RunPhysicsSystem(CollisionWorld collisionWorld) {
        this.collisionWorld = collisionWorld;
    }

    public void update(EntityWorld entityWorld, Array<RunPickup> pickups, float deltaTime,
                       boolean resolvePlayerContacts) {
        entityWorld.collectCollisionBodies(bodies);
        for (RunPickup pickup : pickups) bodies.add(pickup);

        for (CollisionBody body : bodies) {
            if (body.isCollisionActive() && body.getCollisionLayer().isAffectedByGravity()) {
                updateFreeBody(body, deltaTime);
            }
        }

        for (int i = 0; i < bodies.size; i++) {
            CollisionBody first = bodies.get(i);
            if (!first.isCollisionActive()) continue;
            for (int j = i + 1; j < bodies.size; j++) {
                CollisionBody second = bodies.get(j);
                if (!second.isCollisionActive() || first == second || !canCollide(first, second)) continue;
                resolveContact(first, second, resolvePlayerContacts);
            }
        }
    }

    private void updateFreeBody(CollisionBody body, float deltaTime) {
        Vector2 velocity = body.getCollisionVelocity();
        velocity.y -= ROOM_FREE_ENTITY_GRAVITY * deltaTime;
        int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(velocity.x), Math.abs(velocity.y))
                * deltaTime / ROOM_PHYSICS_MAX_MOVEMENT_STEP));
        float stepTime = deltaTime / steps;
        boolean landed = false;

        for (int i = 0; i < steps; i++) {
            moveHorizontally(body, velocity, velocity.x * stepTime);
            if (moveVertically(body, velocity, velocity.y * stepTime)) landed = true;
        }

        if (landed) body.setCollisionRotation(0);

    }

    private void moveHorizontally(CollisionBody body, Vector2 velocity, float dx) {
        if (dx == 0) return;
        Rectangle bounds = body.getCollisionBounds();
        float tile = collisionWorld.getTileSize();
        float nextX = bounds.x + dx;
        float left = tile;
        float right = collisionWorld.getWidth() - tile - bounds.width;
        if (nextX < left || nextX > right) {
            float boundedX = Math.max(left, Math.min(right, nextX));
            body.moveByCollision(boundedX - bounds.x, 0);
            velocity.x = -velocity.x * DROP_ROOM_RESTITUTION;
        } else if (canOccupy(body, dx, 0)) {
            body.moveByCollision(dx, 0);
        } else {
            velocity.x = 0;
        }
    }

    private boolean moveVertically(CollisionBody body, Vector2 velocity, float dy) {
        if (dy == 0) return false;
        Rectangle bounds = body.getCollisionBounds();
        float tile = collisionWorld.getTileSize();
        float nextY = bounds.y + dy;
        float ceiling = collisionWorld.getHeight() - tile - bounds.height;
        if (nextY > ceiling) {
            body.moveByCollision(0, ceiling - bounds.y);
            velocity.y = -Math.abs(velocity.y) * DROP_ROOM_RESTITUTION;
            return false;
        }
        if (dy < 0) {
            float floor = findFloor(bounds, nextY, tile);
            if (!Float.isNaN(floor)) {
                body.moveByCollision(0, floor - bounds.y);
                velocity.setZero();
                return true;
            }
        }
        if (canOccupy(body, 0, dy)) body.moveByCollision(0, dy);
        else velocity.y = 0;
        return false;
    }

    private float findFloor(Rectangle bounds, float nextY, float roomFloor) {
        float floor = nextY < roomFloor ? roomFloor : Float.NaN;
        int tile = collisionWorld.getTileSize();
        int firstRow = Math.max(0, (int) Math.floor(nextY / tile) - 1);
        int lastRow = (int) Math.floor(bounds.y / tile);
        int firstColumn = (int) Math.floor((bounds.x + COLLISION_EPSILON) / tile);
        int lastColumn = (int) Math.floor((bounds.x + bounds.width - COLLISION_EPSILON) / tile);
        for (int row = firstRow; row <= lastRow; row++) {
            float tileTop = (row + 1) * tile;
            if (tileTop > bounds.y + COLLISION_EPSILON || tileTop < nextY - COLLISION_EPSILON) continue;
            for (int column = firstColumn; column <= lastColumn; column++) {
                if (collisionWorld.hasTile(column, row) && (Float.isNaN(floor) || tileTop > floor)) {
                    floor = tileTop;
                }
            }
        }
        return floor;
    }

    private boolean canOccupy(CollisionBody body, float dx, float dy) {
        Rectangle bounds = body.getCollisionBounds();
        float x = bounds.x + dx, y = bounds.y + dy;
        int tile = collisionWorld.getTileSize();
        if (x < tile || y < tile || x + bounds.width > collisionWorld.getWidth() - tile
                || y + bounds.height > collisionWorld.getHeight() - tile) return false;
        int firstColumn = (int) Math.floor((x + COLLISION_EPSILON) / tile);
        int lastColumn = (int) Math.floor((x + bounds.width - COLLISION_EPSILON) / tile);
        int firstRow = (int) Math.floor((y + COLLISION_EPSILON) / tile);
        int lastRow = (int) Math.floor((y + bounds.height - COLLISION_EPSILON) / tile);
        for (int row = firstRow; row <= lastRow; row++) {
            for (int column = firstColumn; column <= lastColumn; column++) {
                if (collisionWorld.hasTile(column, row)) return false;
            }
        }
        return true;
    }

    private boolean canCollide(CollisionBody first, CollisionBody second) {
        return CollisionLayer.canCollide(first.getCollisionLayer(), second.getCollisionLayer())
                && first.getCollisionBounds().overlaps(second.getCollisionBounds());
    }

    private void resolveContact(CollisionBody first, CollisionBody second, boolean resolvePlayerContacts) {
        CollisionLayer a = first.getCollisionLayer();
        CollisionLayer b = second.getCollisionLayer();
        if (a == CollisionLayer.FREE && b == CollisionLayer.FREE) {
            separateFreeBodies(first, second);
        } else if (resolvePlayerContacts && a == CollisionLayer.PLAYER && b == CollisionLayer.FREE) {
            pushFreeBody(first, second);
        } else if (resolvePlayerContacts && a == CollisionLayer.FREE && b == CollisionLayer.PLAYER) {
            pushFreeBody(second, first);
        } else if (resolvePlayerContacts && a == CollisionLayer.PLAYER && b == CollisionLayer.ANCHORED) {
            blockPlayer(first, second);
        } else if (resolvePlayerContacts && a == CollisionLayer.ANCHORED && b == CollisionLayer.PLAYER) {
            blockPlayer(second, first);
        } else if (a == CollisionLayer.FREE && b == CollisionLayer.BOMB) {
            pushFreeBody(second, first);
        } else if (a == CollisionLayer.BOMB && b == CollisionLayer.FREE) {
            pushFreeBody(first, second);
        }
    }

    private void pushFreeBody(CollisionBody player, CollisionBody freeBody) {
        Rectangle playerBox = player.getCollisionBounds();
        Rectangle freeBox = freeBody.getCollisionBounds();
        float direction = centerX(playerBox) < centerX(freeBox) ? 1 : -1;
        float overlap = direction > 0
                ? playerBox.x + playerBox.width - freeBox.x
                : freeBox.x + freeBox.width - playerBox.x;
        float displacement = Math.max(COLLISION_EPSILON, overlap + COLLISION_EPSILON);
        if (canOccupy(freeBody, direction * displacement, 0)) {
            freeBody.moveByCollision(direction * displacement, 0);
        } else {
            float reverse = -direction * displacement;
            if (canOccupy(player, reverse, 0)) player.moveByCollision(reverse, 0);
        }
    }

    private void blockPlayer(CollisionBody player, CollisionBody anchored) {
        Rectangle p = player.getCollisionBounds(), a = anchored.getCollisionBounds();
        float overlapX = Math.min(p.x + p.width, a.x + a.width) - Math.max(p.x, a.x);
        float overlapY = Math.min(p.y + p.height, a.y + a.height) - Math.max(p.y, a.y);
        if (overlapX <= overlapY) {
            float dx = centerX(p) <= centerX(a) ? -overlapX - COLLISION_EPSILON : overlapX + COLLISION_EPSILON;
            if (canOccupy(player, dx, 0)) player.moveByCollision(dx, 0);
        } else {
            float dy = centerY(p) <= centerY(a) ? -overlapY - COLLISION_EPSILON : overlapY + COLLISION_EPSILON;
            if (canOccupy(player, 0, dy)) player.moveByCollision(0, dy);
        }
    }

    private void separateFreeBodies(CollisionBody first, CollisionBody second) {
        Rectangle a = first.getCollisionBounds(), b = second.getCollisionBounds();
        float overlapX = Math.min(a.x + a.width, b.x + b.width) - Math.max(a.x, b.x);
        float overlapY = Math.min(a.y + a.height, b.y + b.height) - Math.max(a.y, b.y);
        boolean horizontal = overlapX <= overlapY;
        float sign = horizontal
                ? (centerX(a) <= centerX(b) ? -1f : 1f)
                : (centerY(a) <= centerY(b) ? -1f : 1f);
        float overlap = horizontal ? overlapX : overlapY;
        float half = overlap / 2 + COLLISION_EPSILON;
        float dx = horizontal ? sign * half : 0;
        float dy = horizontal ? 0 : sign * half;
        boolean canMoveFirst = canOccupy(first, dx, dy);
        boolean canMoveSecond = canOccupy(second, -dx, -dy);
        if (canMoveFirst && canMoveSecond) {
            first.moveByCollision(dx, dy);
            second.moveByCollision(-dx, -dy);
        } else if (canMoveFirst) {
            first.moveByCollision(horizontal ? sign * (overlap + COLLISION_EPSILON) : 0,
                    horizontal ? 0 : sign * (overlap + COLLISION_EPSILON));
        } else if (canMoveSecond) {
            second.moveByCollision(horizontal ? -sign * (overlap + COLLISION_EPSILON) : 0,
                    horizontal ? 0 : -sign * (overlap + COLLISION_EPSILON));
        }
        if (horizontal) {
            first.getCollisionVelocity().x = 0;
            second.getCollisionVelocity().x = 0;
        } else {
            first.getCollisionVelocity().y = 0;
            second.getCollisionVelocity().y = 0;
        }
    }

    private float centerX(Rectangle box) { return box.x + box.width / 2; }
    private float centerY(Rectangle box) { return box.y + box.height / 2; }
}
