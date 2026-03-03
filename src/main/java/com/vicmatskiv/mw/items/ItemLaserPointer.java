package com.vicmatskiv.mw.items;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemLaserPointer extends Item {

    public ItemLaserPointer() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_LaserPointer");
        this.setTextureName("mw:laserpointer");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
