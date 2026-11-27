package com.aramirezochoa.nyamnyam.screen.game.debug;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/** Starts a seeded modular area from a world catalog. */
public final class AreaCommand implements DebugCommand {
    private final BiConsumer<Long, String> loader;
    private final Supplier<String> status;

    public AreaCommand(BiConsumer<Long, String> loader, Supplier<String> status) {
        this.loader = loader;
        this.status = status;
    }

    @Override public String name() { return "area"; }

    @Override public String description() { return "Show current area or load one by seed"; }

    @Override
    public String execute(String[] arguments) {
        if (arguments.length == 0) return status.get();
        if (arguments.length > 2) return "Usage: area [seed [world]]";
        try {
            long seed = Long.parseLong(arguments[0]);
            String world = arguments.length == 2 ? arguments[1] : "world_01";
            loader.accept(seed, world);
            return "Loaded " + world + " area with seed " + seed;
        } catch (NumberFormatException exception) {
            return "Invalid area seed: " + arguments[0];
        } catch (RuntimeException exception) {
            return "Area generation failed: " + exception.getMessage();
        }
    }
}
