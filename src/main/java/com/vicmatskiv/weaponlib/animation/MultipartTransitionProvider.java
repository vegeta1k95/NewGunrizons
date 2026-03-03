package com.vicmatskiv.weaponlib.animation;

import java.util.List;

import com.vicmatskiv.weaponlib.RenderableState;

public interface MultipartTransitionProvider {

    List<MultipartTransition> getPositioning(RenderableState var1);
}
