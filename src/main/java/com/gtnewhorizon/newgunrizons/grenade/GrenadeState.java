package com.gtnewhorizon.newgunrizons.grenade;

import com.gtnewhorizon.newgunrizons.network.TypeRegistry;
import com.gtnewhorizon.newgunrizons.state.ManagedState;

import lombok.Getter;

@Getter
public enum GrenadeState implements ManagedState<GrenadeState> {

    READY,
    SAFETY_PIN_OFF(9),
    STRIKER_LEVER_RELEASED(9),
    THROWING(9),
    THROWN(9),
    EXPLODED_IN_HANDS(9);

    private final GrenadeState preparingPhase;
    private final GrenadeState commitPhase;
    private final int priority;

    GrenadeState() {
        this(0, null, null);
    }

    GrenadeState(int priority) {
        this(priority, null, null);
    }

    GrenadeState(int priority, GrenadeState preparingPhase, GrenadeState commitPhase) {
        this.priority = priority;
        this.preparingPhase = preparingPhase;
        this.commitPhase = commitPhase;
    }

    static {
        TypeRegistry.getInstance()
            .register(GrenadeState.class);
    }
}
