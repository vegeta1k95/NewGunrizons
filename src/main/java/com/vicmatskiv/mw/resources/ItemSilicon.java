package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemSilicon extends Item {

    public ItemSilicon() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_Silicon");
        this.setTextureName("mw:silicon");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
