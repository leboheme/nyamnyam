package com.aramirezochoa.nyamnyam.screen.loading;

import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.ScreenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Created by boheme on 12/01/15.
 */
public class LoadingScreen implements Screen {

    private TextureAtlas textureAtlas;
    private Stage stage;
    private MediaManager mediaManager;
    private float loadingCounter = 2f;

    private enum LoadingType {
        BANQUI {
            @Override
            public TextureAtlas getTextureAtlas() {
                return MediaManager.LOADING.get("data/loading/banqui.atlas");
            }

            @Override
            public String getAnimationName() {
                return "BANQUI";
            }

            @Override
            public float getOffsetX() {
                return 240;
            }

            @Override
            public float getOffsetY() {
                return 50;
            }

            @Override
            public float getFrameDuration() {
                return 0.5f;
            }
        },
        GLUTY {
            @Override
            public TextureAtlas getTextureAtlas() {
                return MediaManager.LOADING.get("data/loading/gluty.atlas");
            }

            @Override
            public String getAnimationName() {
                return "GLUTY";
            }

            @Override
            public float getOffsetX() {
                return 250;
            }

            @Override
            public float getOffsetY() {
                return 50;
            }

            @Override
            public float getFrameDuration() {
                return 0.5f;
            }
        },
        TFLY {
            @Override
            public TextureAtlas getTextureAtlas() {
                return MediaManager.LOADING.get("data/loading/tfly.atlas");
            }

            @Override
            public String getAnimationName() {
                return "TFLY";
            }

            @Override
            public float getOffsetX() {
                return 229.5f;
            }

            @Override
            public float getOffsetY() {
                return 50;
            }

            @Override
            public float getFrameDuration() {
                return 1f;
            }
        };

        public abstract TextureAtlas getTextureAtlas();

        public abstract String getAnimationName();

        public abstract float getOffsetX();

        public abstract float getOffsetY();

        public static LoadingType random() {
            switch (MathUtils.random(2)) {
                case 0:
                    return LoadingType.TFLY;
                case 1:
                    return LoadingType.BANQUI;
                case 2:
                    return LoadingType.GLUTY;
            }
            return LoadingType.BANQUI;
        }

        public abstract float getFrameDuration();
    }

    public LoadingScreen(MediaManager nextMediaManager) {
        this.mediaManager = nextMediaManager;
    }

    private boolean loadComplete;

    @Override
    public void show() {
        loadComplete = false;

        MediaManager.LOADING.loadAssets();
        mediaManager.loadAssets();
    }

    private void onAssetsLoaded() {
        loadComplete = true;

        final LoadingType loadingType = LoadingType.random();
        textureAtlas = loadingType.getTextureAtlas();
        stage = new Stage(new StretchViewport(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT));

        Table table = new Table();
        table.setFillParent(true);

        Image image = new Image(textureAtlas.findRegion("background"));
        stage.addActor(image);

        final Animation<TextureRegion> animation = new Animation(loadingType.getFrameDuration(), textureAtlas.findRegions(loadingType.getAnimationName()));
        animation.setPlayMode(Animation.PlayMode.LOOP);
        table.add(new Actor() {
            private float timer = 0;

            @Override
            public void draw(Batch batch, float alpha) {
                timer += 0.2f;
                batch.draw(animation.getKeyFrame(timer, true), loadingType.getOffsetX(), loadingType.getOffsetY());
            }
        });

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.graphics.getGL20().glClearColor(1, 1, 1, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (!MediaManager.LOADING.update()) {
            return;
        }
        if (!loadComplete)
            onAssetsLoaded();

        if (loadingCounter < 0) {
            ScreenManager.INSTANCE.loaded();
        } else {
            stage.act(delta);
            stage.draw();

            loadingCounter -= delta;
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        textureAtlas.dispose();
        stage.dispose();
    }
}
