package com.aramirezochoa.nyamnyam.media;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.aramirezochoa.nyamnyam.DataManager;
import com.aramirezochoa.nyamnyam.input.InputManager;
import com.aramirezochoa.nyamnyam.screen.menu.MenuAction;
import com.badlogic.gdx.utils.Align;

/**
 * Created by boheme on 12/01/15.
 */
public enum MediaManager {
    MENU(false) {
        @Override
        public void loadAssets() {
            manager.load("data/menu/menu.atlas", TextureAtlas.class);
            manager.load("data/menu/font.ttf", FreeTypeFontGenerator.class);

            manager.load(THEME_CHEWING, Music.class);
            manager.load(SOUND_BUTTON, Sound.class);
            manager.load(SOUND_INTRO_NYAMNYAM, Sound.class);
            manager.load(SOUND_LIVE, Sound.class);
            manager.load(SOUND_NYAM_BOOST, Sound.class);
        }
    },
    GAME {
        @Override
        public void loadAssets() {
            manager.load("data/game/game.atlas", TextureAtlas.class);
            manager.load("data/game/background.atlas", TextureAtlas.class);
            manager.load("data/game/gui.atlas", TextureAtlas.class);
            manager.load("data/game/font.ttf", FreeTypeFontGenerator.class);

            manager.load(THEME_NORMAL, Music.class);
            manager.load(THEME_BOSS, Music.class);

            manager.load(SOUND_JUMP, Sound.class);
            manager.load(SOUND_SHOT, Sound.class);
            manager.load(SOUND_SHOT_FIRE, Sound.class);
            manager.load(SOUND_LIVE, Sound.class);
            manager.load(SOUND_NYAM_CANDY, Sound.class);
            manager.load(SOUND_NYAM_BOOST, Sound.class);
            manager.load(SOUND_NYAM_MEAL, Sound.class);
            manager.load(SOUND_HIT_PROJECTILE, Sound.class);
            manager.load(SOUND_NYAM_HIT, Sound.class);
            manager.load(SOUND_HURRY, Sound.class);
            manager.load(SOUND_DARK, Sound.class);
            manager.load(SOUND_GLUTY_WHIRL, Sound.class);
            manager.load(SOUND_EXPLOSION, Sound.class);
        }

        @Override
        public void stopAllThemes() {
            stopTheme(THEME_NORMAL);
            stopTheme(THEME_BOSS);
        }
    }, INTRO {
        @Override
        public void loadAssets() {
            manager.load("data/intro/intro.atlas", TextureAtlas.class);
            manager.load("data/intro/font.ttf", FreeTypeFontGenerator.class);
            manager.load(SOUND_INTRO_PUSH, Sound.class);
            manager.load(SOUND_INTRO_CRUSH, Sound.class);
            manager.load(SOUND_INTRO_NYAMNYAM, Sound.class);
        }
    },
    END {
        @Override
        public void loadAssets() {
            manager.load("data/end/end.atlas", TextureAtlas.class);
            manager.load("data/end/font.ttf", FreeTypeFontGenerator.class);
        }
    },
    HELP {
        @Override
        public void loadAssets() {
            manager.load("data/help/help.atlas", TextureAtlas.class);
            manager.load("data/help/font.ttf", FreeTypeFontGenerator.class);
        }
    },
    STORE {
        @Override
        public void loadAssets() {
            manager.load("data/store/store.atlas", TextureAtlas.class);
            manager.load("data/store/font.ttf", FreeTypeFontGenerator.class);
        }
    };

    private static AssetManager manager;

    public static String THEME_CHEWING = "data/sounds/chewing.ogg";

    public static String THEME_NORMAL = "data/sounds/stage_normal.ogg";
    public static String THEME_BOSS = "data/sounds/stage_boss.ogg";

    public static String SOUND_JUMP = "data/sounds/jump.ogg";
    public static String SOUND_SHOT = "data/sounds/shot.ogg";
    public static String SOUND_SHOT_FIRE = "data/sounds/shot_fire.ogg";
    public static String SOUND_LIVE = "data/sounds/live.ogg";
    public static String SOUND_NYAM_CANDY = "data/sounds/nyam_candy.ogg";
    public static String SOUND_NYAM_BOOST = "data/sounds/nyam_boost.ogg";
    public static String SOUND_NYAM_MEAL = "data/sounds/nyam_meal.ogg";
    public static String SOUND_HIT_PROJECTILE = "data/sounds/hit_projectile.ogg";
    public static String SOUND_NYAM_HIT = "data/sounds/nyam_hit.ogg";
    public static String SOUND_HURRY = "data/sounds/hurry.ogg";
    public static String SOUND_DARK = "data/sounds/dark.ogg";
    public static String SOUND_GLUTY_WHIRL = "data/sounds/gluty_whirl.ogg";
    public static String SOUND_EXPLOSION = "data/sounds/explosion.ogg";

