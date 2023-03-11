package com.aramirezochoa.nyamnyam.screen.end;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
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

/**
 * Created by leboheme on 18/02/15.
 */
public class EndScreen extends AbstractScreen {

    private Stage stage;
    private BitmapFont font;

    public EndScreen() {
        super(MediaManager.END);
    }

    @Override
    public void show() {
        this.stage = new Stage(new StretchViewport(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT));

        TextureAtlas atlas = getMediaManager().get("data/end/end.atlas");
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        parameter.characters = Constant.NYAM_CHARS;
        font = ((FreeTypeFontGenerator) getMediaManager().get("data/end/font.ttf")).generateFont(parameter);

        // Background
        Actor background = new Image(new TextureRegionDrawable(atlas.findRegion("background")));
        stage.addActor(background);

        // Nyam
        Actor nyam = new NyamAvatar(atlas);
        stage.addActor(nyam);

        // Gluty & Tfly
        Actor glutyTfly = new GlutyTflyAnimation(atlas);
        stage.addActor(glutyTfly);

        Label label = new Label(LanguageManager.INSTANCE.getString("mmm"), new Label.LabelStyle(font, Color.WHITE));
        label.setName("title");
        label.setAlignment(Align.center);
        label.setVisible(false);
        label.addAction(Actions.sequence(Actions.fadeOut(0), Actions.visible(true), Actions.fadeIn(2f, Interpolation.fade), Actions.delay(4f)));
        label.setX((Constant.SCREEN_WIDTH - label.getWidth()) / 2);
        label.setY(310);
        stage.addActor(label);
    }

    @Override
    public void render(float delta) {
        Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        stage.act();
        stage.draw();

        if (stage.getRoot().findActor("title").getActions().size == 0 && (Gdx.input.isTouched() || isBackJustPressed())) {
            ScreenManager.INSTANCE.changeTo(ScreenType.MENU);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        font.dispose();
    }

    private static class NyamAvatar extends Actor {

        private TextureRegion currentFrame;
        private float currentFrameTimer;
        private Animation<TextureRegion> idleAnimation, nyamAnimation;
        private boolean nyamStatus;

        public NyamAvatar(TextureAtlas atlas) {
            idleAnimation = new Animation(0.1f, atlas.findRegions("idle"));
            idleAnimation.setPlayMode(Animation.PlayMode.NORMAL);
            nyamAnimation = new Animation(0.1f, atlas.findRegions("nyam"));
            nyamAnimation.setPlayMode(Animation.PlayMode.NORMAL);
            currentFrame = idleAnimation.getKeyFrame(0);
            setX(205);
            setY(35);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (nyamStatus) {
                if (nyamAnimation.isAnimationFinished(currentFrameTimer)) {
                    nyamStatus = false;
                    currentFrameTimer = 0;
                    currentFrame = idleAnimation.getKeyFrame(0);
                } else {
                    currentFrame = nyamAnimation.getKeyFrame(currentFrameTimer);
                }
            } else {
                if (idleAnimation.isAnimationFinished(currentFrameTimer)) {
                    if (MathUtils.random(3) == 1) {
                        nyamStatus = true;
                        currentFrameTimer = 0;
                        currentFrame = nyamAnimation.getKeyFrame(0);
                    } else {
                        currentFrameTimer = 0;
                        currentFrame = idleAnimation.getKeyFrame(0);
                    }
                } else {
                    currentFrame = idleAnimation.getKeyFrame(currentFrameTimer);
                }
            }
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

    }

    private static class GlutyTflyAnimation extends Actor {

        private TextureRegion currentFrame;
        private float currentFrameTimer;
        private Animation<TextureRegion> currentAnimation;
        private boolean left = false;

        public GlutyTflyAnimation(TextureAtlas atlas) {
            currentAnimation = new Animation(0.075f, atlas.findRegions("gluty"));
            currentAnimation.setPlayMode(Animation.PlayMode.LOOP);
            currentFrame = currentAnimation.getKeyFrame(0);
            setX(1200);
            setY(30);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            currentFrame = currentAnimation.getKeyFrame(currentFrameTimer);
            currentFrameTimer += delta;
            if (getX() >= 1200 && !left) {
                getActions().clear();
                addAction(Actions.moveTo(-750, 0, 2.75f));
                left = true;
            } else if (getX() <= -750 && left) {
                getActions().clear();
                addAction(Actions.moveTo(1200, 0, 2.75f));
                left = false;
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            if (currentFrame != null) {
                currentFrame.flip(!left, false);
                batch.draw(currentFrame, getX(), getY(), currentFrame.getRegionWidth() * getScaleX(), currentFrame.getRegionHeight() * getScaleY());
                currentFrame.flip(!left, false);
            }
        }

    }
}
