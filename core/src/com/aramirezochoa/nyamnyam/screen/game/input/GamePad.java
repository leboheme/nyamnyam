package com.aramirezochoa.nyamnyam.screen.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.GameScreen;
import com.aramirezochoa.nyamnyam.screen.game.core.GameWrapper;

/**
 * Created by boheme on 21/01/15.
 */
public class GamePad extends InputAdapter implements GameScreen.GameScreenComponent {

    //    private final GameWrapper gameWrapper;
    private Button leftButton, rightButton, fireButton, jumpButton;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Vector3 coords = new Vector3();

    public GamePad(MediaManager mediaManager, final GameWrapper gameWrapper) {
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT);
        this.camera.setToOrtho(false, Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT);
        this.camera.update();
        this.batch.setProjectionMatrix(camera.combined);

        // Buttons
        TextureAtlas atlas = mediaManager.get("data/game/gui.atlas");

        // Left button
        leftButton = new Button(atlas.findRegion("BUTTON", 0), atlas.findRegion("BUTTON", 1), 8, 8, camera, Constant.GAME_BUTTON_PAD_WIDTH, Constant.GAME_BUTTON_PAD_HEIGHT) {
            @Override
            public void execute() {
                gameWrapper.mainAvatar.left();
            }
        };
        // Right button
        rightButton = new Button(atlas.findRegion("BUTTON", 0), atlas.findRegion("BUTTON", 1), 96, 8, camera, Constant.GAME_BUTTON_PAD_WIDTH, Constant.GAME_BUTTON_PAD_HEIGHT) {
            @Override
            public void execute() {
                gameWrapper.mainAvatar.right();
            }
        };
        // Fire button
        fireButton = new Button(atlas.findRegion("BUTTON", 0), atlas.findRegion("BUTTON", 1), 624, 8, camera, Constant.GAME_BUTTON_PAD_WIDTH, Constant.GAME_BUTTON_PAD_HEIGHT) {
            @Override
            public void execute() {
                gameWrapper.mainAvatar.fire();
            }
        };
        // Jump button
        jumpButton = new Button(atlas.findRegion("BUTTON", 0), atlas.findRegion("BUTTON", 1), 712, 8, camera, Constant.GAME_BUTTON_PAD_WIDTH, Constant.GAME_BUTTON_PAD_HEIGHT) {
            @Override
            public void execute() {
                gameWrapper.mainAvatar.jump();
            }
        };
    }

    public void update(float deltaTime) {
        leftButton.update(deltaTime);
        rightButton.update(deltaTime);
        fireButton.update(deltaTime);
        jumpButton.update(deltaTime);
    }

    @Override
    public void render(float deltaTime) {
        leftButton.render(batch);
        rightButton.render(batch);
        fireButton.render(batch);
        jumpButton.render(batch);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        checkCoordinates(screenX, screenY, pointer);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        checkCoordinates(screenX, screenY, pointer);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (leftButton.pointer == pointer) leftButton.setPressed(false);
        if (rightButton.pointer == pointer) rightButton.setPressed(false);
        if (fireButton.pointer == pointer) fireButton.setPressed(false);
        if (jumpButton.pointer == pointer) jumpButton.setPressed(false);
        return false;
    }

    private void checkCoordinates(int screenX, int screenY, int pointer) {
        coords.x = screenX;
        coords.y = screenY;
        camera.unproject(coords);
        checkButton(leftButton, coords.x, coords.y, pointer);
        checkButton(rightButton, coords.x, coords.y, pointer);
        checkButton(fireButton, coords.x, coords.y, pointer);
        checkButton(jumpButton, coords.x, coords.y, pointer);
    }

    private void checkButton(Button button, float screenX, float screenY, int pointer) {
        if (button.contains(screenX, screenY)) {
            button.setPressed(true);
            button.setPointer(pointer);
        }
    }

    private static abstract class Button {

        private final TextureRegion textureButtonUp, textureButtonDown;
        private TextureRegion currentFrame;
        private final float x, y;
        private boolean pressed;
        private final Rectangle box = new Rectangle();
        private int pointer;
        private Vector3 coords = new Vector3();
        private OrthographicCamera camera;

        public Button(TextureAtlas.AtlasRegion buttonUp, TextureAtlas.AtlasRegion buttonDown, float x, float y, OrthographicCamera camera, float width, float height) {
            this.textureButtonUp = buttonUp;
            this.textureButtonDown = buttonDown;
            this.x = x;
            this.y = y;
            setPressed(false);
            if (width > textureButtonUp.getRegionWidth()) {
                this.box.set(x - ((width - textureButtonUp.getRegionWidth()) / 2), y, width, height);
            } else {
                this.box.set(x, y, width, height);
            }
            this.camera = camera;
        }

        public abstract void execute();

        public void update(float deltaTime) {
            if (pressed) {
                execute();
            }
            coords.x = Gdx.input.getX(pointer);
            coords.y = Gdx.input.getY(pointer);
            camera.unproject(coords);
            if (!Gdx.input.isTouched(pointer) || !contains(coords.x, coords.y)) {
                setPressed(false);
            }
        }

        public void render(Batch batch) {
            batch.begin();
            batch.draw(currentFrame, x, y);
            batch.end();
            if (GameWrapper.debug) {
                drawDebug();
            }
        }

        private void drawDebug() {
            GameWrapper.debugRenderer.begin(ShapeRenderer.ShapeType.Line);
            GameWrapper.debugRenderer.setColor(Color.BLUE);
            GameWrapper.debugRenderer.rect(box.x, box.y + Constant.SCREEN_GAME_Y_OFFSET, box.width, box.height);
            GameWrapper.debugRenderer.end();
        }

        public boolean contains(float screenX, float screenY) {
            return box.contains(screenX, screenY);
        }

        public void setPressed(boolean pressed) {
            this.pressed = pressed;
            currentFrame = pressed ? textureButtonDown : textureButtonUp;
        }

        public void setPointer(int pointer) {
            this.pointer = pointer;
        }
    }

}
