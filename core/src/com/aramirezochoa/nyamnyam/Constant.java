package com.aramirezochoa.nyamnyam;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by boheme on 12/01/15.
 */
public class Constant {

    public static final Vector2 VECTOR_16 = new Vector2(16, 16);
    public static final Vector2 VECTOR_24 = new Vector2(24, 24);
    public static final Vector2 VECTOR_32 = new Vector2(32, 32);

    public static final java.lang.String NYAM_CHARS = FreeTypeFontGenerator.DEFAULT_CHARS + "€’";

    public static final float SCREEN_WIDTH = 800;
    public static final float SCREEN_HEIGHT = 480;
    public static final float SCREEN_GAME_Y_OFFSET = -80f;
    public static final int TILE_SIZE = 16;

    // Game
    public static final Vector2 GRAVITY = new Vector2(0, -16.0f);
    public static final float GAME_MAX_LAG = 0.02f;
    public static final float GAME_BUTTON_PAD_WIDTH = 87f;
    public static final float GAME_BUTTON_PAD_HEIGHT = 150f;
    public static final float MAX_NEGATIVE_VELOCITY = -200f;
    public static final float DAMPING = 0.5f;
    public static final float GAME_WIN_FREE_TIME = 4f;

    public static final int GAME_NEW_GAME_LIVES = 3;
    public static final long GAME_FREE_LIVE_TIMER = 900000;
    public static final int GAME_LEVELS = 60;
    public static final float GAME_LEVEL_HURRY_TIME = 60;
    public static final float GAME_LEVEL_DARK_TIME = 75;

    // Entity
    public static final float ENTITY_BOX_OFFSET = 5f;
    public static final float BOSS_BOX_OFFSET = 15f;

    // Avatar
    public static final Vector2 AVATAR_DIMENSION = VECTOR_32;
    public static final float AVATAR_WALK_VELOCITY = 150f;
    public static final float AVATAR_JUMP_VELOCITY = 370f;

    // Main avatars
    public static final Vector2 MAIN_AVATAR_SPAWN_POSITION = new Vector2(48, 16);
    public static final float MAIN_AVATAR_TIME_DEAD = 2f;
    public static final float MAIN_AVATAR_TIME_SPAWN = 1f;
    public static final float MAIN_AVATAR_FIRE_TIME_LAPSE = 0.75f;
    public static final float MAIN_AVATAR_TIME_GARLIC_BOOST = 20f;
    public static final float MAIN_AVATAR_BOOST_MOVEMENT = 1.25f;
    public static final float MAIN_AVATAR_BOOST_FIRE_VELOCITY = 2f;
    public static final float MAIN_AVATAR_BOOST_FIRE_DISTANCE = 1.75f;
    public static final float MAIN_AVATAR_BOOST_FIRE_RATE = 2f;
    public static final int MAIN_AVATAR_BOOST_LIVES = 3;

    public static final float GLUTY_WHIRL_TIME = 0.2f;
    public static final float GLUTY_BOOST_FIRE_DISTANCE = 1.3f;

    public static final Vector2 TFLY_BOMB_VELOCITY = new Vector2(60, 150);
    public static final float TFLY_BOMB_TIMER = 1f;
    public static final float TFLY_EXPLOSION_TIMER = 0.2f;
    public static final float TFLY_BOMB_EXPLOSION_WIDTH = 96;
    public static final float TFLY_BOMB_BIG_EXPLOSION_WIDTH = 160;

    // Enemies
    public static final float ENEMY_WALK_VELOCITY = 75f;
    public static final Color ENEMY_ANGRY_COLOR = new Color(1, 0.25f, 0.25f, 1);
    public static final Color ENEMY_SCARED_COLOR = new Color(0.5f, 0.5f, 1f, 1);
    public static final float RINOS_WALK_VELOCITY = 50f;
    public static final float LITO_WALK_VELOCITY = 60f;
    public static final float ENEMY_FIRE_RATE = 2f;
    public static final float MBER_WALK_VELOCITY = 30f;
    public static final float BANQUI_WALK_VELOCITY = 75f;
    public static final float BANQUI_JUMP_VELOCITY = 320f;
    public static final Vector2 PHANT_DIMENSION = new Vector2(28, 28);

