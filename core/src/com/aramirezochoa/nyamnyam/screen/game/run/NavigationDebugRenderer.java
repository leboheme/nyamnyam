package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.room.RoomConnector;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

/** Temporary world overlay for reviewing room boundaries and available portals. */
final class NavigationDebugRenderer {
    private final ShapeRenderer renderer = new ShapeRenderer();

    void render(PlacedRoom placement, Matrix4 projection) {
        RoomInstance room = placement.getRoom();
        int tile = room.getTileSize();
        renderer.setProjectionMatrix(projection);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(0.2f, 0.5f, 0.75f, 1f);
        for (int row = 0; row < room.getHeight(); row++) {
            for (int col = 0; col < room.getWidth(); col++) {
                if (!room.isPlayableAt(col, row)) continue;
                float x = (col + 1) * tile;
                float y = (room.getHeight() - row) * tile;
                if (col == 0 || !room.isPlayableAt(col - 1, row)) renderer.line(x, y, x, y + tile);
                if (col == room.getWidth() - 1 || !room.isPlayableAt(col + 1, row)) {
                    renderer.line(x + tile, y, x + tile, y + tile);
                }
                if (row == 0 || !room.isPlayableAt(col, row - 1)) renderer.line(x, y + tile, x + tile, y + tile);
                if (row == room.getHeight() - 1 || !room.isPlayableAt(col, row + 1)) renderer.line(x, y, x + tile, y);
            }
        }
        for (RoomConnector door : room.getConnectors()) {
            if (placement.getLink(door.getSlot()) == null) renderer.setColor(0.5f, 0.5f, 0.5f, 1f);
            else renderer.setColor(0.35f, 1f, 0.35f, 1f);
            float x = door.getX() + tile;
            float y = door.getY() + tile;
            if (door.getWidth() == 0) renderer.line(x, y, x, y + door.getHeight());
            else {
                renderer.rect(x, y, door.getWidth(), door.getHeight());
                renderer.line(x + door.getWidth() / 2f, y,
                        x + door.getWidth() / 2f, y + door.getHeight());
            }
        }
        renderer.end();
    }

    void dispose() { renderer.dispose(); }
}
