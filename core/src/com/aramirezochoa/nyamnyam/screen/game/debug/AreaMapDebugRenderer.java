package com.aramirezochoa.nyamnyam.screen.game.debug;

import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomConnector;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomModule;
import com.aramirezochoa.nyamnyam.screen.game.run.FloorState;
import com.aramirezochoa.nyamnyam.screen.game.run.PlacedRoom;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

/** Temporary HUD map of the area's logical modules and chosen connections. */
public final class AreaMapDebugRenderer {
    private static final float[][] ROOM_COLORS = {
            {0.20f, 0.43f, 0.66f}, {0.49f, 0.27f, 0.58f}, {0.15f, 0.53f, 0.48f},
            {0.61f, 0.36f, 0.22f}, {0.37f, 0.42f, 0.65f}, {0.53f, 0.30f, 0.40f},
            {0.34f, 0.48f, 0.29f}
    };

    private final Viewport viewport;
    private final BitmapFont font;
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final GlyphLayout label = new GlyphLayout();

    public AreaMapDebugRenderer(MediaManager mediaManager, Viewport viewport) {
        this.viewport = viewport;
        font = mediaManager.get("data/game/font-14.fnt");
    }

    public void render(FloorState floor) {
        if (floor == null || floor.getRooms().size == 0) return;

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (PlacedRoom room : floor.getRooms()) {
            for (RoomModule module : room.getRoom().getFootprint()) {
                int x = room.getModuleX() + module.getX();
                int y = room.getModuleY() + module.getY();
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }

        float cell = Math.min(18f, Math.min(180f / (maxX - minX + 1), 150f / (maxY - minY + 1)));
        float mapWidth = (maxX - minX + 1) * cell;
        float mapHeight = (maxY - minY + 1) * cell;
        float panelWidth = Math.max(170f, mapWidth + 12f);
        float panelHeight = mapHeight + 42f;
        float panelX = viewport.getWorldWidth() - panelWidth - 8f;
        float panelY = viewport.getWorldHeight() - panelHeight - 8f;
        float mapX = panelX + (panelWidth - mapWidth) / 2f;
        float mapY = panelY + 6f;

        viewport.apply();
        shapes.setProjectionMatrix(viewport.getCamera().combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0f, 0f, 0f, 0.78f);
        shapes.rect(panelX, panelY, panelWidth, panelHeight);
        for (int index = 0; index < floor.getRooms().size; index++) {
            PlacedRoom room = floor.getRooms().get(index);
            if (index == floor.getCurrentRoomIndex()) shapes.setColor(0.85f, 0.67f, 0.16f, 1f);
            else {
                float[] color = ROOM_COLORS[index % ROOM_COLORS.length];
                shapes.setColor(color[0], color[1], color[2], 1f);
            }
            for (RoomModule module : room.getRoom().getFootprint()) {
                float x = mapX + (room.getModuleX() + module.getX() - minX) * cell;
                float y = mapY + (room.getModuleY() + module.getY() - minY) * cell;
                shapes.rect(x, y, cell, cell);
            }
        }
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(Color.WHITE);
        for (int index = 0; index < floor.getRooms().size; index++) {
            PlacedRoom room = floor.getRooms().get(index);
            for (RoomModule module : room.getRoom().getFootprint()) {
                int moduleX = room.getModuleX() + module.getX();
                int moduleY = room.getModuleY() + module.getY();
                float x = mapX + (moduleX - minX) * cell;
                float y = mapY + (moduleY - minY) * cell;
                if (ownerAt(floor, moduleX - 1, moduleY) != index) shapes.line(x, y, x, y + cell);
                if (ownerAt(floor, moduleX + 1, moduleY) != index) shapes.line(x + cell, y, x + cell, y + cell);
                if (ownerAt(floor, moduleX, moduleY - 1) != index) shapes.line(x, y, x + cell, y);
                if (ownerAt(floor, moduleX, moduleY + 1) != index) shapes.line(x, y + cell, x + cell, y + cell);
            }
        }
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (PlacedRoom room : floor.getRooms()) {
            for (RoomConnector connector : room.getRoom().getConnectors()) {
                boolean linked = room.getLink(connector.getSlot()) != null;
                shapes.setColor(linked ? 0.30f : 0.58f, linked ? 1f : 0.58f,
                        linked ? 0.40f : 0.58f, 1f);
                float x = mapX + (room.getModuleX() + connector.getModuleX() - minX) * cell;
                float y = mapY + (room.getModuleY() + connector.getModuleY() - minY) * cell;
                float marker = Math.max(2f, cell / 4f);
                char side = connector.getSlot().charAt(0);
                if (side == 'W' || side == 'E') {
                    float edge = x + (side == 'E' ? cell : 0);
                    shapes.rect(edge - marker / 2f, y + (cell - marker) / 2f, marker, marker);
                } else {
                    float edge = y + (side == 'N' ? cell : 0);
                    shapes.rect(x + (cell - marker) / 2f, edge - marker / 2f, marker, marker);
                }
            }
        }
        shapes.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.setProjectionMatrix(viewport.getCamera().combined);
        Color previousColor = font.getColor().cpy();
        float previousScaleX = font.getData().scaleX;
        float previousScaleY = font.getData().scaleY;
        font.setColor(Color.WHITE);
        font.getData().setScale(0.75f);
        batch.begin();
        font.draw(batch, "seed " + floor.getSeed(), panelX + 6f, panelY + panelHeight - 5f);
        font.draw(batch, "room " + (floor.getCurrentRoomIndex() + 1) + "/" + floor.getRooms().size,
                panelX + 6f, panelY + panelHeight - 20f);
        font.getData().setScale(Math.min(0.8f, cell / 18f));
        for (int index = 0; index < floor.getRooms().size; index++) {
            PlacedRoom room = floor.getRooms().get(index);
            label.setText(font, String.valueOf(index + 1));
            for (RoomModule module : room.getRoom().getFootprint()) {
                float x = mapX + (room.getModuleX() + module.getX() - minX) * cell;
                float y = mapY + (room.getModuleY() + module.getY() - minY) * cell;
                font.draw(batch, label, x + (cell - label.width) / 2f, y + (cell + label.height) / 2f);
            }
        }
        batch.end();
        font.getData().setScale(previousScaleX, previousScaleY);
        font.setColor(previousColor);
    }

    private int ownerAt(FloorState floor, int x, int y) {
        for (int index = 0; index < floor.getRooms().size; index++) {
            PlacedRoom room = floor.getRooms().get(index);
            for (int moduleIndex = 0; moduleIndex < room.getRoom().getFootprint().size; moduleIndex++) {
                RoomModule module = room.getRoom().getFootprint().get(moduleIndex);
                if (room.getModuleX() + module.getX() == x && room.getModuleY() + module.getY() == y) return index;
            }
        }
        return -1;
    }

    public void dispose() {
        shapes.dispose();
        batch.dispose();
    }
}
