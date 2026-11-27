package com.aramirezochoa.nyamnyam.screen.game.debug;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.game.run.RunStats;

import java.util.Locale;
import java.util.function.Supplier;

import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.DEBUG_POWER_BASE;
import static com.aramirezochoa.nyamnyam.screen.game.run.PrototypeBalance.SALIVATION_REGENERATION_PER_SECOND;

/** Read-only view of the active JSON run's base stats and modifiers. */
public final class StatsCommand implements DebugCommand {
    private final Supplier<RunStats> statsSupplier;

    public StatsCommand(Supplier<RunStats> statsSupplier) {
        this.statsSupplier = statsSupplier;
    }

    @Override public String name() { return "stats"; }
    @Override public String description() { return "Show base, modifiers and effective JSON run stats"; }

    @Override
    public String execute(String[] arguments) {
        if (arguments.length != 0) return "Usage: stats";
        RunStats stats = statsSupplier.get();
        if (stats == null) return "stats requires a JSON room";
        float baseCadence = 1f / Constant.MAIN_AVATAR_FIRE_TIME_LAPSE;
        return String.format(Locale.US,
                "Power (debug only; no combat effect): %.2f base, %+.0f%%, %.2f effective\n"
                        + "Cadence: %.2f base attacks/s, %+.0f%%, %.2f attacks/s (%.2f s interval)\n"
                        + "Salivation: %.2f base/s, %+.0f%%, %.2f/s",
                DEBUG_POWER_BASE, stats.getModifier(RunStats.Stat.POWER) * 100f, stats.getPower(),
                baseCadence, stats.getModifier(RunStats.Stat.CADENCE) * 100f,
                stats.getCadence(), stats.getCadence() <= 0f ? Float.MAX_VALUE : 1f / stats.getCadence(),
                SALIVATION_REGENERATION_PER_SECOND,
                stats.getModifier(RunStats.Stat.SALIVATION) * 100f, stats.getSalivation());
    }
}
