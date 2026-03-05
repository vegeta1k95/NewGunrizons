package com.gtnewhorizon.newgunrizons.client.animation;

import java.util.List;

import com.gtnewhorizon.newgunrizons.state.RenderableState;

/**
 * Provides the list of {@link MultipartTransition}s that define the animation
 * keyframes for a given {@link RenderableState}. Implemented by weapon and
 * grenade renderers to supply their per-state positioning data.
 */
public interface MultipartTransitionProvider {

    List<MultipartTransition> getPositioning(RenderableState state);
}
