package com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.core.TileType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.Entity;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.enemy.Enemy;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.projectile.Projectile;

/**
 * Created by boheme on 13/01/15.
 */
public class GameWrapperAssistant {

    private static final String COLLISION_LAYER = "collision";
    private static Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    private static Pool<Vector2> pointPool = new Pool<Vector2>() {
        @Override
        protected Vector2 newObject() {
            return new Vector2();
        }
    };
    private static Array<Rectangle> tiles = new Array<Rectangle>();

    public static void updateEntity(Entity entity, float deltaTime, boolean collideAllTiles) {

        entity.velocity.add(Constant.GRAVITY).add(entity.acceleration);

        if (Math.abs(entity.velocity.x) < 1) {
            entity.velocity.x = 0;
        }

        if (entity.velocity.y < entity.getMaxNegativeVelocity()) {
            entity.velocity.y = entity.getMaxNegativeVelocity();
        }

        if (entity.velocity.y > entity.getMaxPositiveVelocity()) {
            entity.velocity.y = entity.getMaxPositiveVelocity();
        }

        entity.velocity.scl(deltaTime);

        // Check collisions
        checkSides(entity);
        checkBottom(entity);
        if (collideAllTiles) {
            checkTop(entity);
        }
        checkCollisionX(entity, entity.getParent().getMap(), collideAllTiles);
        checkCollisionY(entity, entity.getParent().getMap(), collideAllTiles);

        entity.position.add(entity.velocity);
        entity.velocity.scl(1 / deltaTime);
        entity.dampVelocity();
    }

    public static void updateBoss(Entity entity, float deltaTime) {

        if (Math.abs(entity.velocity.x) < 1) {
            entity.velocity.x = 0;
        }

        entity.velocity.scl(deltaTime);

        // Check collisions
        checkSides(entity);
        if (entity.position.y < Constant.TILE_SIZE) {
            entity.position.y = Constant.TILE_SIZE;
            entity.velocity.y = 0;
        }
        if (entity.position.y + entity.dimension.y > Constant.SCREEN_HEIGHT + Constant.SCREEN_GAME_Y_OFFSET - Constant.TILE_SIZE) {
            entity.position.y = Constant.SCREEN_HEIGHT + Constant.SCREEN_GAME_Y_OFFSET - Constant.TILE_SIZE - entity.dimension.y;
            entity.velocity.y = 0;
        }

        entity.position.add(entity.velocity);
        entity.velocity.scl(1 / deltaTime);
        entity.dampVelocity();
    }

    public static void updateProjectile(Projectile projectile, float deltaTime, boolean tileCollision) {
        projectile.velocity.add(Constant.GRAVITY).add(projectile.acceleration);

        if (Math.abs(projectile.velocity.x) < 1) {
            projectile.velocity.x = 0;
        }

        if (projectile.velocity.y < projectile.getMaxNegativeVelocity()) {
            projectile.velocity.y = projectile.getMaxNegativeVelocity();
        }

        projectile.velocity.scl(deltaTime);

        if (tileCollision) {
            checkCollisionX(projectile, projectile.getParent().getMap(), true);
            checkCollisionY(projectile, projectile.getParent().getMap(), true);
        }

        projectile.position.add(projectile.velocity);
        projectile.velocity.scl(1 / deltaTime);
        projectile.dampVelocity();
    }

    public static void updateEntityNoCollisions(Entity entity, float deltaTime) {

        entity.velocity.add(Constant.GRAVITY).add(entity.acceleration);

        if (Math.abs(entity.velocity.x) < 1) {
            entity.velocity.x = 0;
        }

        if (entity.velocity.y < entity.getMaxNegativeVelocity()) {
            entity.velocity.y = entity.getMaxNegativeVelocity();
        }

        if (entity.velocity.y > entity.getMaxPositiveVelocity()) {
            entity.velocity.y = entity.getMaxPositiveVelocity();
        }

        entity.velocity.scl(deltaTime);

        entity.position.add(entity.velocity);
        entity.velocity.scl(1 / deltaTime);
        entity.dampVelocity();
    }