    public static final Vector2 BOSS_DIMENSION = new Vector2(96f, 96f);
    public static final Vector2 SUPER_BOSS_DIMENSION = new Vector2(128f, 128f);
    public static final float BOSS_VELOCITY = 100f;
    public static final int BOSS_BOOF_LIVES = 30;
    public static final int BOSS_FOLL_LIVES = 40;
    public static final int BOSS_BANQUI_LIVES = 50;
    public static final int BOSS_NYAM_LIVES = 80;
    public static final int BOSS_MODE_0 = 0;
    public static final int BOSS_MODE_1 = 1;
    public static final int BOSS_MODE_2 = 2;

    // Meal
    public static final float AIR_GUIDE_HALF_WIDTH = 5f;
    public static final float AIR_SPAWN_RATE = 3f;
    public static final float MEAL_RAISE_TIME_1 = 5f;
    public static final float MEAL_RAISE_TIME_2 = 3f;
    public static final float MEAL_RAISE_TIME_3 = 2f;

    public static final float EXPLODE_TIME = 0.1f;
    public static final float MEAL_EXPIRATION_TIME = 10f;
    public static final float MEAL_SPAWN_DELAY = 7f;
    public static final float MEAL_RAISE_VELOCITY = 40f;

    public static final float BEAM_DISTANCE = 200f;

    public static final Vector2 CANDY_DIMENSION = VECTOR_32;
    public static final float CANDY_TIME_TO_LOOSEN = 5f;
    public static final float CANDY_VELOCITY = 40f;

    public static final float VERDURE_DAMPING = 0.97f;
    public static final float FRUIT_DAMPING = 0.97f;

    // Scores
    public static final int SCORE_AIR = 5;
    public static final int SCORE_AIR_TORNADO = 10;
    public static final int SCORE_AIR_SPIKES = 10;
    public static final int SCORE_CANDY = 40;
    public static final int SCORE_VERDURE = 20;
    public static final int SCORE_FRUIT = 80;
    public static final int SCORE_BOOST = 50;
    public static final int SCORE_ENEMY = 50;

    public static final int SCORE_INTERVAL_EXTRA_LIFE = 5000;

    // Projectiles
    public static final Vector2 BEAM_DIMENSION = VECTOR_32;
    public static final float BEAM_VELOCITY = 250f;
    public static final Vector2 HORN_DIMENSION = VECTOR_16;
    public static final float HORN_VELOCITY = -100f;
    public static final Vector2 ROCK_DIMENSION = VECTOR_24;
    public static final Vector2 ROCK_VELOCITY = new Vector2(100f, 250f);
    public static final Vector2 ENERGY_DIMENSION = VECTOR_24;
    public static final float ENERGY_VELOCITY = 175f;
    public static final float PROJECTILE_DAMPING = 0.99f;
    public static final Vector2 ACID_DIMENSION = VECTOR_24;
    public static final float ACID_VELOCITY = -200f;
    public static final Vector2 TORNADO_DIMENSION = VECTOR_32;
    public static final float TORNADO_VELOCITY = 400f;
    public static final float SPIKE_BALL_VELOCITY = -100f;
    public static final Vector2 SPIKE_BALL_DIMENSION = VECTOR_32;
    public static final Vector2 SPIKE_DIMENSION = VECTOR_16;
    public static final int SPIKE_MAX_GERMINATIONS = 5;
    public static final float SPIKE_GERMINATION_RATE = 0.1f;
    public static final float SPIKE_LIFE_DURATION = 3f;
    public static final float BIG_FIRE_VELOCITY = 200f;

    public static final Vector2 DARK_SPAWN_POSITION = new Vector2(50, 300);
    public static final float DARK_TIME_SPAWN = 0.5f;

    // Pets
    public static float TFLY_MAX_OFFSET = 50f;
    public static float TFLY_MOVE = 5f;
    public static final float GLUTY_VELOCITY = 250f;

    public static class Coordinates {
        public float x, y;

        public Coordinates(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
