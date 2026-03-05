package com.gtnewhorizon.newgunrizons.state;

/**
 * Interface for entities that hold and manage a current {@link ManagedState}.
 * <p>
 * The single implementation is {@code PlayerItemInstance}, which tracks the state
 * of a weapon, magazine, or grenade held by a player.
 *
 * @param <T> the state enum type
 */
public interface ExtendedState<T extends ManagedState<T>> {

    /** Updates the current state. Returns true if the state actually changed. */
    boolean setState(T state);

    /** Returns the current state. */
    T getState();

    /** Returns the timestamp (millis) when the state was last updated. */
    long getStateUpdateTimestamp();

}
