package com.vicmatskiv.weaponlib.animation;

import java.util.function.Consumer;

import com.vicmatskiv.weaponlib.Part;
import com.vicmatskiv.weaponlib.RenderContext;

import lombok.Getter;

@Getter
public class Transition {

    private static final Consumer<RenderContext> ANCHORED_POSITION = (c) -> {};
    private final Consumer<RenderContext> itemPositioning;
    private final long duration;
    private final long pause;
    private final Part attachedTo;
    private boolean animated;

    public static boolean isAnchored(Consumer<?> consumer) {
        return consumer == ANCHORED_POSITION;
    }

    public Transition(Consumer<RenderContext> itemPositioning, boolean animated) {
        this(itemPositioning, 0L, 0L);
        this.animated = animated;
    }

    public Transition(Consumer<RenderContext> itemPositioning, long duration) {
        this(itemPositioning, duration, 0L);
    }

    public Transition(Consumer<RenderContext> itemPositioning, long duration, long pause) {
        this(itemPositioning, duration, pause, null);
    }

    public Transition(Consumer<RenderContext> itemPositioning, long duration, long pause, Part attachedTo) {
        this.itemPositioning = itemPositioning;
        this.duration = duration;
        this.pause = pause;
        this.attachedTo = attachedTo;
    }

}
