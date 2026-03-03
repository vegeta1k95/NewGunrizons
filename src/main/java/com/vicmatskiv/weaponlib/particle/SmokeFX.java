package com.vicmatskiv.weaponlib.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class SmokeFX extends EntityFX {

    private static final double SMOKE_SCALE_FACTOR = 1.0005988079071D;
    private static final String SMOKE_TEXTURE = "weaponlib:/com/vicmatskiv/weaponlib/resources/smokes.png";
    private final int imageIndex;
    private static final int imagesPerRow = 4;

    public SmokeFX(World par1World, double positionX, double positionY, double positionZ, float scale, float motionX,
        float motionY, float motionZ) {
        super(par1World, positionX, positionY, positionZ, 0.0D, 0.0D, 0.0D);

        if (motionX == 0.0F) {
            motionX = 1.0F;
        }

        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;

        this.particleTextureIndexX = 0;
        this.particleTextureIndexY = 0;
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.particleAlpha = 0.0F;
        this.particleScale *= scale;
        this.particleMaxAge = 50 + (int) (this.rand.nextFloat() * 30.0F);
        this.imageIndex = this.rand.nextInt() % imagesPerRow;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.motionY += 5.0E-4D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.599999785423279D;
        this.motionY *= 0.9999999785423279D;
        this.motionZ *= 0.599999785423279D;
        double alphaRadians = 0.7853981633974483D
            + Math.PI * (double) ((float) this.particleAge) / (double) ((float) this.particleMaxAge);
        this.particleAlpha = 0.2F * (float) Math.sin(Math.min(alphaRadians, Math.PI));
        this.particleScale = (float) ((double) this.particleScale * 1.0005988079071D);
        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }

    }

    public void renderParticle(Tessellator tessellator, float partialTicks, float par3, float par4, float par5,
        float par6, float par7) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(new ResourceLocation(SMOKE_TEXTURE));
        GL11.glPushMatrix();
        GL11.glPushAttrib(8192);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glAlphaFunc(516, 0.003921569F);
        tessellator.startDrawing(7);
        float f10 = 0.1F * this.particleScale;
        float f11 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float f12 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float f13 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
        tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & '\uffff';
        int k = i & '\uffff';
        tessellator.setBrightness(200);
        RenderHelper.disableStandardItemLighting();
        float uWidth = 0.25F;
        float aU = (float) (this.imageIndex + 1) * uWidth;
        float aV = 1.0F;
        float bU = (float) (this.imageIndex + 1) * uWidth;
        float bV = 0.0F;
        float cU = (float) this.imageIndex * uWidth;
        float cV = 0.0F;
        float dU = (float) this.imageIndex * uWidth;
        float dV = 1.0F;
        tessellator
            .addVertexWithUV(f11 - par3 * f10 - par6 * f10, f12 - par4 * f10, f13 - par5 * f10 - par7 * f10, aU, aV);
        tessellator
            .addVertexWithUV(f11 - par3 * f10 + par6 * f10, f12 + par4 * f10, f13 - par5 * f10 + par7 * f10, bU, bV);
        tessellator
            .addVertexWithUV(f11 + par3 * f10 + par6 * f10, f12 + par4 * f10, f13 + par5 * f10 + par7 * f10, cU, cV);
        tessellator
            .addVertexWithUV(f11 + par3 * f10 - par6 * f10, f12 - par4 * f10, f13 + par5 * f10 - par7 * f10, dU, dV);
        tessellator.draw();
        RenderHelper.enableStandardItemLighting();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public int getFXLayer() {
        return 3;
    }
}
