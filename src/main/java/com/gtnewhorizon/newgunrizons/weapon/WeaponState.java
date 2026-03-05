package com.gtnewhorizon.newgunrizons.weapon;

import com.gtnewhorizon.newgunrizons.network.TypeRegistry;
import com.gtnewhorizon.newgunrizons.state.ManagedState;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

public enum WeaponState implements ManagedState<WeaponState> {

    READY(false),
    LOAD(true),
    LOAD_ITERATION,
    LOAD_ITERATION_COMPLETED,
    ALL_LOAD_ITERATIONS_COMPLETED,
    UNLOAD_PREPARING,
    UNLOAD(UNLOAD_PREPARING, READY, true),
    FIRING(9),
    RECOILED(10),
    PAUSED(10),
    EJECT_REQUIRED,
    EJECTING,
    MODIFYING(2, false),
    NEXT_ATTACHMENT(2, false),
    ALERT;

    private final WeaponState preparingPhase;
    private final WeaponState commitPhase;
    @Getter
    private final boolean isTransient;
    @Getter
    private final int priority;

    WeaponState() {
        this(0, null, null, true);
    }

    WeaponState(int priority) {
        this(priority, null, null, true);
    }

    WeaponState(boolean isTransient) {
        this(0, null, null, isTransient);
    }

    WeaponState(int priority, boolean isTransient) {
        this(priority, null, null, isTransient);
    }

    WeaponState(WeaponState preparingPhase, WeaponState commitPhase, boolean isTransient) {
        this(0, preparingPhase, commitPhase, isTransient);
    }

    WeaponState(int priority, WeaponState preparingPhase, WeaponState commitPhase, boolean isTransient) {
        this.priority = priority;
        this.preparingPhase = preparingPhase;
        this.commitPhase = commitPhase;
        this.isTransient = false;
    }

    public WeaponState getPreparingPhase() {
        return this.preparingPhase;
    }

    public WeaponState getCommitPhase() {
        return this.commitPhase;
    }

    public void init(ByteBuf buf) {}

    public void serialize(ByteBuf buf) {}

    static {
        TypeRegistry.getInstance()
            .register(WeaponState.class);
    }
}
