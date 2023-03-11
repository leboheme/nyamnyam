package com.aramirezochoa.nyamnyam.input;

import com.aramirezochoa.nyamnyam.screen.menu.MenuAction;

/**
 * Created by boheme on 13/01/15.
 */
public enum InputManager {
    INSTANCE;

    private volatile MenuAction menuAction = MenuAction.NONE;

    public synchronized void notifyMenuAction(MenuAction menuAction) {
        this.menuAction = menuAction;
    }

    public synchronized MenuAction getMenuAction() {
        MenuAction toReturn = menuAction;
        menuAction = MenuAction.NONE;
        return toReturn;
    }

}