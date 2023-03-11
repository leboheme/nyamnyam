package com.aramirezochoa.nyamnyam.screen.game.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.activity.ActivityManager;
import com.aramirezochoa.nyamnyam.i18n.LanguageManager;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.game.GameScreen;
import com.aramirezochoa.nyamnyam.screen.game.GameState;
import com.aramirezochoa.nyamnyam.screen.game.status.GameStatusListener;
import com.aramirezochoa.nyamnyam.screen.menu.MenuAction;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by boheme on 13/01/15.
 */
public class GameUserInterface implements GameScreen.GameScreenComponent, GameStatusListener {

    private int level, lives, score;
    private Stage stage;
    private Table inGameTable, pauseTable, winTable, loseTable;
    private Window confirmWindow;

    private final TextureRegion bar;
    private BitmapFont barFont;

    private BitmapFont messageFont, bigMessageFont;
    private SnapshotArray<VolatileMessage> messages = new SnapshotArray<VolatileMessage>();
    private Pool<VolatileMessage> messagePool = new Pool<VolatileMessage>() {
        @Override
        protected VolatileMessage newObject() {
            return new VolatileMessage();
        }
    };
    private String fourthMessage = "";

    public GameUserInterface(MediaManager mediaManager) {
        this.stage = new Stage(new StretchViewport(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT));
        bar = ((TextureAtlas) mediaManager.get("data/game/gui.atlas")).findRegion("BAR");
        loadFonts((FreeTypeFontGenerator) mediaManager.get("data/game/font.ttf"));
        loadButtons((TextureAtlas) mediaManager.get("data/game/gui.atlas"));
    }

    public void init(int level, int lives, int score) {
        this.level = level;
        this.lives = lives;
        this.score = score;

        ActivityManager.INSTANCE.showBannerAd(false);

        this.fourthMessage = "";
    }

