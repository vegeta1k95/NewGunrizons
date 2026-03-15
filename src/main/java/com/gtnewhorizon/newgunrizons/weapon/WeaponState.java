package com.gtnewhorizon.newgunrizons.weapon;

import com.gtnewhorizon.newgunrizons.network.TypeRegistry;
import com.gtnewhorizon.newgunrizons.state.ManagedState;

import lombok.Getter;

@Getter
public enum WeaponState implements ManagedState<WeaponState> {

    IDLE,
    RELOADING_START,
    RELOADING_ITERATION,
    RELOADING_ITERATION_COMPLETED,
    RELOADING_END,
    UNLOADING_PREPARING,
    UNLOADING(UNLOADING_PREPARING, IDLE),
    SHOOTING(9),
    RECOILED(10),
    MODIFYING(2),
    NEXT_ATTACHMENT(2),
    NO_AMMO;

    private final WeaponState preparingPhase;
    private final WeaponState commitPhase;
    private final int priority;

    WeaponState() {
        this(0, null, null);
    }

    WeaponState(int priority) {
        this(priority, null, null);
    }

    WeaponState(WeaponState preparingPhase, WeaponState commitPhase) {
        this(0, preparingPhase, commitPhase);
    }

    WeaponState(int priority, WeaponState preparingPhase, WeaponState commitPhase) {
        this.priority = priority;
        this.preparingPhase = preparingPhase;
        this.commitPhase = commitPhase;
    }

    static {
        TypeRegistry.getInstance()
            .register(WeaponState.class);
    }
}
