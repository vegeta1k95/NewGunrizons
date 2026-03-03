package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemCopperIngot extends Item {

    public ItemCopperIngot() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_CopperIngot");
        this.setTextureName("mw:copperingot");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
