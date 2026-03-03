package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemTanCloth extends Item {

    public ItemTanCloth() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_TanCloth");
        this.setTextureName("mw:tancloth");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
