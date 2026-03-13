package com.gtnewhorizon.newgunrizons.client.animation;

import java.util.HashMap;
import java.util.Map;

import com.gtnewhorizon.newgunrizons.client.animation.BedrockAnimation.AnimationClip;
import com.gtnewhorizon.newgunrizons.model.JsonModel;
import com.gtnewhorizon.newgunrizons.state.RenderableState;

/**
 * Maps {@link RenderableState}s to Bedrock animation clips and manages
 * playback lifecycle. Used by {@code WeaponRenderer} to automatically play
 * bone-level animations when the weapon state machine changes state.
 */
public class BedrockAnimationController {

    private final BedrockAnimation animation;
    private final Map<RenderableState, String> stateClipMap = new HashMap<>();
    private BedrockAnimationPlayer currentPlayer;
    private RenderableState currentAnimState;

    public BedrockAnimationController(BedrockAnimation animation) {
        this.animation = animation;
    }

    /**
     * Maps a renderable state to a named animation clip.
     */
    public void mapState(RenderableState state, String clipName) {
        stateClipMap.put(state, clipName);
    }

    /**
     * Called each frame with the current weapon renderable state.
     * Starts a new animation if the state changed and has a mapped clip.
     */
    public void onStateChanged(RenderableState newState) {
        if (newState == currentAnimState) return;
        currentAnimState = newState;

        String clipName = stateClipMap.get(newState);
        if (clipName != null) {
            AnimationClip clip = animation.getClip(clipName);
            if (clip != null) {
                currentPlayer = new BedrockAnimationPlayer(clip);
                currentPlayer.start();
                return;
            }
        }
        // No animation for this state — stop current
        currentPlayer = null;
    }

    /**
     * Applies the current animation's bone transforms to the model.
     * Call before {@code model.render()}.
     */
    public void applyToModel(JsonModel model) {
        if (currentPlayer != null && currentPlayer.isPlaying()) {
            currentPlayer.applyToModel(model);
        }
    }

    /**
     * Resets all bones to rest pose after rendering.
     * Call after {@code model.render()}.
     */
    public void resetModel(JsonModel model) {
        if (currentPlayer != null) {
            model.resetBonesToRestPose();
        }
    }

    /**
     * Returns the animation duration in milliseconds for the given state, or -1 if no animation is mapped.
     */
    public long getAnimationDurationMs(RenderableState state) {
        String clipName = stateClipMap.get(state);
        if (clipName == null) return -1;
        AnimationClip clip = animation.getClip(clipName);
        if (clip == null) return -1;
        return (long) (clip.length * 1000f);
    }

    /**
     * Returns true if there's an animation mapped for this state.
     */
    public boolean hasAnimation(RenderableState state) {
        return stateClipMap.containsKey(state);
    }

    /**
     * Returns the current animation player, or null if none is active.
     */
    public BedrockAnimationPlayer getCurrentPlayer() {
        return currentPlayer;
    }
}