    public static String SOUND_INTRO_PUSH = "data/sounds/push.ogg";
    public static String SOUND_INTRO_CRUSH = "data/sounds/crush.ogg";
    public static String SOUND_INTRO_NYAMNYAM = "data/sounds/nyamnyam.ogg";

    public static String SOUND_BUTTON = "data/sounds/button.ogg";
    private static float VOLUME = 0.1f;
    private static final String SOUND = "sounds";

    private static Boolean soundEnabled = true;
    private boolean interactionStarted;

    MediaManager() {
        interactionStarted = true;
    }

    MediaManager(boolean interactionStarted) {
        this.interactionStarted = interactionStarted;
    }

    public static void init() {
        manager = new AssetManager();
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        Preferences prefs = Gdx.app.getPreferences(DataManager.COMMON_DATA);
        if (!prefs.contains(SOUND)) {
            prefs.putBoolean(SOUND, true);
            prefs.flush();
        }
        soundEnabled = prefs.getBoolean(SOUND);
    }

    public abstract void loadAssets();

    public boolean update() {
        return manager.update();
    }

    public void unloadAssets() {
        manager.clear();
    }

    public static TextButton.TextButtonStyle createTextButtonStyle(String name, TextureAtlas atlas, BitmapFont font) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(atlas.findRegion(name, 0));
        buttonStyle.down = new TextureRegionDrawable(atlas.findRegion(name, 1));
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.valueOf("515151");
        return buttonStyle;
    }

    public static TextButton createTextButton(String text, TextButton.TextButtonStyle textButtonStyle, final MenuAction menuAction) {
        TextButton button = new TextButton(text, textButtonStyle);
        button.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                InputManager.INSTANCE.notifyMenuAction(menuAction);
            }
        });
        return button;
    }

    public static Button.ButtonStyle createButtonStyle(String name, TextureAtlas atlas) {
        Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(atlas.findRegion(name, 0));
        buttonStyle.down = new TextureRegionDrawable(atlas.findRegion(name, 1));
        return buttonStyle;
    }

    public static Button createButton(Button.ButtonStyle buttonStyle, final MenuAction menuAction) {
        Button button = new Button(buttonStyle);
        button.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                InputManager.INSTANCE.notifyMenuAction(menuAction);
            }
        });
        return button;
    }

    public static CheckBox.CheckBoxStyle createCheckBoxStyle(String name, TextureAtlas textureAtlas, BitmapFont bitmapFont) {
        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.font = bitmapFont;
        checkBoxStyle.fontColor = Color.BLACK;
        checkBoxStyle.checkboxOn = new TextureRegionDrawable(textureAtlas.findRegion(name, 0));
        checkBoxStyle.checkboxOff = new TextureRegionDrawable(textureAtlas.findRegion(name, 1));
        return checkBoxStyle;
    }

    // sound checkbox
    public static CheckBox createCheckBox(CheckBox.CheckBoxStyle checkBoxStyle) {
        final CheckBox checkBox = new CheckBox("", checkBoxStyle);
        checkBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                soundEnabled = checkBox.isChecked();
                persistSoundOption();
            }
        });
        checkBox.align(Align.left);
        checkBox.setChecked(soundEnabled);
        return checkBox;
    }

    private static void persistSoundOption() {
        Preferences prefs = Gdx.app.getPreferences(DataManager.COMMON_DATA);
        prefs.remove(SOUND);
        prefs.putBoolean(SOUND, soundEnabled);
        prefs.flush();
    }

    public <T> T get(String s) {
        return manager.get(s);
    }

    public void interactionStarted() {
        interactionStarted = true;
    }

    public void playSound(String name) {
        if (interactionStarted && soundEnabled) {
            ((Sound) get(name)).play(VOLUME);
        }
    }

    public void playTheme(String theme) {
        if (interactionStarted && soundEnabled) {
            if (!((Music) get(theme)).isPlaying()) {
                ((Music) get(theme)).setVolume(VOLUME);
                ((Music) get(theme)).setLooping(true);
                ((Music) get(theme)).play();
            }
        } else {
            if (((Music) get(theme)).isPlaying()) {
                ((Music) get(theme)).stop();
            }
        }
    }

    public void pauseTheme(String theme) {
        if (((Music) get(theme)).isPlaying()) {
            ((Music) get(theme)).pause();
        }
    }

    public void stopAllThemes() {

    }

    public void stopTheme(String theme) {
        if (((Music) get(theme)).isPlaying()) {
            ((Music) get(theme)).stop();
        }
    }
}