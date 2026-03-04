package com.gtnewhorizon.newgunrizons.state;

/**
 * Simplified visual states used by the rendering system.
 * <p>
 * Maps complex internal states ({@code WeaponState}, {@code GrenadeState}) to a smaller
 * set of animation states. The mapping is performed by {@code WeaponRenderer.getStateDescriptor()}
 * and {@code GrenadeRenderer.getStateDescriptor()}.
 */
public enum RenderableState {

    // --- Base states ---
    NORMAL,
    RUNNING,

    // --- Aiming ---
    ZOOMING,

    // --- Firing ---
    SHOOTING,
    AUTO_SHOOTING,
    RECOILED,
    ZOOMING_SHOOTING,
    ZOOMING_RECOILED,

    // --- Reload ---
    RELOADING,
    UNLOADING,
    LOAD_ITERATION,
    LOAD_ITERATION_COMPLETED,
    ALL_LOAD_ITERATIONS_COMPLETED,

    // --- Mechanical ---
    EJECT_SPENT_ROUND,

    // --- Attachment mode ---
    MODIFYING,

    // --- Grenade ---
    SAFETY_PIN_OFF,
    STRIKER_LEVER_OFF,
    THROWING,
    THROWN
}
