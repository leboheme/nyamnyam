package com.aramirezochoa.nyamnyam.screen.game.debug;

import java.util.function.LongConsumer;
import java.util.function.Supplier;

/** Starts an empty modular area by seed for navigation checks. */
public final class AreaCommand implements DebugCommand {
    private final LongConsumer loader;
    private final Supplier<String> status;

    public AreaCommand(LongConsumer loader, Supplier<String> status) {
        this.loader = loader;
        this.status = status;
    }

    @Override public String name() { return "area"; }

    @Override public String description() { return "Show current area or load one by seed"; }

    @Override
    public String execute(String[] arguments) {
        if (arguments.length == 0) return status.get();
        if (arguments.length != 1) return "Usage: area [seed]";
        try {
            long seed = Long.parseLong(arguments[0]);
            loader.accept(seed);
            return "Loaded navigation area with seed " + seed;
        } catch (NumberFormatException exception) {
            return "Invalid area seed: " + arguments[0];
        } catch (IllegalStateException exception) {
            return "Area generation failed: " + exception.getMessage();
        }
    }
}
