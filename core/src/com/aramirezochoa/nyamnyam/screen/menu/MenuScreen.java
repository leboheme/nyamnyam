package com.aramirezochoa.nyamnyam.screen.menu;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.DataManager;
import com.aramirezochoa.nyamnyam.activity.ActivityManager;
import com.aramirezochoa.nyamnyam.activity.ActivityTransaction;
import com.aramirezochoa.nyamnyam.i18n.LanguageManager;
import com.aramirezochoa.nyamnyam.input.InputManager;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.AbstractScreen;
import com.aramirezochoa.nyamnyam.screen.ScreenManager;
import com.aramirezochoa.nyamnyam.screen.ScreenType;
import com.aramirezochoa.nyamnyam.screen.game.core.entity.avatar.main.MainAvatarType;
import com.aramirezochoa.nyamnyam.screen.game.status.GameStatus;
import com.aramirezochoa.nyamnyam.screen.store.StoreItem;
import com.aramirezochoa.nyamnyam.store.StoreManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.concurrent.TimeUnit;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by boheme on 12/01/15.
 */
public class MenuScreen extends AbstractScreen {

    private BitmapFont font, smallFont;
    private Stage stage;
    private boolean newGame, continueGame;
    private float playTimer;

    private Window confirmWindow, giftAdviseWindow, noDailyGiftWindow, lotteryWindow, giftErrorWindow;
    private boolean dailyGiftAvailable = false;
    private boolean showRateUs;

    public MenuScreen() {
        super(MediaManager.MENU);
    }

