package com.aramirezochoa.nyamnyam.screen.game.room;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

/** Draws the semantic material grid over the legacy TMX stage for comparison. */
public class RoomDebugRenderer {

    private final ShapeRenderer renderer = new ShapeRenderer();

    public void render(RoomInstance room, Matrix4 projection) {
        if (room == null) {
            return;
        }

        renderer.setProjectionMatrix(projection);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(0.2f, 1f, 0.2f, 1f);
        int tileSize = room.getTileSize();
        for (int y = 0; y < room.getHeight(); y++) {
            for (int x = 0; x < room.getWidth(); x++) {
                if (room.getMaterialAt(x, y) != null) {
                    renderer.rect((x + 1) * tileSize, (room.getHeight() - y) * tileSize,
                            tileSize, tileSize);
                }
            }
        }
        renderer.end();
    }

    public void dispose() {
        renderer.dispose();
    }
}
