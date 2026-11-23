package com.aramirezochoa.nyamnyam.screen.game.debug;

/** Explicitly restart the prototype run, unlike stage which only reloads room content. */
public final class RestartCommand implements DebugCommand {
    private final Runnable restart;

    public RestartCommand(Runnable restart) { this.restart = restart; }
    @Override public String name() { return "restart"; }
    @Override public String description() { return "Restart the current stage with initial run resources"; }
    @Override public String execute(String[] arguments) {
        if (arguments.length != 0) return "Usage: restart";
        restart.run();
        return "Restarted run";
    }
}
