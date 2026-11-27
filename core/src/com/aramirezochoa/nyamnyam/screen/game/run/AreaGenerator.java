package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.room.RoomAppearance;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomConnector;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomInstance;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomLoader;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomMaterial;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/** Bounded, seeded growth from an authored world catalog. */
final class AreaGenerator {
    private static final int MAX_ATTEMPTS = 64;
    private static final int ROOM_COUNT = 7;

    private final RoomLoader loader;
    private final RoomAppearance theme;
    private final List<Template> templates = new ArrayList<Template>();

    AreaGenerator(RoomLoader loader, WorldConfig world) {
        this.loader = loader;
        this.theme = world.theme;
        for (String name : world.rooms) {
            String path = "data/rooms/" + name + ".json";
            RoomInstance room = loader.load(path);
            if (room.getConnectors().size == 0) {
                throw new IllegalArgumentException("World room has no connectors: " + path);
            }
            templates.add(new Template(path, room));
        }
    }

    FloorState generate(long seed) {
        Random random = new Random(seed);
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            FloorState floor = new FloorState(seed);
            addRoom(floor, templates.get(0), 0, 0);

            List<Template> remaining = new ArrayList<Template>(templates.subList(1, templates.size()));
            Collections.shuffle(remaining, random);
            remaining = remaining.subList(0, Math.min(ROOM_COUNT - 1, remaining.size()));

            boolean placedAll = true;
            for (Template template : remaining) {
                List<Candidate> choices = candidates(floor, template);
                if (choices.isEmpty()) {
                    placedAll = false;
                    break;
                }
                Candidate choice = choices.get(random.nextInt(choices.size()));
                int index = addRoom(floor, template, choice.x, choice.y);
                floor.connect(choice.parentIndex, choice.parentSlot, index, choice.childSlot);
            }
            if (placedAll) connectAdjacentRooms(floor);
            if (placedAll && hasBranchAndDeadEnd(floor)) return floor;
        }
        throw new IllegalStateException("Could not generate a connected modular area for seed " + seed
                + " after " + MAX_ATTEMPTS + " attempts with the current template catalog");
    }

    private int addRoom(FloorState floor, Template template, int x, int y) {
        RoomInstance room = loader.load(template.path);
        room.setAppearance(RoomMaterial.FLOOR, theme);
        return floor.addRoom(template.path, room, x, y);
    }

    private List<Candidate> candidates(FloorState floor, Template template) {
        List<Candidate> result = new ArrayList<Candidate>();
        Set<String> occupied = occupiedModules(floor);
        for (int index = 0; index < floor.getRooms().size; index++) {
            PlacedRoom parent = floor.getRooms().get(index);
            for (RoomConnector exit : parent.getRoom().getConnectors()) {
                if (parent.getLink(exit.getSlot()) != null) continue;
                for (RoomConnector entrance : template.room.getConnectors()) {
                    if (entrance.getSlot().charAt(0) != opposite(exit.getSlot().charAt(0))) continue;
                    int x = parent.getModuleX() + exit.getModuleX()
                            + stepX(exit.getSlot().charAt(0)) - entrance.getModuleX();
                    int y = parent.getModuleY() + exit.getModuleY()
                            + stepY(exit.getSlot().charAt(0)) - entrance.getModuleY();
                    if (fits(template.room, x, y, occupied)) {
                        result.add(new Candidate(index, exit.getSlot(), entrance.getSlot(), x, y));
                    }
                }
            }
        }
        return result;
    }

    private Set<String> occupiedModules(FloorState floor) {
        Set<String> occupied = new HashSet<String>();
        for (PlacedRoom placed : floor.getRooms()) {
            for (RoomModule module : placed.getRoom().getFootprint()) {
                occupied.add(key(placed.getModuleX() + module.getX(), placed.getModuleY() + module.getY()));
            }
        }
        return occupied;
    }

    private boolean fits(RoomInstance room, int x, int y, Set<String> occupied) {
        for (RoomModule module : room.getFootprint()) {
            if (occupied.contains(key(x + module.getX(), y + module.getY()))) return false;
        }
        return true;
    }

    /** Complete every free pair of facing connectors after placement, including loops. */
    private void connectAdjacentRooms(FloorState floor) {
        for (int firstIndex = 0; firstIndex < floor.getRooms().size; firstIndex++) {
            PlacedRoom first = floor.getRooms().get(firstIndex);
            for (RoomConnector exit : first.getRoom().getConnectors()) {
                if (first.getLink(exit.getSlot()) != null) continue;
                char side = exit.getSlot().charAt(0);
                int neighborX = first.getModuleX() + exit.getModuleX() + stepX(side);
                int neighborY = first.getModuleY() + exit.getModuleY() + stepY(side);
                for (int secondIndex = firstIndex + 1; secondIndex < floor.getRooms().size; secondIndex++) {
                    if (first.getLink(exit.getSlot()) != null) break;
                    PlacedRoom second = floor.getRooms().get(secondIndex);
                    for (RoomConnector entrance : second.getRoom().getConnectors()) {
                        if (second.getLink(entrance.getSlot()) == null
                                && entrance.getSlot().charAt(0) == opposite(side)
                                && second.getModuleX() + entrance.getModuleX() == neighborX
                                && second.getModuleY() + entrance.getModuleY() == neighborY) {
                            floor.connect(firstIndex, exit.getSlot(), secondIndex, entrance.getSlot());
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean hasBranchAndDeadEnd(FloorState floor) {
        boolean branch = false, deadEnd = false, horizontal = false, vertical = false;
        for (PlacedRoom room : floor.getRooms()) {
            branch |= room.getLinks().size() >= 3;
            deadEnd |= room.getLinks().size() == 1;
            for (String slot : room.getLinks().keySet()) {
                horizontal |= slot.startsWith("W") || slot.startsWith("E");
                vertical |= slot.startsWith("N") || slot.startsWith("S");
            }
        }
        return branch && deadEnd && horizontal && vertical;
    }

    private String key(int x, int y) { return x + "," + y; }
    private char opposite(char side) {
        return side == 'W' ? 'E' : side == 'E' ? 'W' : side == 'N' ? 'S' : 'N';
    }
    private int stepX(char side) { return side == 'W' ? -1 : side == 'E' ? 1 : 0; }
    private int stepY(char side) { return side == 'S' ? -1 : side == 'N' ? 1 : 0; }

    private static final class Template {
        final String path;
        final RoomInstance room;

        Template(String path, RoomInstance room) {
            this.path = path;
            this.room = room;
        }
    }

    private static final class Candidate {
        final int parentIndex;
        final String parentSlot;
        final String childSlot;
        final int x;
        final int y;

        Candidate(int parentIndex, String parentSlot, String childSlot, int x, int y) {
            this.parentIndex = parentIndex;
            this.parentSlot = parentSlot;
            this.childSlot = childSlot;
            this.x = x;
            this.y = y;
        }
    }
}
