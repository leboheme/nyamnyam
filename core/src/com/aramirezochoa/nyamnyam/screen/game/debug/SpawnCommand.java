package com.aramirezochoa.nyamnyam.screen.game.debug;

import com.aramirezochoa.nyamnyam.screen.game.run.PickupType;
import java.util.function.IntUnaryOperator;

/** Numeric IDs remain usable even when display names or art change. */
public final class SpawnCommand implements DebugCommand {
    private final IntUnaryOperator spawn;

    public SpawnCommand(IntUnaryOperator spawn) { this.spawn = spawn; }
    @Override public String name() { return "spawn"; }
    @Override public String description() { return "Spawn a pickup by ID; spawn list shows IDs"; }

    @Override public String execute(String[] arguments) {
        if (arguments.length == 1 && arguments[0].equalsIgnoreCase("list")) {
            StringBuilder result = new StringBuilder();
            for (PickupType type : PickupType.values()) {
                if (result.length() > 0) result.append("\n");
                result.append(type.id).append(" - ").append(type.name());
            }
            result.append("\nIDs 5–9 are reserved");
            return result.toString();
        }
        if (arguments.length != 1) return "Usage: spawn <id> | spawn list";
        int id;
        try { id = Integer.parseInt(arguments[0]); }
        catch (NumberFormatException exception) { return "Invalid pickup ID; use spawn list"; }
        if (id >= 5 && id <= 9) return "Pickup ID " + id + " is reserved";
        if (PickupType.fromId(id) == null) return "Unknown pickup ID; use spawn list";
        return spawn.applyAsInt(id) < 0 ? "spawn requires an active JSON run" : "Spawned pickup " + id;
    }
}
