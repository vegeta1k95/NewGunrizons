package com.vicmatskiv.weaponlib;

import java.util.Random;
import java.util.function.BiConsumer;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

public class LaserBeamRenderer implements CustomRenderer {

    private final float xOffset = 0.5F;
    private final float yOffset = -1.3F;
    private final float zOffset = -1.7F;

    private final BiConsumer<EntityLivingBase, ItemStack> positioning;

    public LaserBeamRenderer(BiConsumer<EntityLivingBase, ItemStack> positioning) {
        this.positioning = positioning;
    }

    public void render(RenderContext renderContext) {
        PlayerItemInstance<?> instance = renderContext.getPlayerItemInstance();
        TransformType type = renderContext.getTransformType();
        if (instance instanceof PlayerWeaponInstance && ((PlayerWeaponInstance) instance).isLaserOn()
            && (type == TransformType.THIRD_PERSON_LEFT_HAND || type == TransformType.THIRD_PERSON_RIGHT_HAND
                || type == TransformType.FIRST_PERSON_LEFT_HAND
                || type == TransformType.FIRST_PERSON_RIGHT_HAND
                || type == TransformType.GROUND)) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(1048575);
            GL11.glDisable(2884);
            GL11.glDisable(2896);
            GL11.glDisable(3553);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.5F);
            GL11.glLineWidth(1.5F);
            GL11.glDepthMask(false);
            if (this.positioning != null) {
                this.positioning.accept(renderContext.getPlayer(), renderContext.getWeapon());
            }

            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawing(1);
            long time = System.currentTimeMillis();
            Random random = new Random(time - time % 300L);
            float start = this.zOffset;
            float length = 100.0F;
            float end = 0.0F;

            for (int i = 0; i < 100 && start < length && end < length; ++i) {
                tessellator.addVertex(this.xOffset, this.yOffset, start);
                tessellator.setBrightness(200);
                end = start - (1.0F + random.nextFloat() * 2.0F);
                if (end > length) {
                    end = length;
                }
                tessellator.addVertex(this.xOffset, this.yOffset, end);
                start = end + random.nextFloat() * 0.5F;
            }

            tessellator.draw();
            GL11.glDepthMask(true);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

    }
}