    private static void checkCollisionX(Entity entity, TiledMap map, boolean collideAllTiles) {

        Rectangle avatarRect = createRectangleFromEntity(entity);
        avatarRect.x += entity.velocity.x;

        // Points of interest
        Vector2 sidePosition = pointPool.obtain();
        sidePosition.x = entity.velocity.x > 0 ? avatarRect.x + avatarRect.width : avatarRect.x;
        sidePosition.y = avatarRect.y + 5;

        int startX, startY, endX, endY;
        if (entity.velocity.x > 0) {
            startX = endX = (int) (avatarRect.x + avatarRect.width);
        } else {
            startX = endX = (int) (avatarRect.x);
        }
        startY = (int) (entity.position.y);
        endY = (int) (entity.position.y + entity.dimension.y);
        getTiles(startX, startY, endX, endY, tiles, map, entity.velocity.x > 0);

        boolean walled = false;
        for (Rectangle tile : tiles) {
            if (avatarRect.overlaps(tile)) {
                if (collideAllTiles) {
                    if (entity.velocity.x > 0) {
                        entity.position.x = tile.x - entity.dimension.x;
                    } else if (entity.velocity.x < 0) {
                        entity.position.x = tile.x + tile.width;
                    }
                    entity.velocity.x = 0;
                    walled = true;
                } else {
                    TiledMapTileLayer.Cell cell = getCell(tile.x, tile.y, map);
                    TileType tileType = TileType.getTileType(cell);
                    if (!TileType.NO_BLOCK.equals(tileType)) {
                        if (tileType.checkCollisionX(entity.position.x + entity.dimension.x / 2, entity.position.y, sidePosition.x, sidePosition.y, tile)) {
                            if (entity.velocity.x > 0) {
                                entity.position.x = tile.x - entity.dimension.x;
                            } else if (entity.velocity.x < 0) {
                                entity.position.x = tile.x + tile.width;
                            }
                            entity.velocity.x = 0;
                            walled = true;
                        }
                    }
                }
            }
        }
        if (walled) entity.notifyWall();

        pointPool.free(sidePosition);
        rectPool.free(avatarRect);
    }

    private static void checkCollisionY(Entity entity, TiledMap map, boolean collideAllTiles) {

        Rectangle avatarRect = rectPool.obtain();
        avatarRect.set(entity.position.x, entity.position.y, entity.dimension.x, entity.dimension.y);
        avatarRect.x += entity.velocity.x;
        avatarRect.y += entity.velocity.y;
        if (avatarRect.y < 0) avatarRect.y = 0;

        // Points of interest
        Rectangle collisionPoint = rectPool.obtain();
        collisionPoint.set(avatarRect.x, entity.velocity.y > 0 ? avatarRect.y + avatarRect.height : avatarRect.y, entity.dimension.x, 1);

        int startX, startY, endX, endY;
        if (entity.velocity.y <= 0) {
            startY = (int) (avatarRect.y);
            endY = (int) (avatarRect.y) + Constant.TILE_SIZE;
        } else {
            startY = endY = (int) (avatarRect.y + avatarRect.height);
        }
        startX = (int) avatarRect.x + 5;
        endX = (int) (avatarRect.x + avatarRect.getWidth() - 5);
        getTiles(startX, startY, endX, endY, tiles, map, entity.velocity.x >= 0);

        entity.grounded = false;
        // It gets the tiles ordered depending on the direction side (left/right)
        for (Rectangle tile : tiles) {
            if (avatarRect.overlaps(tile)) {
                if (collideAllTiles) {
                    if (entity.velocity.y > 0) {
                        entity.position.y = tile.y - entity.dimension.y;
                    } else if (entity.velocity.y < 0) {
                        entity.position.y = tile.y + tile.height;
                        entity.grounded = true;
                    }
                    entity.velocity.y = 0;
                } else {
                    TileType tileType = TileType.getTileType(getCell(tile.x, tile.y, map));
                    if (!TileType.NO_BLOCK.equals(tileType)) {
                        if (entity.velocity.y <= 0) {
                            Vector2 intersection = tileType.checkCollisionY(entity.position.x + entity.dimension.x / 2, entity.position.y, collisionPoint.x, collisionPoint.y, tile);
                            if (intersection != null) {
                                entity.position.y = intersection.y;
                                entity.velocity.y = 0;
                                entity.grounded = true;
                            }
                        }
                    }
                }
            }
        }

        rectPool.free(collisionPoint);
        rectPool.free(avatarRect);
    }

    private static void checkTop(Entity entity) {
        if (entity.position.y > Constant.SCREEN_HEIGHT + Constant.SCREEN_GAME_Y_OFFSET + entity.dimension.y) {
            entity.position.y = -entity.dimension.y;
        }
    }

    private static void checkBottom(Entity entity) {
        if (entity.position.y < -entity.dimension.y) {
            entity.position.y = Constant.SCREEN_HEIGHT + Constant.SCREEN_GAME_Y_OFFSET;
        }
    }

