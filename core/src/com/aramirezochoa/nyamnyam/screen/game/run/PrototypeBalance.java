package com.aramirezochoa.nyamnyam.screen.game.run;

/**
 * Tuning panel for the JSON prototype. Values are provisional; edit and rebuild to try them.
 * Distances use logical world pixels and times use seconds. Legacy TMX keeps its own constants.
 */
public final class PrototypeBalance {
    private PrototypeBalance() { }

    // Salivation: starts full, regenerates continuously, including while attacking.
    public static final float SALIVATION_CAPACITY = 6f;
    public static final float SALIVATION_BUBBLE_COST = 1f;
    public static final float SALIVATION_REGENERATION_PER_SECOND = 0.75f;

    // Combat: ceil(resistance / power) impacts to capture; capture itself is not a defeat.
    public static final float ENEMY_INITIAL_RESISTANCE = 3f;
    public static final float ATTACK_POWER = 1f;
    public static final float ENEMY_HIT_FEEDBACK_SECONDS = 0.12f;

    // Common food: variants in each family restore the same amount, capped at capacity.
    public static final float FRUIT_SALIVATION_RESTORE = 2f;
    public static final float VERDURE_SALIVATION_RESTORE = 1f;
    // Drop launch: px/s, px/s²; pickup delay only prevents instant collection at spawn.
    public static final float DROP_HORIZONTAL_SPEED = 240f;
    public static final float DROP_SECOND_UPWARD_SPEED_MULTIPLIER = 0.85f;
    public static final float DROP_UPWARD_SPEED = 360f;
    public static final float DROP_GRAVITY = 650f;
    public static final float DROP_ROOM_RESTITUTION = 0.85f;
    public static final float DROP_PICKUP_DELAY_SECONDS = 0.15f;
    // Spin uses degrees/s. Movement substeps keep landing accurate at high launch speeds.
    public static final float DROP_MAX_MOVEMENT_STEP = 4f;
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
