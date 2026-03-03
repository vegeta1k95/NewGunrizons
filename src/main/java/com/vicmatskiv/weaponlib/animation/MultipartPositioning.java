package com.vicmatskiv.weaponlib.animation;

import java.util.Queue;

import com.vicmatskiv.weaponlib.Part;
import com.vicmatskiv.weaponlib.RenderContext;

public interface MultipartPositioning {

    <T> T getFromState(Class<T> var1);

    <T> T getToState(Class<T> var1);

    boolean isExpired(Queue<MultipartPositioning> var1);

    MultipartPositioning.Positioner getPositioner();

    float getProgress();

    interface Positioner {

        void position(Part var1, RenderContext var2);

        default void applySway(float rate, float amplitude) {}
    }
}
