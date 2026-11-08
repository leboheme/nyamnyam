package com.aramirezochoa.nyamnyam.screen.game.debug;

import com.aramirezochoa.nyamnyam.Constant;

import java.util.function.IntConsumer;

public final class StageCommand implements DebugCommand {
    private final IntConsumer stageLoader;

    public StageCommand(IntConsumer stageLoader) {
        this.stageLoader = stageLoader;
    }

    @Override
    public String name() {
        return "stage";
    }

    @Override
    public String description() {
        return "Load a stage by number (1-" + Constant.GAME_LEVELS + ")";
    }

    @Override
    public String execute(String[] arguments) {
        if (arguments.length != 1) {
            return "Usage: stage <number> (1-" + Constant.GAME_LEVELS + ")";
        }

        int stageNumber;
        try {
            stageNumber = Integer.parseInt(arguments[0]);
        } catch (NumberFormatException exception) {
            return "Invalid stage number: " + arguments[0];
        }

        if (stageNumber < 1 || stageNumber > Constant.GAME_LEVELS) {
            return "Stage must be between 1 and " + Constant.GAME_LEVELS;
        }

        stageLoader.accept(stageNumber);
        return "Loaded stage " + stageNumber;
    }
}
