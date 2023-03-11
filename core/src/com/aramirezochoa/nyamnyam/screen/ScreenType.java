package com.aramirezochoa.nyamnyam.screen;

import com.aramirezochoa.nyamnyam.screen.end.EndScreen;
import com.aramirezochoa.nyamnyam.screen.game.GameScreen;
import com.aramirezochoa.nyamnyam.screen.help.HelpScreen;
import com.aramirezochoa.nyamnyam.screen.intro.IntroScreen;
import com.aramirezochoa.nyamnyam.screen.menu.MenuScreen;
import com.aramirezochoa.nyamnyam.screen.store.StoreScreen;

/**
 * Created by boheme on 12/01/15.
 */
public enum ScreenType {
    INTRO(new IntroScreen()),
    END(new EndScreen()),
    MENU(new MenuScreen()),
    GAME(new GameScreen()),
    HELP(new HelpScreen()),
    EXIT(null);

    private final AbstractScreen screen;

    ScreenType(AbstractScreen screen) {
        this.screen = screen;
    }

    public AbstractScreen getScreen() {
        return screen;
    }
}
