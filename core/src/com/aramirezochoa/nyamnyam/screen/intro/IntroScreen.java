package com.aramirezochoa.nyamnyam.screen.intro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.aramirezochoa.nyamnyam.Constant;
import com.aramirezochoa.nyamnyam.i18n.LanguageManager;
import com.aramirezochoa.nyamnyam.media.MediaManager;
import com.aramirezochoa.nyamnyam.screen.AbstractScreen;
import com.aramirezochoa.nyamnyam.screen.ScreenManager;
import com.aramirezochoa.nyamnyam.screen.ScreenType;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by leboheme on 10/02/15.
 */
public class IntroScreen extends AbstractScreen {

    private Stage stage;
    private BitmapFont font;

    public IntroScreen() {
        super(MediaManager.INTRO);
    }

    @Override
    public void show() {
        this.stage = new Stage(new StretchViewport(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT));

        TextureAtlas atlas = getMediaManager().get("data/intro/intro.atlas");
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 48;
        parameter.characters = Constant.NYAM_CHARS;
        font = ((FreeTypeFontGenerator) getMediaManager().get("data/intro/font.ttf")).generateFont(parameter);

        // First phrase
        final Label label = new Label(LanguageManager.INSTANCE.getString("firstPhrase"), new Label.LabelStyle(font, Color.WHITE));
        label.setAlignment(Align.center);
        label.addAction(Actions.sequence(Actions.fadeOut(0), Actions.show(), Actions.fadeIn(2f), Actions.delay(3f), Actions.fadeOut(2),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        ((EggAnimation) stage.getRoot().findActor("EGG")).setCrush();
                    }
                }), Actions.removeActor()));
        label.setX((Constant.SCREEN_WIDTH - label.getWidth()) / 2);
        label.setY(350f);
        label.setVisible(false);

        // Background
        Actor background = new Image(new TextureRegionDrawable(atlas.findRegion("background")));
        background.addAction(Actions.sequence(
                Actions.fadeOut(0),
                Actions.delay(5f),
                Actions.fadeIn(2f, Interpolation.circle),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        stage.addActor(label);
                        ((EggAnimation) stage.getRoot().findActor("EGG")).setPush();
                    }
                })
        ));
        stage.addActor(background);

        // Second phrase
        Label label2 = new Label(LanguageManager.INSTANCE.getString("secondPhrase"), new Label.LabelStyle(font, Color.WHITE));
        label2.setAlignment(Align.center);
        label2.setName("SECOND_PHRASE");
        label2.setX((Constant.SCREEN_WIDTH - label2.getWidth()) / 2);
        label2.setY(350f);
        label2.setVisible(false);
        stage.addActor(label2);

        // Initial egg
        Actor egg = new EggAnimation(atlas);
        egg.setName("EGG");
        stage.addActor(egg);

        // Nyam
        Actor nyam = new NyamAnimation(atlas);
        nyam.setName("NYAM");
        stage.addActor(nyam);
        nyam.setVisible(false);
    }

    @Override
    public void render(float delta) {
        Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        stage.act();
        stage.draw();

        if (Gdx.input.isTouched() || isBackJustPressed()) {
            ScreenManager.INSTANCE.changeTo(ScreenType.GAME);
        } else if (stage.getRoot().findActor("SECOND_PHRASE") == null) {
            Label label = new Label(LanguageManager.INSTANCE.getString("tapMessage"), new Label.LabelStyle(font, Color.WHITE));
            label.setAlignment(Align.center);
            label.addAction(Actions.forever(sequence(Actions.fadeOut(0.5f, Interpolation.fade), Actions.fadeIn(0.5f, Interpolation.fade))));
            label.setX((Constant.SCREEN_WIDTH - label.getWidth()) / 2);
            label.setY(350f);
            stage.addActor(label);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        font.dispose();
    }

    private static class EggAnimation extends Actor {

        private final TextureAtlas textureAtlas;
        private TextureRegion currentFrame;
        private float currentFrameTimer;
        private Animation<TextureRegion> currentAnimation;

        public EggAnimation(TextureAtlas atlas) {
            this.textureAtlas = atlas;
            currentFrame = atlas.findRegion("egg");
            currentAnimation = new Animation(0.1f, atlas.findRegions("egg"));
            setX(367.5f);
            setY(100);
            addAction(Actions.sequence(
                    Actions.fadeOut(0),
                    Actions.scaleBy(-0.75f, -0.75f),
                    Actions.parallel(
                            Actions.fadeIn(5f, Interpolation.fade),
                            Actions.scaleBy(0.75f, 0.75f, 5f, Interpolation.linear),
                            Actions.moveTo(264.5f, 0, 5f, Interpolation.linear)
                    )
            ));
        }


        @Override
        public void act(float delta) {
            super.act(delta);
            currentFrame = currentAnimation.getKeyFrame(currentFrameTimer);
            currentFrameTimer += delta;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

            if (currentFrame != null) {
                batch.draw(currentFrame, getX(), getY(), currentFrame.getRegionWidth() * getScaleX(), currentFrame.getRegionHeight() * getScaleY());
            }
        }

        public void setPush() {
            currentFrameTimer = 0;
            currentAnimation = new Animation(0.1f, textureAtlas.findRegions("push"));
            currentAnimation.setPlayMode(Animation.PlayMode.LOOP);
            MediaManager.INTRO.playSound(MediaManager.SOUND_INTRO_PUSH);
        }

        public void setCrush() {
            currentFrameTimer = 0;
            currentAnimation = new Animation(0.1f, textureAtlas.findRegions("crush"));
            currentAnimation.setPlayMode(Animation.PlayMode.NORMAL);
            addAction(Actions.sequence(Actions.delay(4), Actions.run(new Runnable() {
                @Override
                public void run() {
                    MediaManager.INTRO.playSound(MediaManager.SOUND_INTRO_NYAMNYAM);
                    ((NyamAnimation) getStage().getRoot().findActor("NYAM")).start();
                }
            })));
            MediaManager.INTRO.playSound(MediaManager.SOUND_INTRO_CRUSH);
        }
    }

    private static class NyamAnimation extends Actor {

        private TextureRegion currentFrame;

        public NyamAnimation(TextureAtlas atlas) {
            currentFrame = atlas.findRegion("nyam");
            setX(280);
            setY(125f);
            setScaleX(0.2f);
            setScaleY(0.2f);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

            if (currentFrame != null) {
                batch.draw(currentFrame, getX(), getY(), currentFrame.getRegionWidth() * getScaleX(), currentFrame.getRegionHeight() * getScaleY());
            }
        }

        public void start() {
            addAction(Actions.sequence(
                    Actions.show(),
                    Actions.parallel(
                            Actions.moveTo(-200, -120, 0.3f),
                            Actions.scaleTo(1, 1, 0.3f)
                    ),
                    Actions.hide(),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            getStage().getRoot().findActor("SECOND_PHRASE").addAction(
                                    Actions.sequence(Actions.fadeOut(0), Actions.show(), Actions.fadeIn(1f), Actions.delay(3f), Actions.fadeOut(2), Actions.removeActor()));

                        }
                    }),
                    Actions.removeActor()
            ));
        }
    }
}
