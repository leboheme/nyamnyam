package com.aramirezochoa.nyamnyam.screen.help;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.DataManager;
import com.aramirezochoa.nyamnyam.activity.ActivityManager;
import com.aramirezochoa.nyamnyam.i18n.LanguageManager;
import com.aramirezochoa.nyamnyam.input.InputManager;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.AbstractScreen;
import com.aramirezochoa.nyamnyam.screen.ScreenManager;
import com.aramirezochoa.nyamnyam.screen.ScreenType;
import com.aramirezochoa.nyamnyam.screen.menu.MenuAction;

/**
 * Created by leboheme on 11/02/15.
 */
public class HelpScreen extends AbstractScreen {

    private Stage stage;
    private BitmapFont font;

    public HelpScreen() {
        super(MediaManager.HELP);
    }

    @Override
    public void show() {
        this.stage = new Stage(new StretchViewport(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        TextureAtlas atlas = getMediaManager().get("data/help/help.atlas");
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 46;
        parameter.characters = Constant.NYAM_CHARS;
        font = ((FreeTypeFontGenerator) getMediaManager().get("data/help/font.ttf")).generateFont(parameter);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);

        Table table = new Table();
        table.setFillParent(true);

        table.add(new Label(LanguageManager.INSTANCE.getString("tips"), labelStyle)).center().colspan(2).padTop(20f).row();

        Table scrollTable = new Table();
        scrollTable.left();

        String mainCharacer = DataManager.INSTANCE.getCurrentCharacter().name() + "_";

        scrollTable.add(new Image(atlas.findRegion(mainCharacer + "ONE"))).left().spaceLeft(50f).spaceBottom(25f);
        scrollTable.add(new Label(LanguageManager.INSTANCE.getString(mainCharacer + "helpOne"), labelStyle)).left().spaceLeft(30f).spaceBottom(25f).expand();
        scrollTable.row();

        scrollTable.add(new Image(atlas.findRegion(mainCharacer + "TWO"))).left().spaceLeft(50f).spaceBottom(25f);
        scrollTable.add(new Label(LanguageManager.INSTANCE.getString(mainCharacer + "helpTwo"), labelStyle)).left().spaceLeft(30f).spaceBottom(25f);
        scrollTable.row();

        scrollTable.add(new Image(atlas.findRegion(mainCharacer + "THREE"))).left().spaceLeft(50f).spaceBottom(25f);
        scrollTable.add(new Label(LanguageManager.INSTANCE.getString(mainCharacer + "helpThree"), labelStyle)).left().spaceLeft(30f).spaceBottom(25f);
        scrollTable.row();

        scrollTable.add(new Image(atlas.findRegion(mainCharacer + "FOUR"))).left().spaceLeft(50f).spaceBottom(25f);
        scrollTable.add(new Label(LanguageManager.INSTANCE.getString(mainCharacer + "helpFour"), labelStyle)).left().spaceLeft(30f).spaceBottom(25f);
        scrollTable.row();

        scrollTable.add(new Image(atlas.findRegion(mainCharacer + "FIVE"))).left().spaceLeft(50f).spaceBottom(25f);
        scrollTable.add(new Label(LanguageManager.INSTANCE.getString(mainCharacer + "helpFive"), labelStyle)).left().spaceLeft(30f).spaceBottom(25f);
        scrollTable.row();

        ScrollPane scroll = new ScrollPane(scrollTable);
        scroll.setScrollX(0);
        table.add(scroll).fill().expand().pad(25f);

        //Add back button
        Button.ButtonStyle buttonStyle = MediaManager.createButtonStyle("BUTTON_EXIT", atlas);
        Button button = MediaManager.createButton(buttonStyle, MenuAction.EXIT);
        table.add(button).padRight(10f).padBottom(10f).bottom().right();
        table.row();

        table.setBackground(new TextureRegionDrawable(atlas.findRegion("BACKGROUND")));
        stage.addActor(table);

        ActivityManager.INSTANCE.trackScreen(ActivityManager.SCREEN_HELP);
    }

    @Override
    public void render(float delta) {
        Gdx.graphics.getGL20().glClearColor(1, 1, 1, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        MenuAction menuAction = InputManager.INSTANCE.getMenuAction();
        switch (menuAction) {
            case EXIT:
                ScreenManager.INSTANCE.changeTo(ScreenType.MENU);
                break;
            default:
                if (isBackJustPressed()) {
                    ScreenManager.INSTANCE.changeTo(ScreenType.MENU);
                }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        font.dispose();
    }
}
