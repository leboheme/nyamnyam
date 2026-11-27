package com.aramirezochoa.nyamnyam.screen.game.debug;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.run.RunRuntime;

import java.util.Locale;
import java.util.function.Supplier;

/** Read-only view of the active JSON runtime's base attack cadence. */
public final class StatsCommand implements DebugCommand {
    private final Supplier<RunRuntime> runtimeSupplier;

    public StatsCommand(Supplier<RunRuntime> runtimeSupplier) {
        this.runtimeSupplier = runtimeSupplier;
    }

    @Override public String name() { return "stats"; }
    @Override public String description() { return "Show base attack cadence"; }

    @Override
    public String execute(String[] arguments) {
        if (arguments.length != 0) return "Usage: stats";
        RunRuntime runtime = runtimeSupplier.get();
        if (runtime == null) return "stats requires a JSON room";

        float interval = Constant.MAIN_AVATAR_FIRE_TIME_LAPSE;
        return String.format(Locale.ROOT,
                "Cadence: %.2f attacks/s base (%.2fs interval)", 1f / interval, interval);
    }
}
