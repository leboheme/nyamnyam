package com.aramirezochoa.nyamnyam.screen.game.run;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

import java.util.HashSet;
import java.util.Set;

/** Loads a world catalog without retaining its config in the live run. */
final class WorldLoader {
    WorldConfig load(String name) {
        if (name == null || !name.matches("[A-Za-z0-9][A-Za-z0-9_-]*")) {
            throw new IllegalArgumentException("Invalid world name: " + name);
        }
        WorldConfig config = new Json().fromJson(WorldConfig.class,
                Gdx.files.internal("data/worlds/" + name + ".json"));
        if (config.rooms == null || config.rooms.length == 0 || config.theme == null) {
            throw new IllegalArgumentException("World requires a theme and a nonempty room catalog: " + name);
        }
        Set<String> names = new HashSet<String>();
        for (String room : config.rooms) {
            if (room == null || !room.matches("[A-Za-z0-9][A-Za-z0-9_-]*(/[A-Za-z0-9][A-Za-z0-9_-]*)*")
                    || !names.add(room)) {
                throw new IllegalArgumentException("Invalid or duplicate room in world " + name + ": " + room);
            }
        }
        return config;
    }
}
