package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemSulfurDust extends Item {

    public ItemSulfurDust() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_SulfurDust");
        this.setTextureName("mw:sulfurdust");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
