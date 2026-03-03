package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemElectronics extends Item {

    public ItemElectronics() {
        this.setMaxStackSize(32);
        this.setUnlocalizedName("mw_Electronics");
        this.setTextureName("mw:electronics");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
