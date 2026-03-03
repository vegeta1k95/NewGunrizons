package com.vicmatskiv.weaponlib.perspective;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.Framebuffer;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.RenderContext;
import com.vicmatskiv.weaponlib.RenderableState;
import com.vicmatskiv.weaponlib.RenderingPhase;
import com.vicmatskiv.weaponlib.shader.DynamicShaderContext;
import com.vicmatskiv.weaponlib.shader.DynamicShaderGroupManager;
import com.vicmatskiv.weaponlib.shader.DynamicShaderPhase;
import com.vicmatskiv.weaponlib.shader.Framebuffers;

import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

public class ScopePerspective {

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 400;

    protected ClientModContext modContext;
    protected Framebuffer framebuffer;
    protected int width;
    protected int height;
    protected ScopeWorldRenderer entityRenderer;
    protected EffectRenderer effectRenderer;
    protected DynamicShaderGroupManager shaderGroupManager;

    private long renderEndNanoTime = System.nanoTime();

    public ScopePerspective() {
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
    }

    public void activate(ClientModContext modContext, PerspectiveManager manager) {
        this.modContext = modContext;

        if (this.framebuffer == null) {
            this.framebuffer = new Framebuffer(this.width, this.height, true);
            this.framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        }

        this.entityRenderer = manager.getEntityRenderer();
        this.effectRenderer = manager.getEffectRenderer();
        this.shaderGroupManager = new DynamicShaderGroupManager();

        if (this.shaderGroupManager.hasActiveGroups()) {
            System.err.println("!!! Active shader groups found !!!");
        }
    }

    public void deactivate() {
        int originalFramebufferId = Framebuffers.getCurrentFramebuffer();
        this.framebuffer.deleteFramebuffer();
        this.shaderGroupManager.removeAllShaders(
            new DynamicShaderContext(null, this.entityRenderer, null, 0.0F));
        Minecraft mc = Minecraft.getMinecraft();
        Framebuffers.bindFramebuffer(
            originalFramebufferId,
            true,
            mc.getFramebuffer().framebufferWidth,
            mc.getFramebuffer().framebufferHeight);
    }

    public float getBrightness(RenderContext renderContext) {
        float brightness = 0.0F;
        PlayerWeaponInstance instance = renderContext.getWeaponInstance();
        if (instance == null) {
            return 0.0F;
        } else {
            boolean aimed = instance.isAimed();
            float progress = Math.min(1.0F, renderContext.getTransitionProgress());
            if (isAimingState(renderContext.getFromState()) && isAimingState(renderContext.getToState())) {
                brightness = 1.0F;
            } else if (progress > 0.0F && aimed && isAimingState(renderContext.getToState())) {
                brightness = progress;
            } else if (isAimingState(renderContext.getFromState()) && progress > 0.0F && !aimed) {
                brightness = Math.max(1.0F - progress, 0.0F);
            }

            return brightness;
        }
    }

    public int getTexture() {
        return this.framebuffer != null ? this.framebuffer.framebufferTexture : -1;
    }

    private static boolean isAimingState(RenderableState renderableState) {
        return renderableState == RenderableState.ZOOMING || renderableState == RenderableState.ZOOMING_RECOILED
            || renderableState == RenderableState.ZOOMING_SHOOTING;
    }

    public void update(RenderTickEvent event) {
        PlayerWeaponInstance instance = this.modContext.getMainHeldWeapon();
        if (instance != null && instance.isAimed()) {
            this.modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.RENDER_PERSPECTIVE);
            long finishTimeNano = this.renderEndNanoTime + 16666666L;
            int origDisplayWidth = Minecraft.getMinecraft().displayWidth;
            int origDisplayHeight = Minecraft.getMinecraft().displayHeight;
            EntityRenderer origEntityRenderer = Minecraft.getMinecraft().entityRenderer;
            this.framebuffer.bindFramebuffer(true);
            Minecraft.getMinecraft().displayWidth = this.width;
            Minecraft.getMinecraft().displayHeight = this.height;
            Minecraft.getMinecraft().entityRenderer = this.entityRenderer;
            this.entityRenderer.setPrepareTerrain(false);
            this.entityRenderer.updateRenderer();
            this.prepareRenderWorld(event);
            this.entityRenderer.renderWorld(event.renderTickTime, finishTimeNano);
            this.postRenderWorld(event);
            Minecraft.getMinecraft().entityRenderer = origEntityRenderer;
            Minecraft.getMinecraft()
                .getFramebuffer()
                .bindFramebuffer(true);
            Minecraft.getMinecraft().displayWidth = origDisplayWidth;
            Minecraft.getMinecraft().displayHeight = origDisplayHeight;
            this.renderEndNanoTime = System.nanoTime();
            this.modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.NORMAL);
        }
    }

    private void prepareRenderWorld(RenderTickEvent event) {
        DynamicShaderContext shaderContext = new DynamicShaderContext(
            DynamicShaderPhase.POST_WORLD_OPTICAL_SCOPE_RENDER,
            this.entityRenderer,
            this.framebuffer,
            event.renderTickTime);
        PlayerWeaponInstance instance = this.modContext.getMainHeldWeapon();
        this.shaderGroupManager.applyShader(shaderContext, instance);
    }

    private void postRenderWorld(RenderTickEvent event) {
        DynamicShaderContext shaderContext = new DynamicShaderContext(
            DynamicShaderPhase.POST_WORLD_OPTICAL_SCOPE_RENDER,
            this.entityRenderer,
            this.framebuffer,
            event.renderTickTime);
        this.shaderGroupManager.removeStaleShaders(shaderContext);
    }
}
