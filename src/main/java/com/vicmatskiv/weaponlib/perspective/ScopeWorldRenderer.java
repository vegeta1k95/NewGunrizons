package com.vicmatskiv.weaponlib.perspective;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.shader.DynamicShaderGroup;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Custom EntityRenderer subclass for scope perspective rendering.
 * <p>
 * The only behavioral differences from vanilla EntityRenderer:
 * <ul>
 *   <li>Hand rendering is skipped in {@link #renderWorld} (the scope framebuffer
 *       should show the world without the player's hand)</li>
 *   <li>Post-processing shaders can be conditionally applied via {@link #useShader}</li>
 * </ul>
 * All other rendering behavior (terrain, entities, particles, weather, fog, camera,
 * lighting) is inherited from EntityRenderer via access-transformer-exposed methods.
 */
@SideOnly(Side.CLIENT)
public class ScopeWorldRenderer extends EntityRenderer {

    private boolean useShader;

    public ScopeWorldRenderer(Minecraft minecraft, IResourceManager resourceManager) {
        super(minecraft, resourceManager);
    }

    /**
     * Renders the world without the first-person hand, then conditionally applies
     * post-processing shaders. This is called by {@link ScopePerspective} to render
     * the scene into the scope's framebuffer.
     * <p>
     * Delegates to {@code super.renderWorld()} so that any Mixin modifications from
     * other mods (e.g. Angelica) are preserved. Hand rendering is suppressed by
     * overriding {@link #renderHand} as a no-op.
     */
    @Override
    public void renderWorld(float partialTicks, long finishTimeNano) {
        super.renderWorld(partialTicks, finishTimeNano);
        applyShaderIfNeeded(partialTicks);
    }

    /**
     * No-op override — the scope framebuffer should not contain the player's
     * hand/weapon model. Made accessible via Access Transformer (mw_at.cfg).
     */
    @Override
    public void renderHand(float partialTicks, int pass) {
        // Intentionally empty — scope perspective should not render the hand.
    }

    private void applyShaderIfNeeded(float partialTicks) {
        if (OpenGlHelper.shadersSupported) {
            if (this.theShaderGroup != null && this.useShader) {
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glPushMatrix();
                GL11.glLoadIdentity();
                this.theShaderGroup.loadShaderGroup(partialTicks);
                GL11.glPopMatrix();
            }
            Minecraft.getMinecraft()
                .getFramebuffer()
                .bindFramebuffer(true);
        }
    }

    public void setPrepareTerrain(boolean b) {}

    public void setShaderGroup(DynamicShaderGroup shaderGroup) {
        this.theShaderGroup = shaderGroup;
    }

    public void useShader(boolean useShader) {
        this.useShader = useShader;
    }
}
