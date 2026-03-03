package com.vicmatskiv.weaponlib.perspective;

import java.util.function.BiConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.CustomRenderer;
import com.vicmatskiv.weaponlib.RenderContext;

import com.vicmatskiv.weaponlib.TransformType;
import com.vicmatskiv.weaponlib.ViewfinderModel;

public class PerspectiveRenderer implements CustomRenderer {

    private final ViewfinderModel model = new ViewfinderModel();
    private final BiConsumer<EntityLivingBase, ItemStack> positioning;

    public PerspectiveRenderer(BiConsumer<EntityLivingBase, ItemStack> positioning) {
        this.positioning = positioning;
    }

    public void render(RenderContext renderContext) {

        if (renderContext.getTransformType() != TransformType.FIRST_PERSON_RIGHT_HAND
            && renderContext.getTransformType() != TransformType.FIRST_PERSON_LEFT_HAND) {
            return;
        }
        if (renderContext.getModContext() == null) {
            return;
        }

        ClientModContext clientModContext = (ClientModContext) renderContext.getModContext();
        ScopePerspective perspective = clientModContext.getViewManager()
            .getPerspective(renderContext.getPlayerItemInstance(), false);

        if (perspective == null) {
            return;
        }

        float brightness = perspective.getBrightness(renderContext);
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
        this.positioning.accept(renderContext.getPlayer(), renderContext.getWeapon());
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, perspective.getTexture());
        Minecraft.getMinecraft().entityRenderer.disableLightmap(0.0D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(brightness, brightness, brightness, 1.0F);
        this.model.render(
            renderContext.getPlayer(),
            renderContext.getLimbSwing(),
            renderContext.getFlimbSwingAmount(),
            renderContext.getAgeInTicks(),
            renderContext.getNetHeadYaw(),
            renderContext.getHeadPitch(),
            renderContext.getScale());
        Minecraft.getMinecraft().entityRenderer.enableLightmap(0.0D);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
