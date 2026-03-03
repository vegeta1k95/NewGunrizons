package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemCloth extends Item {

    public ItemCloth() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_Cloth");
        this.setTextureName("mw:cloth");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
