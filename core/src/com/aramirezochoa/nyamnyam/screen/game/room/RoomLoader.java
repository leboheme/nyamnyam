package com.aramirezochoa.nyamnyam.screen.game.room;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.meal.MealType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Loads a compiled room config and creates a mutable runtime instance.
 */
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

        RoomLayout layout = loadFirstLayout(config, internalPath);
        Array<RoomAttractor> attractors = new Array<RoomAttractor>();
        if (config.attractors != null) {
            for (RoomAttractorConfig attractor : config.attractors) {
                if (attractor == null || attractor.x < 0 || attractor.x > config.width
                        || attractor.y < 0 || attractor.y > config.height) {
                    throw new IllegalArgumentException("Invalid bubble attractor in " + internalPath);
                }
                attractors.add(new RoomAttractor((attractor.x + 1) * config.tileSize,
                        (attractor.y + 1) * config.tileSize, 4 * config.tileSize, 4 * config.tileSize));
            }
        }
        Array<RoomModule> footprint = loadFootprint(config, internalPath);
        Array<RoomConnector> connectors = loadConnectors(config, footprint, internalPath);
        return new RoomInstance(config.width, config.height, config.tileSize, materials, layout, attractors,
                footprint, connectors);
    }

    private Array<RoomModule> loadFootprint(RoomConfig config, String path) {
        Array<RoomModule> result = new Array<RoomModule>();
        if (config.footprint == null) {
            if (config.connectors != null) {
                throw new IllegalArgumentException("Connectors require a footprint: " + path);
            }
            result.add(new RoomModule(0, 0)); // Existing rooms predate modular authoring.
            return result;
        }
        Set<String> cells = new HashSet<String>();
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        for (RoomModuleConfig module : config.footprint) {
            if (module == null || module.x < 0 || module.y < 0
                    || !cells.add(module.x + "," + module.y)) {
                throw new IllegalArgumentException("Invalid or duplicate footprint cell: " + path);
            }
            minX = Math.min(minX, module.x);
            minY = Math.min(minY, module.y);
            result.add(new RoomModule(module.x, module.y));
        }
        if (minX != 0 || minY != 0) {
            throw new IllegalArgumentException("Footprint must start at 0,0: " + path);
        }
        Set<String> reached = new HashSet<String>();
        reached.add(result.first().getX() + "," + result.first().getY());
        boolean changed;
        do {
            changed = false;
            for (RoomModule module : result) {
                String key = module.getX() + "," + module.getY();
                if (!reached.contains(key) && (reached.contains((module.getX() - 1) + "," + module.getY())
                        || reached.contains((module.getX() + 1) + "," + module.getY())
                        || reached.contains(module.getX() + "," + (module.getY() - 1))
                        || reached.contains(module.getX() + "," + (module.getY() + 1)))) {
                    reached.add(key);
                    changed = true;
                }
            }
        } while (changed);
        if (reached.size() != cells.size()) {
            throw new IllegalArgumentException("Disconnected footprint: " + path);
        }
        return result;
    }

    private Array<RoomConnector> loadConnectors(RoomConfig config, Array<RoomModule> footprint, String path) {
        Array<RoomConnector> result = new Array<RoomConnector>();
        if (config.connectors == null) return result;
        Set<String> slots = new HashSet<String>();
        Set<String> cells = new HashSet<String>();
        for (RoomModule module : footprint) cells.add(module.getX() + "," + module.getY());
        for (RoomConnectorConfig connector : config.connectors) {
            if (connector == null || connector.slot == null || !connector.slot.matches("[WESN][01]")
                    || !slots.add(connector.slot)) {
                throw new IllegalArgumentException("Invalid or duplicate connector slot: " + path);
            }
            char side = connector.slot.charAt(0);
            int x = connector.moduleX, y = connector.moduleY;
            int dx = side == 'W' ? -1 : side == 'E' ? 1 : 0;
            int dy = side == 'S' ? -1 : side == 'N' ? 1 : 0;
            if (!cells.contains(x + "," + y) || cells.contains((x + dx) + "," + (y + dy))) {
                throw new IllegalArgumentException("Connector is not on an exterior module edge: " + path);
            }
            int index = 0;
            for (RoomModule module : footprint) {
                int mx = module.getX(), my = module.getY();
                if (!cells.contains((mx + dx) + "," + (my + dy))
                        && (side == 'W' || side == 'E' ? my < y : mx < x)) index++;
            }
            if (index != connector.slot.charAt(1) - '0') {
                throw new IllegalArgumentException("Connector slot index does not match footprint: " + path);
            }
            float limitX = config.width * config.tileSize;
            float limitY = config.height * config.tileSize;
            float expectedLength = (side == 'W' || side == 'E' ? 3 : 2) * config.tileSize;
            if (!Float.isFinite(connector.x) || !Float.isFinite(connector.y)
                    || !Float.isFinite(connector.length) || connector.length != expectedLength
                    || connector.x < 0 || connector.y < 0 || connector.x > limitX || connector.y > limitY
                    || (side == 'W' || side == 'E' ? connector.y + connector.length > limitY
                    : connector.x + connector.length > limitX)
                    || !Float.isFinite(connector.arrivalX) || !Float.isFinite(connector.arrivalY)
                    || connector.arrivalX <= 0 || connector.arrivalX >= limitX
                    || connector.arrivalY <= 0 || connector.arrivalY >= limitY) {
                throw new IllegalArgumentException("Invalid physical connector coordinates: " + path);
            }
            result.add(new RoomConnector(connector));
        }
        return result;
    }

    private RoomLayout loadFirstLayout(RoomConfig config, String internalPath) {
        RoomLayout layout = new RoomLayout(null);
        if (config.layoutDefinitions == null || config.layoutDefinitions.length == 0) {
            return layout;
        }
        RoomLayoutDefinition source = config.layoutDefinitions[0];
        if (source == null) {
            return layout;
        }
        layout = new RoomLayout(source.name);
        if (source.enemies != null) {
            for (EnemySpawnConfig enemy : source.enemies) {
                if (enemy == null || enemy.type == null || enemy.facing == null
                        || enemy.x < 0 || enemy.x >= config.width
                        || enemy.y < 0 || enemy.y >= config.height) {
                    throw new IllegalArgumentException("Invalid enemy spawn in " + internalPath);
                }
                layout.addEnemy(new RoomEnemySpawn(enemy.type, enemy.x, enemy.y, enemy.facing));
            }
        }
        if (source.meals != null) {
            for (MealSpawnConfig meal : source.meals) {
                if (meal == null || !isStaticMeal(meal.type)
                        || meal.x < 0 || meal.x >= config.width
                        || meal.y < 0 || meal.y >= config.height) {
                    throw new IllegalArgumentException("Invalid meal spawn in " + internalPath);
                }
                layout.addMeal(new RoomMealSpawn(meal.type, meal.x, meal.y, meal.expiring));
            }
        }
        return layout;
    }

    private boolean isStaticMeal(MealType type) {
        return type != null && !type.isAir() && !type.isCandy()
                && (type.isBoost() || type.isFruit() || type.isVerdure());
    }

    public RoomInstance loadIfPresent(String internalPath) {
        return Gdx.files.internal(internalPath).exists() ? load(internalPath) : null;
    }
}
