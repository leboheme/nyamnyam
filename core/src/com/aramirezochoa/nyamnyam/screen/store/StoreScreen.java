package com.aramirezochoa.nyamnyam.screen.store;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
import com.aramirezochoa.nyamnyam.screen.menu.MenuAction;
import com.aramirezochoa.nyamnyam.store.StoreManager;
import com.aramirezochoa.nyamnyam.store.StoreTransactionListener;

/**
 * Created by boheme on 19/02/15.
 */
public class StoreScreen extends AbstractScreen {

    private Stage stage;
    private BitmapFont bigFont, smallFont;

    public StoreScreen() {
        super(MediaManager.STORE);
    }

    @Override
    public void show() {
        this.stage = new Stage(new StretchViewport(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        TextureAtlas atlas = getMediaManager().get("data/store/store.atlas");
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 46;
        parameter.characters = Constant.NYAM_CHARS;
        bigFont = ((FreeTypeFontGenerator) getMediaManager().get("data/store/font.ttf")).generateFont(parameter);

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.characters = Constant.NYAM_CHARS;
        smallFont = ((FreeTypeFontGenerator) getMediaManager().get("data/store/font.ttf")).generateFont(parameter);

        Table table = new Table();
        table.setFillParent(true);

        table.add(new Label(LanguageManager.INSTANCE.getString("store"), new Label.LabelStyle(bigFont, Color.BLACK))).center().colspan(2).padTop(20f).row();
        table.add(new Label(LanguageManager.INSTANCE.getString("storeDescription"), new Label.LabelStyle(smallFont, Color.BLACK))).center().colspan(2).padTop(10).row();

        Table scrollTable = new Table();
        scrollTable.left();

        for (StoreItem item : StoreItem.values()) {
            addItem(scrollTable, atlas, item);
        }

        ScrollPane scroll = new ScrollPane(scrollTable);
        scroll.setScrollX(0);
        table.add(scroll).fill().expand().pad(25f).padTop(10f);

        //Add back button
        Button.ButtonStyle buttonStyle = MediaManager.createButtonStyle("BUTTON_EXIT", atlas);
        Button button = MediaManager.createButton(buttonStyle, MenuAction.EXIT);
        table.add(button).padRight(10f).padBottom(10f).bottom().right();
        table.row();

        table.setBackground(new TextureRegionDrawable(atlas.findRegion("BACKGROUND")));
        stage.addActor(table);

        ActivityManager.INSTANCE.trackScreen(ActivityManager.SCREEN_STORE);
    }

    private void addItem(Table scrollTable, TextureAtlas atlas, StoreItem storeItem) {
        Table table = new Table();
        table.setBackground(new TextureRegionDrawable(atlas.findRegion("button_bg")));
        table.left();

        Image icon = new Image(atlas.findRegion(storeItem.name()));
        table.add(icon).center().left().padLeft(10f);

        Label.LabelStyle labelStyle = new Label.LabelStyle(smallFont, Color.BLACK);
        String labelLiteral = LanguageManager.INSTANCE.getString(DataManager.INSTANCE.getCurrentCharacter().name() + "_" + storeItem.getItemId())
                + "\n"
                + LanguageManager.INSTANCE.getString("price") + ": " + storeItem.getPrice();
        Label description = new Label(labelLiteral, labelStyle);
        description.setAlignment(Align.left);
        table.add(description).center().left().padLeft(10f);

        Button.ButtonStyle buttonStyle = MediaManager.createButtonStyle("BUY", atlas);
        ItemStore itemStore = new ItemStore(buttonStyle, storeItem);
        table.add(itemStore).expand().center().right().padRight(10f);

        scrollTable.add(table).spaceBottom(25f).row();
    }

    private static class ItemStore extends Button {

        public ItemStore(ButtonStyle style, final StoreItem storeItem) {
            super(style);
            addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.error("Store", "Buy storeItem " + storeItem.name());
                    StoreManager.INSTANCE.purchaseItem(storeItem,
                            new StoreTransactionListener() {
                                @Override
                                public void transactionFinished(int responseCode, String token) {
                                    if (responseCode == 0) {
                                        StoreManager.INSTANCE.addExtraItem(storeItem, token);
                                    }
                                }
                            });
                }
            });
        }
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
                ScreenManager.INSTANCE.changeToLastScreen();
                break;
            default:
                if (isBackJustPressed()) {
                    ScreenManager.INSTANCE.changeToLastScreen();
                }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        bigFont.dispose();
        smallFont.dispose();
    }

}
