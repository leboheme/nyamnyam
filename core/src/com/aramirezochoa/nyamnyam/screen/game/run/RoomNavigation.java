package com.aramirezochoa.nyamnyam.screen.game.run;

import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatar;
import com.aramirezochoa.nyamnyam.screen.game.room.RoomConnector;

/** Detects only active portal crossings in the current generated room. */
final class RoomNavigation {
    private static final float EDGE_TOLERANCE = 1.5f;

    private final PlacedRoom placement;
    private final float tileSize;
    private String entrySlot;

    RoomNavigation(PlacedRoom placement, String entrySlot) {
        this.placement = placement;
        this.tileSize = placement.getRoom().getTileSize();
        this.entrySlot = entrySlot;
    }

    RoomLink findTransition(MainAvatar player, float intendedX) {
        if (player == null) return null;
        if (entrySlot != null) {
            RoomConnector entry = connector(entrySlot);
            if (entry != null && touchesEntry(player, entry)) return null;
            entrySlot = null;
        }
        for (RoomConnector door : placement.getRoom().getConnectors()) {
            RoomLink link = placement.getLink(door.getSlot());
            if (link == null) continue;
            char side = door.getSlot().charAt(0);
            if ((side == 'W' && intendedX < 0 && atLateralEdge(player, door, true))
                    || (side == 'E' && intendedX > 0 && atLateralEdge(player, door, false))
                    || ((side == 'N' || side == 'S') && touchesFrontPortal(player, door))) {
                return link;
            }
        }
        return null;
    }

    private RoomConnector connector(String slot) {
        for (RoomConnector door : placement.getRoom().getConnectors()) {
            if (door.getSlot().equals(slot)) return door;
        }
        return null;
    }

    private boolean touchesEntry(MainAvatar player, RoomConnector door) {
        char side = door.getSlot().charAt(0);
        float lineX = door.getX() + tileSize;
        float centerY = player.position.y + player.dimension.y / 2f;
        float bottom = door.getY() + tileSize;
        if (centerY < bottom || centerY > bottom + door.getHeight()) return false;
        if (side == 'W') return Math.abs(player.position.x - lineX) < player.dimension.x / 2f;
        if (side == 'E') return Math.abs(player.position.x + player.dimension.x - lineX) < player.dimension.x / 2f;
        return touchesFrontPortal(player, door);
    }

    private boolean atLateralEdge(MainAvatar player, RoomConnector door, boolean west) {
        float lineX = door.getX() + tileSize;
        float edge = west ? player.position.x : player.position.x + player.dimension.x;
        float centerY = player.position.y + player.dimension.y / 2f;
        float bottom = door.getY() + tileSize;
        return Math.abs(edge - lineX) <= EDGE_TOLERANCE
                && centerY >= bottom && centerY <= bottom + door.getHeight();
    }

    private boolean touchesFrontPortal(MainAvatar player, RoomConnector door) {
        float centerX = door.getX() + tileSize + door.getWidth() / 2f;
        float centerY = player.position.y + player.dimension.y / 2f;
        float bottom = door.getY() + tileSize;
        return player.position.x <= centerX && player.position.x + player.dimension.x >= centerX
                && centerY >= bottom && centerY <= bottom + door.getHeight();
    }
}
