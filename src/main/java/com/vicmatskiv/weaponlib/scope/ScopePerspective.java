package com.vicmatskiv.weaponlib.scope;

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

import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

public class ScopePerspective {

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 400;

    protected ClientModContext modContext;
    protected Framebuffer framebuffer;
    protected ScopeWorldRenderer entityRenderer;
    protected EffectRenderer effectRenderer;
    protected DynamicShaderGroupManager shaderGroupManager;

    private long renderEndNanoTime = System.nanoTime();

    /** Set to {@code true} while the scope is rendering, read by MixinRenderGlobal. */
    public static boolean isRenderingScope;

    public ScopePerspective() {}

    public void activate(ClientModContext modContext, ScopeManager manager) {
        this.modContext = modContext;

        if (this.framebuffer == null) {
            this.framebuffer = new Framebuffer(DEFAULT_WIDTH, DEFAULT_HEIGHT, true);
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
        this.framebuffer.deleteFramebuffer();
        this.shaderGroupManager.removeAllShaders(
            new DynamicShaderContext(null, this.entityRenderer, null, 0.0F));
        Minecraft.getMinecraft()
            .getFramebuffer()
            .bindFramebuffer(true);
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
        return renderableState == RenderableState.ZOOMING
            || renderableState == RenderableState.ZOOMING_RECOILED
            || renderableState == RenderableState.ZOOMING_SHOOTING;
    }

    public void update(RenderTickEvent event, PlayerWeaponInstance weaponInstance) {
        this.modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.RENDER_PERSPECTIVE);
        long finishTimeNano = this.renderEndNanoTime + 16666666L;
        Minecraft mc = Minecraft.getMinecraft();

        // Save Minecraft state
        int origDisplayWidth = mc.displayWidth;
        int origDisplayHeight = mc.displayHeight;
        EntityRenderer origEntityRenderer = mc.entityRenderer;
        Framebuffer origFramebuffer = mc.framebufferMc;
        boolean origHideGUI = mc.gameSettings.hideGUI;

        // Swap to scope rendering context.
        // - framebufferMc: Angelica/Iris pipeline compositing targets the scope FBO
        // - hideGUI: suppresses Iris HandRenderer.renderSolid()/renderTranslucent()
        // Vanilla hand rendering and chunk frustum culling are handled by mixins
        // (MixinEntityRenderer and MixinRenderGlobal respectively).
        mc.framebufferMc = this.framebuffer;
        mc.displayWidth = DEFAULT_WIDTH;
        mc.displayHeight = DEFAULT_HEIGHT;
        mc.entityRenderer = this.entityRenderer;
        mc.gameSettings.hideGUI = true;

        this.framebuffer.bindFramebuffer(true);
        this.entityRenderer.updateRenderer();
        this.prepareRenderWorld(event, weaponInstance);
        isRenderingScope = true;
        try {
            this.entityRenderer.renderWorld(event.renderTickTime, finishTimeNano);
        } finally {
            isRenderingScope = false;
        }
        this.postRenderWorld(event);

        // Restore Minecraft state
        mc.gameSettings.hideGUI = origHideGUI;
        mc.framebufferMc = origFramebuffer;
        mc.entityRenderer = origEntityRenderer;
        mc.displayWidth = origDisplayWidth;
        mc.displayHeight = origDisplayHeight;
        origFramebuffer.bindFramebuffer(true);

        this.renderEndNanoTime = System.nanoTime();
        this.modContext.getSafeGlobals().renderingPhase.set(RenderingPhase.NORMAL);
    }

    private void prepareRenderWorld(RenderTickEvent event, PlayerWeaponInstance weaponInstance) {
        DynamicShaderContext shaderContext = new DynamicShaderContext(
            DynamicShaderPhase.POST_WORLD_OPTICAL_SCOPE_RENDER,
            this.entityRenderer,
            this.framebuffer,
            event.renderTickTime);
        this.shaderGroupManager.applyShader(shaderContext, weaponInstance);
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
