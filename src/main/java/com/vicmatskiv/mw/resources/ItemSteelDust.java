package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemSteelDust extends Item {

    public ItemSteelDust() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_SteelDust");
        this.setTextureName("mw:steeldust");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
