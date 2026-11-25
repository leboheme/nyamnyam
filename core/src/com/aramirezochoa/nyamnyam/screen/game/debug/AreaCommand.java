package com.aramirezochoa.nyamnyam.screen.game.debug;

import java.util.function.LongConsumer;

/** Starts an empty modular area by seed for navigation checks. */
public final class AreaCommand implements DebugCommand {
    private final LongConsumer loader;

    public AreaCommand(LongConsumer loader) { this.loader = loader; }

    @Override public String name() { return "area"; }

    @Override public String description() { return "Load a generated navigation area by seed"; }

    @Override
    public String execute(String[] arguments) {
        if (arguments.length != 1) return "Usage: area <seed>";
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
