package com.aramirezochoa.nyamnyam.screen.game.debug;

public interface DebugCommand {
    String name();

    String description();

    String execute(String[] arguments);
}
