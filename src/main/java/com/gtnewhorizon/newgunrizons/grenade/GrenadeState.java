package com.gtnewhorizon.newgunrizons.grenade;

import com.gtnewhorizon.newgunrizons.state.ManagedState;

public enum GrenadeState implements ManagedState {

    IDLE,
    SAFETY_PIN_OFF,
    STRIKER_LEVER_RELEASED,
    THROWING,
    THROWN,
    EXPLODED_IN_HANDS;
}
