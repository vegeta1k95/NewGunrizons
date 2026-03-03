package com.vicmatskiv.weaponlib.state;

import com.vicmatskiv.weaponlib.network.UniversallySerializable;

public interface ManagedState<T extends ManagedState<T>> extends UniversallySerializable {
    default T preparingPhase() {
        return null;
    }
    default T permitRequestedPhase() {
        return null;
    }
    default T commitPhase() {
        return null;
    }
}
