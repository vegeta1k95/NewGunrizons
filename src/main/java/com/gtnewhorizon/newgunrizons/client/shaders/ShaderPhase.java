package com.gtnewhorizon.newgunrizons.client.shaders;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.ShaderGroup;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.newgunrizons.client.scope.ScopeWorldRenderer;

public interface ShaderPhase {

    ShaderPhase WORLD_RENDER = new ShaderPhase.EntityRendererBinding();
    ShaderPhase SCOPE_RENDER = new ShaderPhase.ScopeRendererBinding();
    ShaderPhase ITEM_RENDER = new ShaderPhase.ItemRenderBinding();

    void apply(ShaderContext var1, DynamicShaderGroup var2);

    void remove(ShaderContext var1, DynamicShaderGroup var2);

    class ScopeRendererBinding implements ShaderPhase {

        public void apply(ShaderContext context, DynamicShaderGroup shaderGroup) {
            EntityRenderer renderer = context.getRenderer();
            if (renderer instanceof ScopeWorldRenderer entityRenderer) {
                ShaderGroup currentShaderGroup = entityRenderer.getShaderGroup();
                if (currentShaderGroup != shaderGroup) {
                    entityRenderer.theShaderGroup = shaderGroup;
                    entityRenderer.useShader(true);
                }
            }

        }

        public void remove(ShaderContext context, DynamicShaderGroup shaderGroup) {
            EntityRenderer renderer = context.getRenderer();
            if (renderer instanceof ScopeWorldRenderer entityRenderer) {
                ShaderGroup currentShaderGroup = entityRenderer.getShaderGroup();
                if (currentShaderGroup instanceof DynamicShaderGroup) {
                    currentShaderGroup.deleteShaderGroup();
                    entityRenderer.theShaderGroup = null;
                }
            }

        }
    }

    class EntityRendererBinding implements ShaderPhase {

        public void apply(ShaderContext context, DynamicShaderGroup shaderGroup) {
            EntityRenderer renderer = context.getRenderer();
            ShaderGroup currentShaderGroup = renderer.getShaderGroup();
            if (currentShaderGroup != shaderGroup) {
                this.remove(context, null);
                renderer.theShaderGroup = shaderGroup;
            }
        }

        public void remove(ShaderContext context, DynamicShaderGroup shaderGroup) {
            EntityRenderer renderer = context.getRenderer();
            ShaderGroup currentShaderGroup = renderer.getShaderGroup();
            if (currentShaderGroup instanceof DynamicShaderGroup) {
                currentShaderGroup.deleteShaderGroup();
                renderer.theShaderGroup = null;
            }
        }
    }

    class ItemRenderBinding implements ShaderPhase {

        public void apply(ShaderContext context, DynamicShaderGroup shaderGroup) {
            if (OpenGlHelper.shadersSupported) {
                int originalMatrixMode = GL11.glGetInteger(2976);
                GL11.glMatrixMode(5890);
                GL11.glPushMatrix();
                GL11.glLoadIdentity();
                GL11.glPushAttrib(8193);
                shaderGroup.loadShaderGroup(context.getPartialTicks());
                GL11.glPopAttrib();
                GL11.glPopMatrix();
                GL11.glMatrixMode(originalMatrixMode);
            }
        }

        public void remove(ShaderContext context, DynamicShaderGroup shaderGroup) {
            shaderGroup.deleteShaderGroup();
        }
    }
}
