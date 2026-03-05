package com.gtnewhorizon.newgunrizons.client.animation;

import java.util.Queue;

import com.gtnewhorizon.newgunrizons.attachment.Part;
import com.gtnewhorizon.newgunrizons.client.render.RenderContext;

/**
 * Represents a positioning snapshot in the animation queue.
 * Can be either a static hold (final pose) or a transitioning blend
 * between two states.
 */
public interface MultipartPositioning {

    <T> T getFromState(Class<T> stateClass);

    <T> T getToState(Class<T> stateClass);

    boolean isExpired(Queue<MultipartPositioning> queue);

    Positioner getPositioner();

    float getProgress();

    /**
     * Applies per-part GL transforms for the current animation frame.
     * Also controls idle sway amplitude/rate.
     */
    interface Positioner {

        void position(Part part, RenderContext context);

        default void applySway(float rate, float amplitude) {}
    }
}
