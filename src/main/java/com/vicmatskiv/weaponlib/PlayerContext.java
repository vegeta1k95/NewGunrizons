package com.vicmatskiv.weaponlib;

import net.minecraft.entity.EntityLivingBase;

public interface PlayerContext {

    EntityLivingBase getPlayer();

    void setPlayer(EntityLivingBase var1);
}
