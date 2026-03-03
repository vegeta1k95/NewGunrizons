package com.vicmatskiv.weaponlib.perspective;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.PlayerItemInstance;

public class PerspectiveManager {

    private ScopePerspective currentPerspective;
    private final ClientModContext clientModContext;
    private ScopeWorldRenderer entityRenderer;
    private EffectRenderer effectRenderer;

    public PerspectiveManager(ClientModContext clientModContext) {
        this.clientModContext = clientModContext;
    }

    public ScopePerspective getPerspective(PlayerItemInstance<?> currentInstance, boolean init) {
        if (currentInstance != null && (this.currentPerspective != null || init)) {
            boolean needsPerspective = currentInstance.needsOpticalScopePerspective();
            if (needsPerspective) {
                if (this.currentPerspective == null) {
                    this.currentPerspective = new ScopePerspective();
                    this.currentPerspective.activate(this.clientModContext, this);
                }
            } else if (this.currentPerspective != null && init) {
                this.currentPerspective.deactivate();
                this.currentPerspective = null;
            }

            return this.currentPerspective;
        } else {
            return null;
        }
    }

    public ScopeWorldRenderer getEntityRenderer() {
        if (entityRenderer == null) {
            entityRenderer = new ScopeWorldRenderer(
                Minecraft.getMinecraft(),
                Minecraft.getMinecraft()
                    .getResourceManager());
        }
        return entityRenderer;
    }

    public EffectRenderer getEffectRenderer() {
        if (effectRenderer == null) {
            effectRenderer = new EffectRenderer(
                Minecraft.getMinecraft().thePlayer.worldObj,
                Minecraft.getMinecraft()
                    .getTextureManager());
        }

        return effectRenderer;
    }
}
