package com.vicmatskiv.weaponlib.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class ExplosionSmokeFX extends EntityFX {

    private final int imageIndex;

    private static final int columnCount = 4;
    private static final int rowCount = 4;

    private static final ExplosionSmokeFX.TriFunction<Float, Integer, Integer, Float> EXPLOSION_SCALE_UPDATE_FUNCTION = (
        currentScale, ticks, maxTicks) -> {
        if (currentScale > 25.0F) {
            currentScale = currentScale * 1.0008F;
        } else if (currentScale > 20.0F) {
            currentScale = currentScale * 1.002F;
        } else if (currentScale > 15.0F) {
            currentScale = currentScale * 1.004F;
        } else if (currentScale > 10.0F) {
            currentScale = currentScale * 1.05F;
        } else {
            currentScale = currentScale * 3.0F;
        }

        return currentScale;
    };
    private static final ExplosionSmokeFX.TriFunction<Float, Integer, Integer, Float> SMOKE_GRENADE_SCALE_UPDATE_FUNCTION = (
        currentScale, ticks, maxTicks) -> {
        if (currentScale > 25.0F) {
            currentScale = currentScale * 1.0008F;
        } else if (currentScale > 20.0F) {
            currentScale = currentScale * 1.002F;
        } else if (currentScale > 15.0F) {
            currentScale = currentScale * 1.004F;
        } else if (currentScale > 5.0F) {
            currentScale = currentScale * 1.05F;
        } else {
            currentScale = currentScale * 2.0F;
        }

        return currentScale;
    };
    private static final ExplosionSmokeFX.TriFunction<Float, Integer, Integer, Float> EXPLOSION_ALPHA_UPDATE_FUNCTION = (
        currentAlpha, ticks, maxTicks) -> {
        double alphaRadians = 0.7853981633974483D
            + Math.PI * (double) ((float) ticks) / (double) ((float) maxTicks);
        return 0.3F * (float) Math.sin(Math.min(alphaRadians, Math.PI));
    };
    private static final ExplosionSmokeFX.TriFunction<Float, Integer, Integer, Float> SMOKE_GRENADE_ALPHA_UPDATE_FUNCTION = (
        currentAlpha, ticks, maxTicks) -> {
        double alphaRadians = 0.7853981633974483D
            + Math.PI * (double) ((float) ticks) / (double) ((float) maxTicks);
        return 0.3F * (float) Math.sin(Math.min(alphaRadians, Math.PI));
    };

    private final ExplosionSmokeFX.Behavior behavior;
    private final ResourceLocation smokeTexture;

    public ExplosionSmokeFX(World par1World, double positionX, double positionY, double positionZ, float scale,
        float motionX, float motionY, float motionZ, int particleMaxAge, ExplosionSmokeFX.Behavior behavior,
        ResourceLocation smokeTexture) {
        super(par1World, positionX, positionY, positionZ, 0.0D, 0.0D, 0.0D);

        this.smokeTexture = smokeTexture;

        if (motionX == 0.0F) {
            motionX = 1.0F;
        }
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;


        this.behavior = behavior;
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.particleAlpha = 0.0F;
        this.particleScale *= scale;
        this.particleMaxAge = particleMaxAge == 0 ? 50 + (int) (this.rand.nextFloat() * 30.0F) : particleMaxAge;
        this.imageIndex = this.rand.nextInt(16);
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.motionY += 1.0E-5D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.799999785423279D;
        this.motionY *= 0.9999999785423279D;
        this.motionZ *= 0.799999785423279D;
        this.particleAlpha = this.behavior.alphaUpdateFunction
            .apply(this.particleAlpha, this.particleAge, this.particleMaxAge);
        this.particleScale = this.behavior.scaleUpdateFunction
            .apply(this.particleScale, this.particleAge, this.particleMaxAge);
        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }

    }

    public void renderParticle(Tessellator tessellator, float partialTicks, float par3, float par4, float par5,
        float par6, float par7) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(this.smokeTexture);
        GL11.glPushMatrix();
        GL11.glPushAttrib(8192);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glAlphaFunc(516, 0.003921569F);
        RenderHelper.disableStandardItemLighting();
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
        float columnWidth = 0.25F;
        float rowHeight = 0.25F;
        int rowIndex = Math.floorDiv(this.imageIndex, rowCount);
        int columnIndex = this.imageIndex % columnCount;
        float aU = (float) (columnIndex + 1) * columnWidth;
        float aV = (float) (rowIndex + 1) * rowHeight;
        float bV = (float) rowIndex * rowHeight;
        float cU = (float) columnIndex * columnWidth;
        tessellator.addVertexWithUV(
            f11 - par3 * f10 - par6 * f10,
            f12 - par4 * f10,
            f13 - par5 * f10 - par7 * f10,
            aU,
            aV);
        tessellator.addVertexWithUV(
            f11 - par3 * f10 + par6 * f10,
            f12 + par4 * f10,
            f13 - par5 * f10 + par7 * f10,
            aU,
            bV);
        tessellator.addVertexWithUV(
            f11 + par3 * f10 + par6 * f10,
            f12 + par4 * f10,
            f13 + par5 * f10 + par7 * f10,
            cU,
            bV);
        tessellator.addVertexWithUV(
            f11 + par3 * f10 - par6 * f10,
            f12 - par4 * f10,
            f13 + par5 * f10 - par7 * f10,
            cU,
            aV);
        tessellator.draw();
        RenderHelper.enableStandardItemLighting();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public int getFXLayer() {
        return 3;
    }

    public enum Behavior {

        EXPLOSION(ExplosionSmokeFX.EXPLOSION_SCALE_UPDATE_FUNCTION, ExplosionSmokeFX.EXPLOSION_ALPHA_UPDATE_FUNCTION),
        SMOKE_GRENADE(ExplosionSmokeFX.SMOKE_GRENADE_SCALE_UPDATE_FUNCTION,
            ExplosionSmokeFX.SMOKE_GRENADE_ALPHA_UPDATE_FUNCTION);

        private final ExplosionSmokeFX.TriFunction<Float, Integer, Integer, Float> scaleUpdateFunction;
        private final ExplosionSmokeFX.TriFunction<Float, Integer, Integer, Float> alphaUpdateFunction;

        Behavior(ExplosionSmokeFX.TriFunction<Float, Integer, Integer, Float> scaleUpdateFunction,
                 ExplosionSmokeFX.TriFunction<Float, Integer, Integer, Float> alphaUpdateFunction) {
            this.scaleUpdateFunction = scaleUpdateFunction;
            this.alphaUpdateFunction = alphaUpdateFunction;
        }
    }

    public interface TriFunction<T, U, V, R> {
        R apply(T var1, U var2, V var3);
    }
}
