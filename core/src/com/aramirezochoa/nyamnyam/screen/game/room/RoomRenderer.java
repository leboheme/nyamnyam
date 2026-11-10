package com.aramirezochoa.nyamnyam.screen.game.room;

import com.aramirezochoa.nyamnyam.Constant;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

/** Renders room materials using the visual assignment stored on the runtime room. */
public class RoomRenderer {

    private final SpriteBatch batch = new SpriteBatch();
    private final TextureRegion[] tileRegions;
    private final int tilesetColumns;

    public RoomRenderer(Texture tilesTexture) {
        tilesetColumns = tilesTexture.getWidth() / Constant.TILE_SIZE;
        int tileRows = tilesTexture.getHeight() / Constant.TILE_SIZE;
        int tileCount = tilesetColumns * tileRows;
        tileRegions = new TextureRegion[tileCount];
        for (int tileIndex = 0; tileIndex < tileCount; tileIndex++) {
            int x = tileIndex % tilesetColumns * Constant.TILE_SIZE;
            int y = tileIndex / tilesetColumns * Constant.TILE_SIZE;
            tileRegions[tileIndex] = new TextureRegion(tilesTexture, x, y, Constant.TILE_SIZE, Constant.TILE_SIZE);
        }
    }

    public void render(RoomInstance room, Matrix4 projection) {
        if (room == null) {
            return;
        }
        batch.setProjectionMatrix(projection);
        batch.begin();
        for (int y = 0; y < room.getHeight(); y++) {
            for (int x = 0; x < room.getWidth(); x++) {
                RoomAppearance appearance = room.getAppearanceAt(x, y);
                if (appearance != null) {
                    batch.draw(tileRegions[appearance.getTileIndex()], (x + 1) * room.getTileSize(),
                            (room.getHeight() - y) * room.getTileSize());
                }
            }
        }
        batch.end();
    }

    public void dispose() {
        batch.dispose();
    }
}
