package com.aramirezochoa.nyamnyam.screen.game.room;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

import java.util.HashMap;
import java.util.Map;

/** Loads a compiled room config and creates a mutable runtime instance. */
public class RoomLoader {

    private final Json json;

    public RoomLoader() {
        json = new Json();
        json.setIgnoreUnknownFields(true);
    }

    public RoomInstance load(String internalPath) {
        RoomConfig config = json.fromJson(RoomConfig.class, Gdx.files.internal(internalPath));
        if (!"nyam-room".equals(config.format) || config.version != 1) {
            throw new IllegalArgumentException("Unsupported room format in " + internalPath);
        }
        if (config.width <= 0 || config.height <= 0 || config.tileSize <= 0) {
            throw new IllegalArgumentException("Room dimensions and tile size must be positive: " + internalPath);
        }
        if (config.materials == null || config.cells == null || config.cells.length != config.height) {
            throw new IllegalArgumentException("Room cells or materials are invalid: " + internalPath);
        }

        Map<Character, RoomMaterial> materialCodes = new HashMap<Character, RoomMaterial>();
        for (Map.Entry<String, RoomMaterial> material : config.materials.entrySet()) {
            String code = material.getKey();
            if (code == null || code.length() != 1 || ".".equals(code) || material.getValue() == null) {
                throw new IllegalArgumentException("Invalid material code in " + internalPath);
            }
            materialCodes.put(code.charAt(0), material.getValue());
        }

        RoomMaterial[][] materials = new RoomMaterial[config.height][config.width];
        for (int y = 0; y < config.height; y++) {
            String row = config.cells[y];
            if (row == null || row.length() != config.width) {
                throw new IllegalArgumentException("Room row " + y + " does not match its width: " + internalPath);
            }
            for (int x = 0; x < config.width; x++) {
                char code = row.charAt(x);
                if (code == '.') {
                    continue;
                }
                RoomMaterial material = materialCodes.get(code);
                if (material == null) {
                    throw new IllegalArgumentException("Unknown material code at " + x + "," + y + " in " + internalPath);
                }
                materials[y][x] = material;
            }
        }

        return new RoomInstance(config.width, config.height, config.tileSize, materials);
    }

    public RoomInstance loadIfPresent(String internalPath) {
        return Gdx.files.internal(internalPath).exists() ? load(internalPath) : null;
    }
}