    private void loadFonts(FreeTypeFontGenerator freeTypeFontGenerator) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.characters = Constant.NYAM_CHARS;
        barFont = freeTypeFontGenerator.generateFont(parameter);
        barFont.setColor(Color.valueOf("13004a"));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 14;
        parameter.characters = Constant.NYAM_CHARS;
        messageFont = freeTypeFontGenerator.generateFont(parameter);
        messageFont.setColor(Color.valueOf("e66e04"));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 52;
        parameter.characters = Constant.NYAM_CHARS;
        bigMessageFont = freeTypeFontGenerator.generateFont(parameter);
        bigMessageFont.setColor(Color.valueOf("d5de25"));
    }

    private void loadButtons(TextureAtlas textureAtlas) {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = bigMessageFont;
        labelStyle.fontColor = Color.valueOf("f2ff24");
        Label label;

        // IN GAME
        inGameTable = new Table();
        inGameTable.setFillParent(true);
        inGameTable.top().right();
        Button.ButtonStyle buttonStyle = MediaManager.createButtonStyle("BUTTON_PAUSE", textureAtlas);
        Button button = MediaManager.createButton(buttonStyle, MenuAction.PAUSE);
        inGameTable.add(button).padTop(8f).padRight(8f);
        stage.addActor(inGameTable);

        // PAUSE
        pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseTable.center();
        pauseTable.setBackground(new TextureRegionDrawable(textureAtlas.findRegion("TABLE_BG")));

        label = new Label(LanguageManager.INSTANCE.getString("pause"), labelStyle);
        pauseTable.add(label).colspan(3).padBottom(20f).row();

        buttonStyle = MediaManager.createButtonStyle("BUTTON_EXIT", textureAtlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.EXIT);
        pauseTable.add(button);

        CheckBox.CheckBoxStyle checkBoxStyle = MediaManager.createCheckBoxStyle("BUTTON_SOUND", textureAtlas, messageFont);
        CheckBox checkBox = MediaManager.createCheckBox(checkBoxStyle);
        pauseTable.add(checkBox).padLeft(50f);

        buttonStyle = MediaManager.createButtonStyle("BUTTON_PLAY", textureAtlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.CONTINUE);
        pauseTable.add(button).padLeft(50f);

        pauseTable.setVisible(false);
        stage.addActor(pauseTable);

        // WIN TABLE
        winTable = new Table();
        winTable.setFillParent(true);
        winTable.center();
        winTable.setBackground(new TextureRegionDrawable(textureAtlas.findRegion("TABLE_BG")));
        label = new Label(LanguageManager.INSTANCE.getString("levelCompleted"), labelStyle);
        winTable.add(label).colspan(2).padBottom(20f).row();
        buttonStyle = MediaManager.createButtonStyle("BUTTON_EXIT", textureAtlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.EXIT);
        winTable.add(button).padRight(25f);
        buttonStyle = MediaManager.createButtonStyle("BUTTON_NEXT", textureAtlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.NEXT);
        winTable.add(button).padLeft(25f);
        winTable.setVisible(false);
        stage.addActor(winTable);

        // LOSE TABLE
        loseTable = new Table();
        loseTable.setFillParent(true);
        loseTable.center();
        loseTable.setBackground(new TextureRegionDrawable(textureAtlas.findRegion("TABLE_BG")));

        label = new Label(LanguageManager.INSTANCE.getString("gameOver"), labelStyle);
        loseTable.add(label).colspan(2).padBottom(15f).row();

        buttonStyle = MediaManager.createButtonStyle("BUTTON_EXIT", textureAtlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.EXIT);
        loseTable.add(button).colspan(2).center().row();

        Label.LabelStyle labelStyle2 = new Label.LabelStyle();
        labelStyle2.font = barFont;
        labelStyle2.fontColor = Color.valueOf("f2ff24");
        label = new Label(LanguageManager.INSTANCE.getString("insertCoin"), labelStyle2);
        label.setAlignment(Align.center);
        label.addAction(Actions.forever(sequence(Actions.fadeOut(0.5f, Interpolation.fade), Actions.fadeIn(0.5f, Interpolation.fade))));
        loseTable.add(label).colspan(2).padTop(15f).row();
        loseTable.setVisible(false);
        stage.addActor(loseTable);

        // confirm dialog in case of exit
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = new TextureRegionDrawable(textureAtlas.findRegion("CONFIRM"));
        windowStyle.stageBackground = new TextureRegionDrawable(textureAtlas.findRegion("CONFIRM_BG"));
        windowStyle.titleFont = messageFont;
        confirmWindow = new Window("", windowStyle);
        confirmWindow.setPosition(250, 154);
        confirmWindow.pack();
        confirmWindow.setModal(true);
        confirmWindow.setVisible(false);
        buttonStyle = MediaManager.createButtonStyle("BUTTON_CONFIRM", textureAtlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.CONFIRM);
        confirmWindow.add(button).padBottom(10f);
        buttonStyle = MediaManager.createButtonStyle("BUTTON_CANCEL", textureAtlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.CANCEL);
        button.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                hideConfirmExit();
            }
        });
        confirmWindow.add(button).padBottom(10f).padLeft(50f);
        stage.addActor(confirmWindow);
    }

    @Override
    public void update(float deltaTime) {
        for (int i = 0; i < messages.size; i++) {
            messages.get(i).update(deltaTime);
        }
        stage.act(deltaTime);
    }

    public void hideConfirmExit() {
        hide(confirmWindow);
    }

    private void hide(Actor actor) {
        actor.getActions().clear();
        actor.addAction(sequence(Actions.fadeOut(0.25f, Interpolation.fade), Actions.hide()));
    }

    public void showConfirmExit() {
        show(confirmWindow, 0.25f);
    }

    private void show(Actor actor, float time) {
        actor.getActions().clear();
        actor.addAction(sequence(Actions.alpha(0), Actions.show(), Actions.fadeIn(time, Interpolation.fade)));
    }

    @Override
    public void notifyScore(int score, int increased, float x, float y) {
        this.score = score;
        VolatileMessage message = messagePool.obtain();
        message.init(String.valueOf(increased), x, y, messageFont, true);
        messages.add(message);
    }

    public void showMessage(String messageString) {
        fourthMessage = messageString;
    }

    @Override
    public void notifyLives(int lives, float x, float y) {
        this.lives = lives;
    }

    @Override
    public void render(float deltaTime) {
        Color color = stage.getBatch().getColor();
        stage.getBatch().setColor(color.r, color.g, color.b, 1);
        stage.getBatch().begin();
        stage.getBatch().draw(bar, 0, 0);
        barFont.draw(stage.getBatch(), LanguageManager.INSTANCE.getString("level") + ": " + level, 200, 65);
        barFont.draw(stage.getBatch(), LanguageManager.INSTANCE.getString("lives") + ": " + lives, 200, 30);
        barFont.draw(stage.getBatch(), LanguageManager.INSTANCE.getString("score") + ": " + score, 400, 65);
        barFont.draw(stage.getBatch(), fourthMessage, 400, 30);
        for (VolatileMessage message : messages) {
            message.getFont().draw(stage.getBatch(), message.message, message.x, message.y);
        }
        stage.getBatch().end();
        stage.getBatch().setColor(color);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        barFont.dispose();
        messageFont.dispose();
        for (int i = 0; i < messages.size; i++) {
            messages.get(i).remove();
        }
    }

    @Override
    public void notifyState(GameState state) {
        switch (state) {
            case IN_GAME:
                hideConfirmExit();
                inGameTable.setVisible(true);
                hide(pauseTable);
                hide(winTable);
                hide(loseTable);
                ActivityManager.INSTANCE.showBannerAd(false);
                break;
            case PAUSE:
                inGameTable.setVisible(false);
                show(pauseTable, 0.25f);
                hide(winTable);
                hide(loseTable);
                ActivityManager.INSTANCE.showBannerAd(true);
                break;
            case WIN:
                inGameTable.setVisible(false);
                hide(pauseTable);
                show(winTable, 0.5f);
                hide(loseTable);
                ActivityManager.INSTANCE.showBannerAd(true);
                break;
            case LOSE:
                inGameTable.setVisible(false);
                hide(pauseTable);
                hide(winTable);
                show(loseTable, 0.5f);
                ActivityManager.INSTANCE.showBannerAd(true);
                break;
        }
    }

    public Stage getStage() {
        return stage;
    }

    private class VolatileMessage implements Pool.Poolable {
        private String message;
        private float timer = 0.5f;
        private float x, y;
        private BitmapFont font;
        private boolean movement;

        public void init(String message, float x, float y, BitmapFont font, boolean movement) {
            this.message = message;
            this.x = x;
            this.y = y;
            this.font = font;
            this.movement = movement;
        }

        @Override
        public void reset() {
            this.timer = 0.5f;
        }

        public void update(float deltaTime) {
            if (timer < 0) {
                remove();
            } else {
                timer -= deltaTime;
                if (movement) y += 1f;
            }
        }

        private void remove() {
            messagePool.free(this);
            messages.removeValue(this, true);
        }

        public BitmapFont getFont() {
            return font;
        }
    }
}
