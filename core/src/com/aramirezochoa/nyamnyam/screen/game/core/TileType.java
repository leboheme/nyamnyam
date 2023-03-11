package com.aramirezochoa.nyamnyam.screen.game.core;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by boheme on 13/01/15.
 */
public enum TileType {
    NO_BLOCK("NB"),
    TOP("TOP") {
        @Override
        public Vector2 checkCollisionY(float x0, float y0, float x1, float y1, Rectangle tile) {
            return checkCollisionTOP(x0, y0, x1, y1, tile, ZERO_UNO, UNO_UNO);
        }
    },
    TOP_LEFT("TOPL") {
        @Override
        public boolean checkCollisionX(float x0, float y0, float x1, float y1, Rectangle tile) {
            return LEFT.checkCollisionX(x0, y0, x1, y1, tile);
        }

        @Override
        public Vector2 checkCollisionY(float x0, float y0, float x1, float y1, Rectangle tile) {
            return checkCollisionTOP(x0, y0, x1, y1, tile, ZERO_UNO, UNO_UNO);
        }
    },
    TOP_RIGHT("TOPR") {
        @Override
        public boolean checkCollisionX(float x0, float y0, float x1, float y1, Rectangle tile) {
            return RIGHT.checkCollisionX(x0, y0, x1, y1, tile);
        }

        @Override
        public Vector2 checkCollisionY(float x0, float y0, float x1, float y1, Rectangle tile) {
            return checkCollisionTOP(x0, y0, x1, y1, tile, ZERO_UNO, UNO_UNO);
        }
    },
    TOP_BOTH("TOPRL") {
        @Override
        public boolean checkCollisionX(float x0, float y0, float x1, float y1, Rectangle tile) {
            if (x0 < x1) {
                return LEFT.checkCollisionX(x0, y0, x1, y1, tile);
            } else {
                return RIGHT.checkCollisionX(x0, y0, x1, y1, tile);
            }
        }

        @Override
        public Vector2 checkCollisionY(float x0, float y0, float x1, float y1, Rectangle tile) {
            return checkCollisionTOP(x0, y0, x1, y1, tile, ZERO_UNO, UNO_UNO);
        }
    },
    LEFT("LEFT") {
        @Override
        public boolean checkCollisionX(float x0, float y0, float x1, float y1, Rectangle tile) {
            if (x0 < x1 && tile.contains(x1, y1)) {
                return true;
            }
            return false;
        }
    },
    RIGHT("RIGHT") {
        @Override
        public boolean checkCollisionX(float x0, float y0, float x1, float y1, Rectangle tile) {
            if (x0 > x1 && tile.contains(x1, y1)) {
                return true;
            }
            return false;
        }
    },
    RIGHT_LEFT("RL") {
        @Override
        public boolean checkCollisionX(float x0, float y0, float x1, float y1, Rectangle tile) {
            if (x0 < x1) {
                return LEFT.checkCollisionX(x0, y0, x1, y1, tile);
            } else {
                return RIGHT.checkCollisionX(x0, y0, x1, y1, tile);
            }
        }
    },;

    private static Vector2 ZERO_UNO = new Vector2(0, 1);
    private static Vector2 UNO_UNO = new Vector2(1, 1);

    private final String code;

    TileType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public boolean checkCollisionX(float x0, float y0, float x1, float y1, Rectangle tile) {
        return false;
    }

    public Vector2 checkCollisionY(float x0, float y0, float x1, float y1, Rectangle tile) {
        return null;
    }

    protected Vector2 checkCollisionTOP(float x0, float y0, float x1, float y1, Rectangle tile, Vector2 origin, Vector2 end) {
        Vector2 result = new Vector2();
        if (Intersector.intersectLines(x0, y0, x1, y1,
                tile.x + origin.x * tile.getWidth(), tile.y + origin.y * tile.getHeight(),
                tile.x + end.x * tile.getWidth(), tile.y + end.y * tile.getHeight(),
                result) && y0 >= result.y && result.y >= y1) {
            return result;
        }
        return null;
    }

    public static TileType getTileType(TiledMapTileLayer.Cell cell) {
        if (cell == null || cell.getTile() == null || cell.getTile().getProperties().get("type") == null) {
            return NO_BLOCK;
        }
        return parseHillType((String) cell.getTile().getProperties().get("type"));
    }

    private static TileType parseHillType(String hill) {
        for (TileType tileType : TileType.values()) {
            if (tileType.code.equals(hill)) {
                return tileType;
            }
        }
        return NO_BLOCK;
    }

}