    @Override
    public void show() {
        this.stage = new Stage(new StretchViewport(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        newGame = continueGame = false;
        playTimer = 0.5f;
        dailyGiftAvailable = DataManager.INSTANCE.isDailyGiftAvailable();

        TextureAtlas atlas = getMediaManager().get("data/menu/menu.atlas");

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 48;
        parameter.characters = Constant.NYAM_CHARS;
        font = ((FreeTypeFontGenerator) getMediaManager().get("data/menu/font.ttf")).generateFont(parameter);

        parameter.size = 40;
        smallFont = ((FreeTypeFontGenerator) getMediaManager().get("data/menu/font.ttf")).generateFont(parameter);

        StoreManager.INSTANCE.checkExtraItems();

        setBackground(atlas);
        setMainButtons(atlas);
        setBorderButtons(atlas);
        setConfirmExitWindow(atlas);
        setGiftAdviseWindow(atlas);
        setNoDailyGiftWindow(atlas);
        setGiftErrorWindow(atlas);

        ActivityManager.INSTANCE.trackScreen(ActivityManager.SCREEN_MENU);

        showRateUs = DataManager.INSTANCE.isShowRateUs();
    }

    private void setBackground(TextureAtlas atlas) {
        stage.addActor(new Image(atlas.findRegion("BACKGROUND")));
        Image image = new Image(atlas.findRegion("title"));
        image.setX(65);
        image.setY(235);
        stage.addActor(image);
        switch (DataManager.INSTANCE.getCurrentCharacter()) {
            case NYAM:
                stage.addActor(new InteractiveNyam(atlas));
                break;
            case GLUTY:
                stage.addActor(new StaticCharacter(atlas, "GLUTY", 390, 40));
                break;
            case TFLY:
                stage.addActor(new StaticCharacter(atlas, "TFLY", 440, 30));
                break;
        }
    }

    private void setMainButtons(TextureAtlas atlas) {
        Table table = new Table();
        table.setFillParent(true);
        table.left().bottom();
        TextButton.TextButtonStyle textButtonStyle;
        TextButton textButton;

        if (!DataManager.INSTANCE.isFirstTime()) {
            textButtonStyle = MediaManager.createTextButtonStyle("button", atlas, font);
            textButton = MediaManager.createTextButton(LanguageManager.INSTANCE.getString("continue"), textButtonStyle, MenuAction.CONTINUE);
        } else {
            textButtonStyle = MediaManager.createTextButtonStyle("button_disabled", atlas, font);
            textButton = MediaManager.createTextButton(LanguageManager.INSTANCE.getString("continue"), textButtonStyle, MenuAction.NONE);
        }
        table.add(textButton).padLeft(35f).row();

        textButtonStyle = MediaManager.createTextButtonStyle("button", atlas, font);
        textButton = MediaManager.createTextButton(LanguageManager.INSTANCE.getString("start"), textButtonStyle, MenuAction.START);
        table.add(textButton).padLeft(35f).padTop(15f).padBottom(35f);

        stage.addActor(table);
    }

    private void setBorderButtons(TextureAtlas atlas) {
        setTopLeftButtons(atlas);
        setTopRightButtons(atlas);
        setBottomRightButtons(atlas);
    }

    private void setBottomRightButtons(TextureAtlas atlas) {
        Button.ButtonStyle buttonStyle;
        Button button;

        Table bottomRightButtons = new Table();
        bottomRightButtons.setFillParent(true);
        bottomRightButtons.bottom().right();

        if (dailyGiftAvailable) {
            Image arrow = new Image(atlas.findRegion("ARROW_GIFT")) {
                public void act(float deltaTime) {
                    super.act(deltaTime);
                    if (!dailyGiftAvailable) {
                        remove();
                    }
                }
            };
            arrow.addAction(Actions.forever(Actions.sequence(Actions.fadeOut(0.5f, Interpolation.linear), Actions.fadeIn(0.5f, Interpolation.linear))));
            bottomRightButtons.add(arrow).right().padBottom(60f).padRight(5f);

            buttonStyle = MediaManager.createButtonStyle("GIFT", atlas);
            button = MediaManager.createButton(buttonStyle, MenuAction.GIFT);
            bottomRightButtons.add(button).right().bottom().padRight(10f).padBottom(10f);
            bottomRightButtons.row();
        } else {
            buttonStyle = MediaManager.createButtonStyle("GIFT", atlas);
            button = MediaManager.createButton(buttonStyle, MenuAction.GIFT);
            bottomRightButtons.add(button).colspan(2).right().top().pad(10f);
            bottomRightButtons.row();
        }

        buttonStyle = MediaManager.createButtonStyle("BUTTON_HELP", atlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.HELP);
        bottomRightButtons.add(button).right().bottom().padBottom(10f).padRight(10f);

        CheckBox.CheckBoxStyle checkBoxStyle = MediaManager.createCheckBoxStyle("BUTTON_SOUND", atlas, font);
        CheckBox checkBox = MediaManager.createCheckBox(checkBoxStyle);
        bottomRightButtons.add(checkBox).right().bottom().padBottom(10f).padRight(10f);

        stage.addActor(bottomRightButtons);
    }

    private void setTopRightButtons(TextureAtlas atlas) {
        Table topRightButtons = new Table();
        topRightButtons.setFillParent(true);
        topRightButtons.top().right();

        switch (DataManager.INSTANCE.getCurrentCharacter()) {
            case NYAM:
                addCharButton("BUTTON_GLUTY", atlas, topRightButtons, MenuAction.PLAY_GLUTY);
                addCharButton("BUTTON_TFLY", atlas, topRightButtons, MenuAction.PLAY_TFLY);
                break;
            case GLUTY:
                addCharButton("BUTTON_NYAM", atlas, topRightButtons, MenuAction.PLAY_NYAM);
                addCharButton("BUTTON_TFLY", atlas, topRightButtons, MenuAction.PLAY_TFLY);
                break;
            case TFLY:
                addCharButton("BUTTON_NYAM", atlas, topRightButtons, MenuAction.PLAY_NYAM);
                addCharButton("BUTTON_GLUTY", atlas, topRightButtons, MenuAction.PLAY_GLUTY);
                break;

        }

        stage.addActor(topRightButtons);
    }

    private void addCharButton(String code, TextureAtlas atlas, Table topRightButtons, MenuAction action) {
        Button.ButtonStyle buttonStyle;
        Button button;
        buttonStyle = MediaManager.createButtonStyle(code, atlas);
        button = MediaManager.createButton(buttonStyle, action);
        topRightButtons.add(button).top().right().padTop(10f).padRight(10f);
    }

    private void setTopLeftButtons(TextureAtlas atlas) {
        Table topLeftButtons = new Table();
        topLeftButtons.setFillParent(true);
        topLeftButtons.top().left();
        // Buttons
        Button.ButtonStyle buttonStyle;
        Button button;

        TextButton.TextButtonStyle textButtonStyle = MediaManager.createTextButtonStyle("LIVES_BG", atlas, smallFont);
        TextButton textButton = new TextButton("", textButtonStyle) {
            public void act(float delta) {
                super.act(delta);
                getLabel().setText(String.valueOf(DataManager.INSTANCE.getGameStatus().getLives()));
            }
        };
        textButton.getLabel().setAlignment(Align.right);
        topLeftButtons.add(textButton).left().top().padLeft(10f).padTop(10f);

        final TextButton.TextButtonStyle simpleStyle = MediaManager.createTextButtonStyle("LIVES_BG_SIMPLE", atlas, smallFont);
        final TextButton.TextButtonStyle extendedStyle = MediaManager.createTextButtonStyle("LIVES_BG_EXTENDED", atlas, smallFont);
        textButton = new TextButton("", simpleStyle) {
            private boolean timerEnabled = false;

            public void act(float delta) {
                super.act(delta);
                if (timerEnabled) {
//                    I know it's 5 seconds because of the migration
                    long timer = DataManager.INSTANCE.getNextFreeLiveTimerRemain();
                    getLabel().setText((int) TimeUnit.MILLISECONDS.toSeconds(timer));
                    if (!DataManager.INSTANCE.isFreeLiveRunning()) {
                        timerEnabled = false;
                        getLabel().setText("");
                        setStyle(simpleStyle);
                    }
                } else {
                    if (DataManager.INSTANCE.isFreeLiveRunning()) {
                        timerEnabled = true;
                        setStyle(extendedStyle);
                    }
                }
            }
        };
        topLeftButtons.add(textButton).left().top().padTop(10f);
        stage.addActor(topLeftButtons);
    }

    private void setConfirmExitWindow(TextureAtlas atlas) {
        Button.ButtonStyle buttonStyle;
        Button button;// confirm dialog in case of exit
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = new TextureRegionDrawable(atlas.findRegion("CONFIRM"));
        windowStyle.stageBackground = new TextureRegionDrawable(atlas.findRegion("CONFIRM_BG"));
        windowStyle.titleFont = font;
        confirmWindow = new Window("", windowStyle);

        Label label = new Label(LanguageManager.INSTANCE.getString("confirmNewGame"), new Label.LabelStyle(font, Color.valueOf("515151")));
        confirmWindow.add(label).colspan(2).pad(25f).row();
        buttonStyle = MediaManager.createButtonStyle("BUTTON_CONFIRM", atlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.CONFIRM);
        confirmWindow.add(button).padBottom(10f);
        buttonStyle = MediaManager.createButtonStyle("BUTTON_CANCEL", atlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.CANCEL);
        button.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                hideWindow(confirmWindow);
            }
        });
        confirmWindow.add(button).padBottom(10f).padLeft(50f);
        confirmWindow.center();
        confirmWindow.setModal(true);
        confirmWindow.pack();
        confirmWindow.setVisible(false);
        confirmWindow.setPosition((Constant.SCREEN_WIDTH - confirmWindow.getWidth()) / 2, (Constant.SCREEN_HEIGHT - confirmWindow.getHeight()) / 2);
        stage.addActor(confirmWindow);
    }

    private void setGiftAdviseWindow(TextureAtlas atlas) {
        Button.ButtonStyle buttonStyle;
        Button button;
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = new TextureRegionDrawable(atlas.findRegion("CONFIRM"));
        windowStyle.stageBackground = new TextureRegionDrawable(atlas.findRegion("CONFIRM_BG"));
        windowStyle.titleFont = smallFont;
        giftAdviseWindow = new Window("", windowStyle);
        giftAdviseWindow.setBounds(100, 100, 600, 280);

        Label label = new Label(LanguageManager.INSTANCE.getString("lotteryDescription"), new Label.LabelStyle(smallFont, Color.valueOf("515151")));
        label.setAlignment(Align.center);
        giftAdviseWindow.add(label).colspan(2).pad(25f).padTop(100).row();
        buttonStyle = MediaManager.createButtonStyle("BUTTON_CONFIRM", atlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.NONE);
        button.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                showVideoGift();
            }
        });
        giftAdviseWindow.add(button).bottom().padBottom(20f).left().padLeft(150f).expand();
        buttonStyle = MediaManager.createButtonStyle("BUTTON_CANCEL", atlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.NONE);
        button.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                hideWindow(giftAdviseWindow);
            }
        });
        giftAdviseWindow.add(button).bottom().padBottom(20f).right().padRight(150f);
        giftAdviseWindow.setVisible(false);
        stage.addActor(giftAdviseWindow);
    }

    private void setNoDailyGiftWindow(TextureAtlas atlas) {
        Button.ButtonStyle buttonStyle;
        Button button;
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = new TextureRegionDrawable(atlas.findRegion("CONFIRM"));
        windowStyle.stageBackground = new TextureRegionDrawable(atlas.findRegion("CONFIRM_BG"));
        windowStyle.titleFont = smallFont;
        noDailyGiftWindow = new Window("", windowStyle);
        noDailyGiftWindow.setBounds(100, 100, 600, 280);

        Label label = new Label(LanguageManager.INSTANCE.getString("noMoreGift"), new Label.LabelStyle(smallFont, Color.valueOf("515151")));
        label.setAlignment(Align.center);
        noDailyGiftWindow.add(label).colspan(2).pad(25f).padTop(100).row();
        buttonStyle = MediaManager.createButtonStyle("BUTTON_CONFIRM", atlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.NONE);
        button.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                hideWindow(noDailyGiftWindow);
            }
        });
        noDailyGiftWindow.add(button).bottom().padBottom(20f).right().padRight(150f);
        noDailyGiftWindow.setVisible(false);
        stage.addActor(noDailyGiftWindow);
    }

    private void setGiftErrorWindow(TextureAtlas atlas) {
        Button.ButtonStyle buttonStyle;
        Button button;
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = new TextureRegionDrawable(atlas.findRegion("CONFIRM"));
        windowStyle.stageBackground = new TextureRegionDrawable(atlas.findRegion("CONFIRM_BG"));
        windowStyle.titleFont = smallFont;
        giftErrorWindow = new Window("", windowStyle);
        giftErrorWindow.setBounds(100, 100, 600, 280);

        Label label = new Label(LanguageManager.INSTANCE.getString("giftError"), new Label.LabelStyle(smallFont, Color.valueOf("515151")));
        label.setAlignment(Align.center);
        giftErrorWindow.add(label).colspan(2).pad(25f).padTop(100).row();
        buttonStyle = MediaManager.createButtonStyle("BUTTON_CONFIRM", atlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.NONE);
        button.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                hideWindow(giftErrorWindow);
            }
        });
        giftErrorWindow.add(button).bottom().padBottom(20f).right().padRight(150f);
        giftErrorWindow.setVisible(false);
        stage.addActor(giftErrorWindow);
    }

    private void showLotteryWindow(TextureAtlas atlas) {
        Button.ButtonStyle buttonStyle;
        Button button;
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = new TextureRegionDrawable(atlas.findRegion("CONFIRM"));
        windowStyle.stageBackground = new TextureRegionDrawable(atlas.findRegion("CONFIRM_BG"));
        windowStyle.titleFont = smallFont;
        lotteryWindow = new Window("", windowStyle);
        lotteryWindow.setBounds(50, 50, 700, 380);
        lotteryWindow.center();

        final StoreItem gift = StoreItem.randomGift();

        Label label = new Label(LanguageManager.INSTANCE.getString("giftGotten"), new Label.LabelStyle(smallFont, Color.valueOf("515151")));
        label.setAlignment(Align.center);
        lotteryWindow.add(label).pad(10).row();
        Image image = new Image(atlas.findRegion(gift.name()));
        lotteryWindow.add(image).pad(10).row();
        label = new Label(LanguageManager.INSTANCE.getString(DataManager.INSTANCE.getCurrentCharacter().name() + "_" + gift.getItemId()), new Label.LabelStyle(smallFont, Color.valueOf("515151")));
        label.setAlignment(Align.center);
        lotteryWindow.add(label).pad(10).row();
        buttonStyle = MediaManager.createButtonStyle("BUTTON_CONFIRM", atlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.NONE);
        button.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                hideWindow(lotteryWindow);
            }
        });
        lotteryWindow.add(button).pad(10f);

        lotteryWindow.setVisible(false);
        stage.addActor(lotteryWindow);

        lotteryWindow.addAction(sequence(Actions.alpha(0), Actions.show(), Actions.fadeIn(0.25f, Interpolation.fade), Actions.delay(0.25f), Actions.run(new Runnable() {
            @Override
            public void run() {
                DataManager.INSTANCE.dailyGiftDone();
                StoreManager.INSTANCE.checkItem(gift);
            }
        })));
    }

    private void showExternalGift(TextureAtlas atlas) {
        Button.ButtonStyle buttonStyle;
        Button button;
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = new TextureRegionDrawable(atlas.findRegion("CONFIRM"));
        windowStyle.stageBackground = new TextureRegionDrawable(atlas.findRegion("CONFIRM_BG"));
        windowStyle.titleFont = smallFont;
        lotteryWindow = new Window("", windowStyle);
        lotteryWindow.setBounds(50, 50, 700, 380);
        lotteryWindow.center();

//        final StoreItem gift = StoreItem.randomGift();

        Label label = new Label(LanguageManager.INSTANCE.getString("giftGotten"), new Label.LabelStyle(smallFont, Color.valueOf("515151")));
        label.setAlignment(Align.center);
        lotteryWindow.add(label).pad(10).row();
        Image image = new Image(atlas.findRegion("LIVE"));
        lotteryWindow.add(image).pad(10).row();
        label = new Label(LanguageManager.INSTANCE.getString("fiveLivesGift"), new Label.LabelStyle(smallFont, Color.valueOf("515151")));
        label.setAlignment(Align.center);
        lotteryWindow.add(label).pad(10).row();
        buttonStyle = MediaManager.createButtonStyle("BUTTON_CONFIRM", atlas);
        button = MediaManager.createButton(buttonStyle, MenuAction.NONE);
        button.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                hideWindow(lotteryWindow);
            }
        });
        lotteryWindow.add(button).pad(10f);
        lotteryWindow.setVisible(false);
        stage.addActor(lotteryWindow);
        lotteryWindow.addAction(sequence(Actions.alpha(0), Actions.show(), Actions.fadeIn(0.25f, Interpolation.fade), Actions.delay(0.25f), Actions.run(new Runnable() {
            @Override
            public void run() {
                GameStatus gameStatus = DataManager.INSTANCE.getGameStatus();
                gameStatus.increaseLive();
                gameStatus.increaseLive();
                gameStatus.increaseLive();
                gameStatus.increaseLive();
                gameStatus.increaseLive();
                DataManager.INSTANCE.saveGameStatus(false);
            }
        })));
    }

    @Override
    public void render(float delta) {
        Gdx.graphics.getGL20().glClearColor(1, 1, 1, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        DataManager.INSTANCE.updateFreeLive();

        stage.act();
        stage.draw();

        if (DataManager.INSTANCE.isExternalGift()) {
            showExternalGift((TextureAtlas) getMediaManager().get("data/menu/menu.atlas"));
            ActivityManager.INSTANCE.trackReward();
            DataManager.INSTANCE.setExternalGift(false);
        }

        if (Gdx.input.justTouched()) {
            MediaManager.MENU.interactionStarted();
        }
        MenuAction menuAction = InputManager.INSTANCE.getMenuAction();
        switch (menuAction) {
            case CONTINUE:
                if (!DataManager.INSTANCE.isFirstTime()) {
                    getMediaManager().playSound(MediaManager.SOUND_BUTTON);
                    continueGame = true;
                }
                break;
            case START:
                getMediaManager().playSound(MediaManager.SOUND_BUTTON);
                showWindow(confirmWindow);
                break;
            case CONFIRM:
                getMediaManager().playSound(MediaManager.SOUND_BUTTON);
                DataManager.INSTANCE.loadNewGame();
                newGame = true;
                break;
            case HELP:
                ScreenManager.INSTANCE.changeTo(ScreenType.HELP);
                break;
            case GIFT:
                if (dailyGiftAvailable) {
//                    showWindow(giftAdviseWindow);
                    showVideoGift();
                } else {
                    showWindow(noDailyGiftWindow);
                }
                break;
            case PLAY_NYAM:
                DataManager.INSTANCE.setCurrentCharacter(MainAvatarType.NYAM);
                ScreenManager.INSTANCE.changeTo(ScreenType.MENU);
                ActivityManager.INSTANCE.trackPlayCharacter(MainAvatarType.NYAM);
                break;
            case PLAY_GLUTY:
                DataManager.INSTANCE.setCurrentCharacter(MainAvatarType.GLUTY);
                ScreenManager.INSTANCE.changeTo(ScreenType.MENU);
                ActivityManager.INSTANCE.trackPlayCharacter(MainAvatarType.GLUTY);
                break;
            case PLAY_TFLY:
                DataManager.INSTANCE.setCurrentCharacter(MainAvatarType.TFLY);
                ScreenManager.INSTANCE.changeTo(ScreenType.MENU);
                ActivityManager.INSTANCE.trackPlayCharacter(MainAvatarType.TFLY);
                break;
            default:
                if (isBackJustPressed()) {
                    if (confirmWindow.isVisible() || giftAdviseWindow.isVisible() || noDailyGiftWindow.isVisible() || giftErrorWindow.isVisible()) {
                        hideWindow(confirmWindow);
                        hideWindow(giftAdviseWindow);
                        hideWindow(noDailyGiftWindow);
                        hideWindow(giftErrorWindow);
                    }
                }
        }

        if (continueGame) {
            if (playTimer < 0) {
                ActivityManager.INSTANCE.trackContinue();
                if (DataManager.INSTANCE.getGameStatus().isFinished()) {
                    ScreenManager.INSTANCE.changeTo(ScreenType.END);
                } else {
                    ScreenManager.INSTANCE.changeTo(ScreenType.GAME);
                }
            } else {
                playTimer -= delta;
            }
        } else if (newGame) {
            if (playTimer < 0) {
                ActivityManager.INSTANCE.trackNewGame();
                if (DataManager.INSTANCE.getCurrentCharacter().equals(MainAvatarType.NYAM)) {
                    ScreenManager.INSTANCE.changeTo(ScreenType.INTRO);
                } else {
                    ScreenManager.INSTANCE.changeTo(ScreenType.GAME);
                }
            } else {
                playTimer -= delta;
            }
        }

        // show rate us
        if (showRateUs) {
            ActivityManager.INSTANCE.showRateUs(new ActivityTransaction() {
                @Override
                public void done(boolean result) {
                    DataManager.INSTANCE.notifyShowRateUs(result);
                }
            });
            showRateUs = false;
        }
    }

    private void showWindow(Window window) {
        window.addAction(sequence(Actions.alpha(0), Actions.show(), Actions.fadeIn(0.25f, Interpolation.fade)));
    }

    private void hideWindow(Window window) {
        if (window != null) {
            window.addAction(sequence(Actions.fadeOut(0.25f, Interpolation.fade), Actions.hide()));
        }
    }


    private void showVideoGift() {
        ActivityManager.INSTANCE.showVideoGift(new ActivityTransaction() {
            @Override
            public void done(boolean result) {
                if (result) {
                    ActivityManager.INSTANCE.trackDailyGift(true);
                    startLottery();
                    dailyGiftAvailable = false;
                } else {
                    ActivityManager.INSTANCE.trackDailyGift(false);
                    hideWindow(giftAdviseWindow);
                    showWindow(giftErrorWindow);
                }
            }
        });
    }

    private void startLottery() {
        hideWindow(giftAdviseWindow);
        showLotteryWindow((TextureAtlas) getMediaManager().get("data/menu/menu.atlas"));
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        font.dispose();
        smallFont.dispose();
    }

    private static class InteractiveNyam extends Actor {
        private TextureRegion currentFrame;
        private float currentFrameTimer;
        private Animation<TextureRegion> animationEating, animationNyam;
        private boolean nyam = false;

        public InteractiveNyam(TextureAtlas textureAtlas) {
            this.animationEating = new Animation(0.1f, textureAtlas.findRegions("NYAM_EATING"));
            this.animationEating.setPlayMode(Animation.PlayMode.LOOP);
            this.animationNyam = new Animation(0.1f, textureAtlas.findRegions("NYAM_NYAM"));
            this.animationNyam.setPlayMode(Animation.PlayMode.NORMAL);
            this.currentFrameTimer = 0;
            this.currentFrame = animationEating.getKeyFrame(currentFrameTimer);
            addListener(new InputListener() {
                public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, int button) {
                    if (!nyam) {
                        MediaManager.MENU.stopAllThemes();
                        MediaManager.MENU.playSound(MediaManager.SOUND_INTRO_NYAMNYAM);
                        nyam = true;
                        currentFrameTimer = 0;
                        currentFrame = animationNyam.getKeyFrame(currentFrameTimer);
                    }
                    return true;
                }
            });
            setBounds(375, 40, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
            MediaManager.MENU.playTheme(MediaManager.THEME_CHEWING);
        }

        public void act(float deltaTime) {
            super.act(deltaTime);
            if (nyam) {
                if (animationNyam.isAnimationFinished(currentFrameTimer)) {
                    currentFrameTimer = 0;
                    currentFrame = animationEating.getKeyFrame(currentFrameTimer);
                    nyam = false;
                    MediaManager.MENU.playTheme(MediaManager.THEME_CHEWING);
                } else {
                    MediaManager.MENU.stopTheme(MediaManager.THEME_CHEWING);
                    currentFrame = animationNyam.getKeyFrame(currentFrameTimer);
                }
            } else {
                currentFrame = animationEating.getKeyFrame(currentFrameTimer);
            }
            currentFrameTimer += 1.5f * deltaTime;

        }

        public void draw(Batch batch, float parentAlpha) {
            batch.draw(currentFrame, 375, 40);
        }
    }

    private static class StaticCharacter extends Actor {
        private TextureRegion currentFrame;
        private float currentFrameTimer;
        private Animation<TextureRegion> animation;

        public StaticCharacter(TextureAtlas textureAtlas, String code, float x, float y) {
            this.animation = new Animation(0.1f, textureAtlas.findRegions(code));
            this.animation.setPlayMode(Animation.PlayMode.LOOP);
            this.currentFrameTimer = 0;
            this.currentFrame = animation.getKeyFrame(currentFrameTimer);
            setBounds(x, y, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        }

        public void act(float deltaTime) {
            super.act(deltaTime);
            currentFrame = animation.getKeyFrame(currentFrameTimer);
            currentFrameTimer += 1.5f * deltaTime;
        }

        public void draw(Batch batch, float parentAlpha) {
            batch.draw(currentFrame, getX(), getY());
        }
    }

}
