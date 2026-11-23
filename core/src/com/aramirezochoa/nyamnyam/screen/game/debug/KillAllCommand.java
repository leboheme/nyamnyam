package com.aramirezochoa.nyamnyam.screen.game.debug;

import java.util.function.IntSupplier;

/** Debug shortcut through the normal JSON defeat/reward path. */
public final class KillAllCommand implements DebugCommand {
    private final IntSupplier defeatAll;

    public KillAllCommand(IntSupplier defeatAll) {
        this.defeatAll = defeatAll;
    }

    @Override public String name() { return "killall"; }
    @Override public String description() { return "Defeat all free and captured enemies in the JSON room, with drops"; }

    @Override public String execute(String[] arguments) {
        if (arguments.length != 0) return "Usage: killall";
        int count = defeatAll.getAsInt();
        return count < 0 ? "killall requires a JSON room" : "Defeated " + count + " enemies";
    }
}
