package com.gtnewhorizon.newgunrizons.client.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.client.particle.ParticleManager;
import com.gtnewhorizon.newgunrizons.entities.EntityBullet;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;

public class EntityBulletRenderer extends Render {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(
        NewGunrizonsMod.MODID, "textures/effect/tracer.png");

    private static final Map<String, ResourceLocation> textureCache = new HashMap<>();

    private static final int LERP_TICKS = 2;

    private static ResourceLocation getTracerTexture(ItemWeapon weapon) {
        String name = weapon.getTracerTexture();
        return textureCache.computeIfAbsent(name,
            n -> new ResourceLocation(NewGunrizonsMod.MODID, "textures/effect/" + n + ".png"));
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        EntityBullet bullet = (EntityBullet) entity;
        ItemWeapon weapon = bullet.getWeapon();
        if (weapon == null) return;

        float tracerWidth = weapon.getTracerWidth();
        float tracerLength = weapon.getTracerLength();

        double renderX = x;
        double renderY = y;
        double renderZ = z;

        // Origin correction: lerp from firing point to entity position.
        // Firing point is in world-space, x/y/z is in camera-relative space.
        // Convert firing point to camera-relative by subtracting camera position.
        Minecraft mc = Minecraft.getMinecraft();
        if (bullet.getThrower() == mc.thePlayer
            && mc.gameSettings.thirdPersonView == 0
            && ParticleManager.hasFiringPointPosition()
            && bullet.ticksExisted <= LERP_TICKS) {

            float t = (bullet.ticksExisted + partialTicks) / (LERP_TICKS + 1.0F);
            t = Math.min(t, 1.0F);

            // Convert firing point from world-space to camera-relative
            // by subtracting the camera (render view entity) position
            Entity view = mc.renderViewEntity;
            double camX = view.lastTickPosX + (view.posX - view.lastTickPosX) * partialTicks;
            double camY = view.lastTickPosY + (view.posY - view.lastTickPosY) * partialTicks;
            double camZ = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * partialTicks;

            double fpX = ParticleManager.getLastFiringPointX() - camX;
            double fpY = ParticleManager.getLastFiringPointY() - camY;
            double fpZ = ParticleManager.getLastFiringPointZ() - camZ;

            renderX = fpX + (x - fpX) * t;
            renderY = fpY + (y - fpY) * t;
            renderZ = fpZ + (z - fpZ) * t;
        }

        this.bindTexture(getTracerTexture(weapon));
        GL11.glPushMatrix();
        GL11.glTranslated(renderX, renderY, renderZ);

        // Vanilla arrow rotation convention
        GL11.glRotatef(
            bullet.prevRotationYaw + (bullet.rotationYaw - bullet.prevRotationYaw) * partialTicks - 90.0F,
            0.0F, 1.0F, 0.0F);
        GL11.glRotatef(
            bullet.prevRotationPitch + (bullet.rotationPitch - bullet.prevRotationPitch) * partialTicks,
            0.0F, 0.0F, 1.0F);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glDepthMask(false);

        Tessellator tess = Tessellator.instance;

        // Horizontal quad along +X
        tess.startDrawingQuads();
        tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        tess.setBrightness(240);
        tess.addVertexWithUV(tracerLength, 0, -tracerWidth, 0, 0);
        tess.addVertexWithUV(0, 0, -tracerWidth, 1, 0);
        tess.addVertexWithUV(0, 0, tracerWidth, 1, 1);
        tess.addVertexWithUV(tracerLength, 0, tracerWidth, 0, 1);
        tess.draw();

        // Vertical quad along +X
        tess.startDrawingQuads();
        tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        tess.setBrightness(240);
        tess.addVertexWithUV(tracerLength, -tracerWidth, 0, 0, 0);
        tess.addVertexWithUV(0, -tracerWidth, 0, 1, 0);
        tess.addVertexWithUV(0, tracerWidth, 0, 1, 1);
        tess.addVertexWithUV(tracerLength, tracerWidth, 0, 0, 1);
        tess.draw();

        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return DEFAULT_TEXTURE;
    }
}
