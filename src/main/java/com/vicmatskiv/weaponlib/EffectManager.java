package com.vicmatskiv.weaponlib;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import com.vicmatskiv.weaponlib.particle.ExplosionSmokeFX;

public interface EffectManager {

    void spawnSmokeParticle(EntityLivingBase var1, float var2, float var3);

    void spawnFlashParticle(EntityLivingBase var1, float var2, float var3, float var4, float var5);

    void spawnExplosionSmoke(double var1, double var3, double var5, double var7, double var9, double var11, float var13,
        int var14, ExplosionSmokeFX.Behavior var15, ResourceLocation var16);

    void spawnExplosionParticle(double var1, double var3, double var5, double var7, double var9, double var11,
        float var13, int var14);
}
