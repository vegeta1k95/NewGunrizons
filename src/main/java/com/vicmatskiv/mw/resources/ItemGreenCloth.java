package com.vicmatskiv.mw.resources;

import net.minecraft.item.Item;

import com.vicmatskiv.mw.ModernWarfareMod;

public class ItemGreenCloth extends Item {

    public ItemGreenCloth() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("mw_GreenCloth");
        this.setTextureName("mw:greencloth");
        this.setCreativeTab(ModernWarfareMod.gunsTab);
    }
}
