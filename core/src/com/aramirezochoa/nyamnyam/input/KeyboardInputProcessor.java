package com.aramirezochoa.nyamnyam.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatar;

/**
 * Created by boheme on 13/01/15.
 */
public class KeyboardInputProcessor implements InputProcessor {

    private final MainAvatar mainAvatar;

    public KeyboardInputProcessor(MainAvatar mainAvatar) {
        this.mainAvatar = mainAvatar;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.LEFT:
                mainAvatar.left();
                break;
            case Input.Keys.RIGHT:
                mainAvatar.right();
                break;
            case Input.Keys.UP:
                mainAvatar.jump();
                break;
            case Input.Keys.SPACE:
                mainAvatar.fire();
                break;
        }
        Gdx.app.error("KeyDown", "");
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        Gdx.app.error("KeyUp", "");
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        Gdx.app.error("KeyTyped", "");
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
