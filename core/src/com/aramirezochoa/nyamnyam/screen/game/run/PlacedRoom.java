package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** One room's template, logical map position, live state, and chosen exits. */
public class PlacedRoom {
    private final String templatePath;
    private final RoomInstance room;
    private final int moduleX;
    private final int moduleY;
    private final Map<String, RoomLink> links = new LinkedHashMap<String, RoomLink>();

    PlacedRoom(String templatePath, RoomInstance room, int moduleX, int moduleY) {
        this.templatePath = templatePath;
        this.room = room;
        this.moduleX = moduleX;
        this.moduleY = moduleY;
    }

    public String getTemplatePath() { return templatePath; }
    public RoomInstance getRoom() { return room; }
    public int getModuleX() { return moduleX; }
    public int getModuleY() { return moduleY; }
    public Map<String, RoomLink> getLinks() { return Collections.unmodifiableMap(links); }
    public RoomLink getLink(String slot) { return links.get(slot); }

    void link(String slot, RoomLink destination) { links.put(slot, destination); }
}
