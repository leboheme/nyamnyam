package com.aramirezochoa.nyamnyam.screen.splash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.screen.ScreenManager;
import com.aramirezochoa.nyamnyam.screen.ScreenType;

/**
 * Created by leboheme on 25/02/15.
 */
public class SplashScreen implements Screen {

    Texture textureTitle;
    TextureRegion title;
    Texture textureOver;
    TextureRegion over;

    private Stage stage;

    private float overPosition = 0;


    @Override
    public void show() {
        textureTitle = new Texture(Gdx.files.internal("data/splash/title.png"));
        title = new TextureRegion(textureTitle);

        textureOver = new Texture(Gdx.files.internal("data/splash/over.png"));
        over = new TextureRegion(textureOver);

        stage = new Stage(new StretchViewport(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT));
        Table table = new Table();
        table.setFillParent(true);

        table.add(new Actor() {
            @Override
            public void draw(Batch batch, float alpha) {
                batch.draw(title, getX() - title.getRegionWidth() / 2, getY() - title.getRegionHeight() / 2);
            }
        });

        table.add(new Actor() {
            @Override
            public void act(float deltaTime) {
                setX(getX() + 5f);
                overPosition = getX() - over.getRegionWidth() / 2;
            }

            @Override
            public void draw(Batch batch, float alpha) {
                batch.draw(over, getX() - over.getRegionWidth() / 2, getY() - over.getRegionHeight() / 2);
            }
        });

        stage.addActor(table);
    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void render(float delta) {
        Gdx.graphics.getGL20().glClearColor(1, 1, 1, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        stage.act();
        stage.draw();
        if (overPosition > Constant.SCREEN_WIDTH) {
            ScreenManager.INSTANCE.changeTo(ScreenType.MENU);
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {
        textureTitle.dispose();
        textureOver.dispose();
        stage.dispose();
    }

}
