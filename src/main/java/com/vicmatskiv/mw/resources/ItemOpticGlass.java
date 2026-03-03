package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemOpticGlass extends Item {

    public ItemOpticGlass() {
        this.setMaxStackSize(16);
        this.setUnlocalizedName("mw_OpticGlass");
        this.setTextureName("mw:opticglass");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
