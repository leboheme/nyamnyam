package com.aramirezochoa.nyamnyam.screen.game.run;

/**
 * Tuning panel for the JSON prototype. Values are provisional; edit and rebuild to try them.
 * Distances use logical world pixels and times use seconds. Legacy TMX keeps its own constants.
 */
public final class PrototypeBalance {
    private PrototypeBalance() { }

    // Health is stored in half teeth: 1 unit = 0.5 tooth. Normal containers reserve whole spaces.
    public static final int STARTING_TOOTH_CONTAINERS = 3;
    public static final int MAX_TOOTH_SPACES = 8;
    public static final int BASIC_ENEMY_DAMAGE_HALVES = 1;
    public static final float PLAYER_HIT_INVULNERABILITY_SECONDS = 1f;
    // Death presentation advances for this long before the Game over overlay pauses the screen.
    public static final float PLAYER_DEFEAT_DELAY_SECONDS = 1.2f;
    // Provisional drop chances: evaluated once per remate, separately from common food.
    public static final float TOOTH_DROP_CHANCE = 0.2f;
    public static final float TOOTH_DROP_CARIES_CHANCE = 0.5f;
    public static final float TOOTH_DROP_HALF_CHANCE = 0.5f;
    public static final float TOOTH_PICKUP_SIZE = 16f;

    // Free room entities: px/s²; physics is applied centrally by RunPhysicsSystem.
    public static final float ROOM_FREE_ENTITY_GRAVITY = 650f;
    public static final float ROOM_PHYSICS_MAX_MOVEMENT_STEP = 4f;
    // Player contact parts free items at this speed (px/s), without blocking the player.
    public static final float FREE_CONTACT_SEPARATION_SPEED = 180f;
    // Visual drop launch: px/s; entities are created at landing.
    public static final float DROP_HORIZONTAL_SPEED = 240f;
    public static final float DROP_SECOND_UPWARD_SPEED_MULTIPLIER = 0.85f;
    public static final float DROP_UPWARD_SPEED = 360f;
    public static final float DROP_ROOM_RESTITUTION = 0.85f;
    // Spin uses degrees/s.
    public static final float DROP_SPIN_DEGREES_PER_SECOND = 720f;
    // Defeated body is visual only; it never delays drops or room completion.
    public static final float DEFEAT_BODY_SECONDS = 1.2f;
    public static final float DEFEAT_BODY_HORIZONTAL_SPEED = 180f;
    public static final float DEFEAT_BODY_UPWARD_SPEED = 320f;
    public static final float DEFEAT_BODY_GRAVITY = 600f;
    public static final float DEFEAT_BODY_SPIN_DEGREES_PER_SECOND = 720f;
    public static final float DEFEAT_BODY_FADE_SECONDS = 0.25f;

    // Bubble lifetime phases (also select the existing cold/warm/hot atlas animation).
    // Empty and occupied bubbles currently share these phases: total lifetime = their sum.
    public static final float BUBBLE_COLD_SECONDS = 5f;
    public static final float BUBBLE_WARM_SECONDS = 3f;
    public static final float BUBBLE_HOT_SECONDS = 2f;
    // Bubble movement: speeds in px/s; damping removes this fraction of impulse per second.
    public static final float BUBBLE_ATTRACTION_SPEED = 40f;
    public static final float BUBBLE_MAX_PUSH_SPEED = 200f;
    public static final float BUBBLE_IMPULSE_DAMPING_PER_SECOND = 6f;
    public static final float BUBBLE_MIN_UPWARD_PUSH_SPEED = 40f;
    public static final float BUBBLE_SIDE_PUSH_MULTIPLIER = 1.5f;
    public static final float BUBBLE_GROUP_SEPARATION_SPEED = 60f;

    // Pressure is an accumulated resource, not a direct contact timer.
    // Net contact rate = gain - relaxation: 1/s freely, 4/s against terrain.
    // With the default threshold this bursts after ~0.65s freely or ~0.16s blocked.
    public static final float BUBBLE_PRESSURE_THRESHOLD = 0.65f;
    public static final float BUBBLE_PRESSURE_RELAXATION_PER_SECOND = 1f;
    public static final float BUBBLE_PRESSURE_GAIN_PER_SECOND = 2f;
    public static final float BUBBLE_BLOCKED_PRESSURE_GAIN_PER_SECOND = 5f;
    // Relative impact speed in px/s that bursts immediately, without waiting for pressure.
    public static final float BUBBLE_BURST_IMPACT_SPEED = 280f;

    // Contact margin in px, shared by player contact, grouping and chain propagation.
    public static final float BUBBLE_CONTACT_MARGIN = 0.25f;

    // Collision solver tolerances, not balance controls. Change with care.
    public static final float BUBBLE_TERRAIN_EDGE_EPSILON = 0.001f;
    public static final float BUBBLE_COINCIDENT_CENTER_EPSILON = 0.001f;
    // Probe distance in px used to detect whether a pushed group is blocked.
    public static final float BUBBLE_BLOCKED_PROBE_DISTANCE = 2f;
    public static final float BUBBLE_MIN_SIDE_INPUT_SPEED = 1f;
}
