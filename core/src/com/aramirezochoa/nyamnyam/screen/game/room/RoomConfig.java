package com.aramirezochoa.nyamnyam.screen.game.room;

import java.util.HashMap;

/** Plain data object populated directly by LibGDX Json.fromJson. */
public class RoomConfig {
    public String format;
    public int version;
    public int width;
    public int height;
    public int tileSize;
    public HashMap<String, RoomMaterial> materials;
    public String[] cells;
    public RoomLayoutDefinition[] layoutDefinitions;
    public RoomAttractorConfig[] attractors;
    public RoomModuleConfig[] footprint;
    public RoomConnectorConfig[] connectors;
    public String[] playableCells;

    public RoomConfig() {
    }
}
