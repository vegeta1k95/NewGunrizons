package com.gtnewhorizon.newgunrizons.client.shaders;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.Framebuffer;

import lombok.Getter;

@Getter
public class ShaderContext {

    private final float partialTicks;
    private final ShaderPhase phase;
    private final Framebuffer framebuffer;
    private final EntityRenderer renderer;

    public ShaderContext(ShaderPhase phase, EntityRenderer renderer, Framebuffer framebuffer, float partialTicks) {
        this.partialTicks = partialTicks;
        this.phase = phase;
        this.framebuffer = framebuffer;
        this.renderer = renderer;
    }
}
