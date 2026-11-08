package com.aramirezochoa.nyamnyam.screen.game.debug;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DebugCommandRegistry {
    private final Map<String, DebugCommand> commands = new LinkedHashMap<String, DebugCommand>();

    public void register(DebugCommand command) {
        commands.put(command.name().toLowerCase(), command);
    }

    public Collection<DebugCommand> commands() {
        return commands.values();
    }

    public String execute(String input) {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return "";
        String[] parts = trimmed.split("\\s+");
        DebugCommand command = commands.get(parts[0].toLowerCase());
        if (command == null) return "Unknown command: " + parts[0];
        String[] arguments = new String[parts.length - 1];
        System.arraycopy(parts, 1, arguments, 0, arguments.length);
        return command.execute(arguments);
    }
}
