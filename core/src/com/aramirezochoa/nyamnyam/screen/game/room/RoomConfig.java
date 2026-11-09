package com.aramirezochoa.nyamnyam.screen.game.room;

import java.util.Map;

/** Plain data object populated directly by LibGDX Json.fromJson. */
public class RoomConfig {
    public String format;
    public int version;
    public int width;
    public int height;
    public int tileSize;
    public Map<String, RoomMaterial> materials;
    public String[] cells;

    public RoomConfig() {
    }
}