    private static void checkSides(Entity entity) {
        if (entity.position.x < Constant.TILE_SIZE) {
            entity.position.x = Constant.TILE_SIZE;
            entity.velocity.x = 0;
            entity.notifyWall();
        }
        if (entity.position.x + entity.dimension.x > Constant.SCREEN_WIDTH - Constant.TILE_SIZE) {
            entity.position.x = Constant.SCREEN_WIDTH - entity.dimension.x - Constant.TILE_SIZE;
            entity.velocity.x = 0;
            entity.notifyWall();
        }

        /*if (entity.position.y < 0) {
            if (entity.position.x < Constant.SCREEN_HOLE_LIMIT_0) {
                entity.position.x = Constant.SCREEN_HOLE_LIMIT_0;
                entity.velocity.x = 0;
            } else if (entity.position.x > Constant.SCREEN_HOLE_LIMIT_3) {
                entity.position.x = Constant.SCREEN_HOLE_LIMIT_3 - entity.dimension.x;
                entity.velocity.x = 0;
            } else if (entity.position.x > Constant.SCREEN_HOLE_LIMIT_1 && entity.position.x < (Constant.SCREEN_WIDTH / 2)) {
                entity.position.x = Constant.SCREEN_HOLE_LIMIT_1 - entity.dimension.x;
                entity.velocity.x = 0;
            } else if (entity.position.x > Constant.SCREEN_HOLE_LIMIT_1 && entity.position.x < Constant.SCREEN_HOLE_LIMIT_2) {
                entity.position.x = Constant.SCREEN_HOLE_LIMIT_2;
                entity.velocity.x = 0;
            }
        }*/
    }

    private static void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles, TiledMap map, boolean right) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(COLLISION_LAYER);
        int tileSize = Constant.TILE_SIZE;
        rectPool.freeAll(tiles);
        tiles.clear();

        if (right) {
            for (int y = startY / tileSize; y <= endY / tileSize; y++) {
                for (int x = startX / tileSize; x <= endX / tileSize; x++) {
                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                    if (cell != null) {
                        Rectangle rect = rectPool.obtain();
                        rect.set(x * tileSize, y * tileSize, tileSize, tileSize);
                        tiles.add(rect);
                    }
                }
            }
        } else {
            for (int y = startY / tileSize; y <= endY / tileSize; y++) {
                for (int x = endX / tileSize; x >= startX / tileSize; x--) {
                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                    if (cell != null) {
                        Rectangle rect = rectPool.obtain();
                        rect.set(x * tileSize, y * tileSize, tileSize, tileSize);
                        tiles.add(rect);
                    }
                }
            }
        }
    }

    private static TiledMapTileLayer.Cell getCell(float x, float y, TiledMap map) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(COLLISION_LAYER);
        return layer.getCell((int) x / Constant.TILE_SIZE, (int) y / Constant.TILE_SIZE);
    }

    private static Rectangle createRectangleFromEntity(Entity entity) {
        Rectangle rect = rectPool.obtain();
        rect.set(entity.position.x, entity.position.y, entity.dimension.x, entity.dimension.y);
        return rect;
    }

    public static boolean isNextStepGrounded(Enemy enemy, TiledMap map) {
        boolean isGrounded = false;
        Rectangle avatarRect = rectPool.obtain();
        avatarRect.set(enemy.position.x, enemy.position.y, enemy.dimension.x, enemy.dimension.y);
        avatarRect.x += Math.signum(enemy.velocity.x) * (enemy.dimension.x / 2);
        if (avatarRect.x < Constant.TILE_SIZE || avatarRect.x > Constant.SCREEN_WIDTH - Constant.TILE_SIZE) {
            avatarRect.x = enemy.position.x;
        }
        avatarRect.y -= 5f;

        // Points of interest
        Rectangle collisionPoint = rectPool.obtain();
        collisionPoint.set(avatarRect.x, avatarRect.y, enemy.dimension.x, 1);

        int startX, startY, endX, endY;
        startY = (int) (avatarRect.y);
        endY = (int) (avatarRect.y) + Constant.TILE_SIZE;
        startX = (int) avatarRect.x;
        endX = (int) (avatarRect.x + avatarRect.getWidth() - 1);

        getTiles(startX, startY, endX, endY, tiles, map, enemy.velocity.x >= 0);

        for (Rectangle tile : tiles) {
            if (avatarRect.overlaps(tile)) {
                TileType tileType = TileType.getTileType(getCell(tile.x, tile.y, map));
                if (!TileType.NO_BLOCK.equals(tileType)) {
                    Vector2 intersection = tileType.checkCollisionY(enemy.position.x + enemy.dimension.x / 2, enemy.position.y, collisionPoint.x, collisionPoint.y, tile);
                    if (intersection != null) {
                        isGrounded = true;
                    }
                }
            }
        }

        rectPool.free(collisionPoint);
        rectPool.free(avatarRect);
        return isGrounded;
    }
}
